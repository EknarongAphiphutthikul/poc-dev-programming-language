package org.example.kotlinrestspringboot.controller


import org.example.kotlinrestspringboot.model.ModelRequest
import org.example.kotlinrestspringboot.model.ModelResponse
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile


@RestController
@RequestMapping("api/v1/example")
class ExampleController {

    private val log = LoggerFactory.getLogger(ExampleController::class.java)

    @GetMapping("/get/{id}")
    fun get(@PathVariable id: String): String {
        log.info("Get method with id: $id")
        return "GET method"
    }

    @PostMapping("/post")
    fun post(@RequestBody req: ModelRequest?): ModelResponse {
        log.info("Post method with req: ${req?.req}")
        return ModelResponse("200")
    }

    @PatchMapping("/patch")
    fun patch(@RequestParam data: ModelRequest?, @RequestParam files: Array<MultipartFile>?): ModelResponse {
        log.info("Patch method with req: ${data?.req} total file: ${files?.size}")
        return ModelResponse("200")
    }

    @GetMapping("/get/error")
    fun getError(): String {
        log.info("Get method with error")
        throw RuntimeException("Error")
    }
}