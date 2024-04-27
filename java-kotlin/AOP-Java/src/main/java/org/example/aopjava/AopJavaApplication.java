package org.example.aopjava;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.*;

@SpringBootApplication
// proxyTargetClass=true is used to force CGLIB proxying
@EnableAspectJAutoProxy(proxyTargetClass=true)
public class AopJavaApplication {

    public static void main(String[] args) {
        SpringApplication.run(AopJavaApplication.class, args);
    }

}
