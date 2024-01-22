package com.example.demo.demousecommonlibparent.controller

import com.example.commonlib.poccommonlib.resource.GetResourceUtils
import com.example.demo.demousecommonlibparent.constants.EndpointConstant
import com.example.demo.demousecommonlibparent.resource.DemoGetResourceUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(EndpointConstant.GET_RESOURCE)
class GetResourceController {

    @Autowired
    lateinit var demoGetResourceUtils: DemoGetResourceUtils
    @Autowired
    lateinit var commonLibGetResourceUtils: GetResourceUtils

    @GetMapping(EndpointConstant.DEMO + "/{filename}")
    fun getResourceDemo(@PathVariable filename: String): String {
        return demoGetResourceUtils.getResourceDemo(filename)
    }

    @GetMapping(EndpointConstant.COMMON_LIB + "/{filename}")
    fun getResourceCommonLib(@PathVariable filename: String): String {
        return commonLibGetResourceUtils.getResourceCommonLib(filename)
    }

    @GetMapping(EndpointConstant.DEMO + EndpointConstant.COMMON_LIB + "/{filename}")
    fun getResourceAtCommonLibFromDemo(@PathVariable filename: String): String {
        return demoGetResourceUtils.getResourceAtCommonLibFromDemo(filename)
    }

    @GetMapping(EndpointConstant.COMMON_LIB + EndpointConstant.DEMO + "/{filename}")
    fun getResourceAtDemoFromCommonLib(@PathVariable filename: String): String {
        return commonLibGetResourceUtils.getResourceAtDemoFromCommonLib(filename)
    }
}