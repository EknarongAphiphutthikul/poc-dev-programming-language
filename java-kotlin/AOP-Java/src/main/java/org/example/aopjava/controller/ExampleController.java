package org.example.aopjava.controller;

import lombok.extern.slf4j.*;
import org.example.aopjava.service.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/aop/")
@Slf4j
public class ExampleController {

    private final ExampleService exampleService;

    public ExampleController(ExampleService exampleService) {
        this.exampleService = exampleService;
    }

    @GetMapping("example")
    public String exampleMethodController() {
        return exampleService.exampleMethod();
    }

    @GetMapping("async")
    public String exampleMethodAsyncController() {
        exampleService.exampleMethodAsync();
        log.info("exampleMethodAsyncController called");
        return "Async method called";
    }
}
