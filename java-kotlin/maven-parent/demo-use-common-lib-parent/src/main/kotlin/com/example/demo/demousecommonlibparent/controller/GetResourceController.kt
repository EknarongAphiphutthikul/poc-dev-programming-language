package com.example.demo.demousecommonlibparent.controller

import com.example.demo.demousecommonlibparent.constants.EndpointConstant
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(EndpointConstant.GET_RESOURCE)
class GetResourceController {

    @GetMapping(EndpointConstant.DEMO + "/{filename}")
    fun getResourceDemo(@PathVariable filename: String): String {
        return "getResourceDemo filename : $filename"
    }

    @GetMapping(EndpointConstant.COMMON_LIB + "/{filename}")
    fun getResourceCommonLib(@PathVariable filename: String): String {
        return "getResourceCommonLib filename : $filename"
    }

    @GetMapping(EndpointConstant.DEMO + EndpointConstant.COMMON_LIB + "/{filename}")
    fun getResourceAtCommonLibFromDemo(@PathVariable filename: String): String {
        return "getResourceAtCommonLibFromDemo filename : $filename"
    }

    @GetMapping(EndpointConstant.COMMON_LIB + EndpointConstant.DEMO + "/{filename}")
    fun getResourceAtDemoFromCommonLib(@PathVariable filename: String): String {
        return "getResourceAtDemoFromCommonLib filename : $filename"
    }
}