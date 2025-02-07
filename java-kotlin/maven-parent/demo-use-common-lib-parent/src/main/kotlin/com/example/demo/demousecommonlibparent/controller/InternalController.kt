package com.example.demo.demousecommonlibparent.controller

import org.springframework.web.bind.annotation.*
import java.util.concurrent.TimeUnit

@RestController
@RequestMapping("/internal")
class InternalController {

    @GetMapping("/hello")
    fun hello(@RequestParam timeSleep: Long): String {
        TimeUnit.SECONDS.sleep(timeSleep)
        return "Hello from internal controller"
    }
}