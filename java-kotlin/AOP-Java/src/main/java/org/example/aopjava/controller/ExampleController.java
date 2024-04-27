package org.example.aopjava.controller;

import org.example.aopjava.service.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/aop/")
public class ExampleController {

    private final ExampleService exampleService;

    public ExampleController(ExampleService exampleService) {
        this.exampleService = exampleService;
    }

    @GetMapping("example")
    public String exampleMethodController() {
        return exampleService.exampleMethod();
    }
}
