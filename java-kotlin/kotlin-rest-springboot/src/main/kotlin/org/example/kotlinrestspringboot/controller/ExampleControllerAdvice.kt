package org.example.kotlinrestspringboot.controller

import org.example.kotlinrestspringboot.model.ModelResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ExampleControllerAdvice {

    private val log = LoggerFactory.getLogger(ExampleControllerAdvice::class.java)

    @ExceptionHandler(
        Exception::class,
    )
    fun generalHandler(exception: Exception): ResponseEntity<ModelResponse> {
        log.error(exception.message, exception)
        return ResponseEntity(
            ModelResponse("500"), HttpStatus.INTERNAL_SERVER_ERROR
        )
    }
}