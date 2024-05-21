package org.example.kotlinrestspringboot.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.example.kotlinrestspringboot.interceptor.LogInterceptor
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebMvcConfig(private val mapper: ObjectMapper) : WebMvcConfigurer {

    @Value("\${spring.application.name}")
    private val appName:String = ""

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(LogInterceptor(mapper, appName))
            .addPathPatterns("/**")
    }
}