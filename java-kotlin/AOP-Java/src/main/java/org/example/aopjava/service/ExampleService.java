package org.example.aopjava.service;

import org.springframework.scheduling.annotation.*;

public interface ExampleService {
    String exampleMethod();
    void exampleMethodAsync();
}
