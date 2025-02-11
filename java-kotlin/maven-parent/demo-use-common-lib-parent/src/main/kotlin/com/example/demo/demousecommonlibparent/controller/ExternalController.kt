package com.example.demo.demousecommonlibparent.controller

import com.example.demo.demousecommonlibparent.bucket4j.Bucket4jResponse
import com.giffing.bucket4j.spring.boot.starter.context.RateLimiting
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.TimeUnit

@RestController
@RequestMapping("/external")
class ExternalController {

    @Autowired
    lateinit var bucket4jResponse: Bucket4jResponse

    // cacheKey = "#timeSleep" is used to define the key to be used to cache the request and must define on parameter of the method
    // fallbackMethodName = "myFallbackMethod" is used to define the fallback method to be called when the rate limit is exceeded
    //            and must define the fallback method in the same controller class
    @RateLimiting(name = "api-external",
            cacheKey = "#timeSleep",
            fallbackMethodName = "myFallbackMethod"
            )
    @GetMapping("/hello")
    fun hello(request: HttpServletRequest, @RequestParam timeSleep: Long): String {
        TimeUnit.SECONDS.sleep(timeSleep)
        return "Hello from external controller"
    }

    // must define the fallback method with the same signature as the method to be rate limited : (request: HttpServletRequest, timeSleep: Long)
    fun myFallbackMethod(request: HttpServletRequest, timeSleep: Long): String {
        return bucket4jResponse.getResponse(request)
    }
}