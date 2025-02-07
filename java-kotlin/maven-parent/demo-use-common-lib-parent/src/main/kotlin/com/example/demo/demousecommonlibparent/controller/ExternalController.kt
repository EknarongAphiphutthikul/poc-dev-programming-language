package com.example.demo.demousecommonlibparent.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.TimeUnit

@RestController
@RequestMapping("/external")
class ExternalController {

    @GetMapping("/hello")
    fun hello(@RequestParam timeSleep: Long): String {
        TimeUnit.SECONDS.sleep(timeSleep)
        return "Hello from external controller"
    }
}