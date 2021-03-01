package com.example.service;

import com.example.domain.RedisKeyConstants;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.redis.RedisConstants;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.processor.aggregate.AggregateController;
import org.apache.camel.processor.aggregate.DefaultAggregateController;
import org.apache.camel.util.URISupport;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CamelService {

    private final CamelContext context;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @PostConstruct
    public void init () throws Exception {

        AggregateController controller = new DefaultAggregateController();

        context.getRegistry().bind("redisTemplate", redisTemplate);
        context.getRegistry().bind("serializer", redisTemplate.getStringSerializer());

        context.addRoutes(new RouteBuilder() {
            public void configure() {
                from("scheduler:" + RedisKeyConstants.NOTIFICATION_EVENT_QUEUE
                        + "?greedy=true"
                        + "&useFixedDelay=false"
                        + "&delay=1000")
                        .routeId("notification-event-consumer")
                        // configure spring-redis to pop events from the event-queue
                        .setHeader(RedisConstants.COMMAND, constant("BRPOP"))
                        .setHeader(RedisConstants.KEY, constant(RedisKeyConstants.NOTIFICATION_EVENT_QUEUE))
                        .setHeader(RedisConstants.TIMEOUT, constant(1L))
                        .to("spring-redis://localhost:6379?redisTemplate=#redisTemplate&serializer=#serializer")
                        // we maintain n different threads that each consume at most 1 event and block for a second
                        // on not receiving anything. On not receiving anything for at least one second, the
                        // component will return an empty exchange which we don't want to process
                        .choice()
                        .when(body().isNotNull())
                        .aggregate(constant(true), new ArrayListAggregationStrategy())
                        .completionTimeout(5000)
                        .completionSize(5)
                        .removeHeaders("*")
                        .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                        .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                        .marshal().json(JsonLibrary.Jackson)
                        .doTry()
                        .to("http://localhost:8099/myResource1")
                        .endDoTry()
                        .doCatch(Exception.class)
                        .removeHeaders("*")
                        .process(new Processor() {
                            public void process(Exchange exchange) throws Exception {
                                Message in = exchange.getIn();
                                String inBody = in.getBody(String.class);
                                inBody = inBody.replace("[\"", "")
                                        .replace("\"", "")
                                        .replace("]", "");
                                in.setBody(inBody);
                                exchange.setIn(in);
                            }
                        })
                        .split(body(), ",")
                        .setHeader(RedisConstants.COMMAND, constant("RPUSH"))
                        .setHeader(RedisConstants.KEY, constant("err:"+RedisKeyConstants.NOTIFICATION_EVENT_QUEUE))
                        .setHeader(RedisConstants.VALUE, simple("${body}"))
                        .setHeader(RedisConstants.TIMEOUT, constant(1L))
                        .to("spring-redis://localhost:6379?redisTemplate=#redisTemplate&serializer=#serializer")
                        .log(LoggingLevel.ERROR, "Fail to send list " + RedisKeyConstants.NOTIFICATION_EVENT_QUEUE + " with payload ${body}");

            }
        });
    }
}
