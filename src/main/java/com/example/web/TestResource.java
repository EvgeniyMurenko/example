package com.example.web;

import com.example.service.TestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/myResource")
@RequiredArgsConstructor
@Slf4j
public class TestResource {

    private final TestService testService;

    @PostMapping
    public void getList(@RequestBody List<String> list) {
        testService.getAll(list);
    }

    @GetMapping
    public void getList() {

    }
}
