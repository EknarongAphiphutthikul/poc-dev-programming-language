package org.example.aopjava.aspect;

import lombok.extern.slf4j.*;
import org.aspectj.lang.annotation.*;
import org.example.aopjava.service.*;
import org.springframework.stereotype.*;

@Aspect
@Slf4j
@Component
public class ExampleAspect {

    private final ExampleService exampleService;

    public ExampleAspect(ExampleService exampleService) {
        this.exampleService = exampleService;
    }

    //* error:
    /*
    @Before("execution(* org.example.aopjava.service.ExampleService.exampleMethod())")
    public void beforeExampleMethodCallService() {
        log.info("Before exampleMethod called");
        exampleService.exampleMethod();
    }
     */

    @Before("execution(* org.example.aopjava.service.ExampleService.exampleMethod())")
    public void beforeExampleMethod() {
        log.info("Before exampleMethod called");
    }

    @After("execution(* org.example.aopjava.service.ExampleService.exampleMethod())")
    public void afterExampleMethod() {
       log.info("After exampleMethod called");
    }
}
