package com.example.demo.demousecommonlibparent.controller

import com.example.demo.demousecommonlibparent.bucket4j.Bucket4jResponse
import com.giffing.bucket4j.spring.boot.starter.context.RateLimiting
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.util.concurrent.TimeUnit

@RestController
@RequestMapping("/internal")
class InternalController {

    @Autowired
    lateinit var bucket4jResponse: Bucket4jResponse

    @RateLimiting(name = "api-internal",
            cacheKey = "'hello' + #userId",
            fallbackMethodName = "myFallbackMethod"
    )
    @GetMapping("/hello")
    fun hello(request: HttpServletRequest, @RequestParam timeSleep: Long, @RequestParam("user-id") userId: String): String {
        TimeUnit.SECONDS.sleep(timeSleep)
        return "Hello from internal controller + $userId"
    }

    fun myFallbackMethod(request: HttpServletRequest, timeSleep: Long, userId: String): String {
        return bucket4jResponse.getResponse(request)
    }
}