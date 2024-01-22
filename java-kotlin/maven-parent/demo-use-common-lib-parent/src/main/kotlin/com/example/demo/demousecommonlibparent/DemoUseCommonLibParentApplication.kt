package com.example.demo.demousecommonlibparent

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan(
    "com.example.demo.demousecommonlibparent",
    "com.example.commonlib.poccommonlib.resource",
)
class DemoUseCommonLibParentApplication

fun main(args: Array<String>) {
    runApplication<DemoUseCommonLibParentApplication>(*args)
}
