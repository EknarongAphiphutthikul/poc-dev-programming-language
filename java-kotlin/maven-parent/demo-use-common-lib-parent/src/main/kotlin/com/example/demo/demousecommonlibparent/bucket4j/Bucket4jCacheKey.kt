package com.example.demo.demousecommonlibparent.bucket4j

import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Component

@Component
class Bucket4jCacheKey {

    fun getKeyByParamAndUrl(request: HttpServletRequest): String {
        return request.requestURI + request.getParameter("user-id")
    }
}