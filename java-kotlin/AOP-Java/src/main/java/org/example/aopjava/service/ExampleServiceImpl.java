package org.example.aopjava.service;

import lombok.extern.slf4j.*;
import org.springframework.scheduling.annotation.*;
import org.springframework.stereotype.*;

@Slf4j
@Service
public class ExampleServiceImpl implements ExampleService {

    @Override
    public String exampleMethod() {
        log.info("exampleMethod called");
        return "Hello from exampleMethod";
    }

    @Override
    @Async("threadPoolTaskExecutor")
    public void exampleMethodAsync() {
        log.info("exampleMethodAsync sleep 4 seconds");
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            log.error("Error in exampleMethodAsync", e);
        }
        log.info("exampleMethodAsync called");
    }
}
