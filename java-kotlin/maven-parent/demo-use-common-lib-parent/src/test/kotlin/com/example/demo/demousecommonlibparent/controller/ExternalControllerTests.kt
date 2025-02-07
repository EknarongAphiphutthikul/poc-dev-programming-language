package com.example.demo.demousecommonlibparent.controller

import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(ExternalController::class)
@ExtendWith(MockKExtension::class)
class ExternalControllerTests(@Autowired val mockMvc: MockMvc,) {

    private val endPoint = "/api/external/hello/{timeSleep}"

    @Test
    fun `test external controller`() {
        mockMvc.perform(
                MockMvcRequestBuilders.get(endPoint, 1)
        ).andExpect(status().isOk).andExpect(content().string("Hello from external controller"))
    }
}