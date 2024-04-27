package org.example.aopjava.service;

import lombok.extern.slf4j.*;
import org.springframework.stereotype.*;

@Slf4j
@Service
public class ExampleServiceImpl implements ExampleService {

    @Override
    public String exampleMethod() {
        log.info("exampleMethod called");
        return "Hello from exampleMethod";
    }
}
