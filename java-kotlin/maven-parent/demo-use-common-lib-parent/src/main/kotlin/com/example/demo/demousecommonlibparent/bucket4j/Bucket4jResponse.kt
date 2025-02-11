package com.example.demo.demousecommonlibparent.bucket4j

import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Component

@Component
class Bucket4jResponse {
    fun getResponse(request: HttpServletRequest): String {
        return "Response from Bucket4jResponse"
    }
}