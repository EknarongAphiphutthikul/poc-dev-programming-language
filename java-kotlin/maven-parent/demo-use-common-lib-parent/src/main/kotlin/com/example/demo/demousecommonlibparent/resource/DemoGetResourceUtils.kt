package com.example.demo.demousecommonlibparent.resource

import com.example.commonlib.poccommonlib.resource.GetResourceUtils
import com.example.commonlib.poccommonlib.utils.getLogger
import org.springframework.stereotype.Component

@Component
class DemoGetResourceUtils {

    var logger = getLogger { }

    // get resource file from src/main/resources
    fun getResourceDemo(filename: String): String {
        logger.info(Thread.currentThread().contextClassLoader.getResource("demo/$filename").path)
        logger.info(DemoGetResourceUtils::class.java.classLoader.getResource("demo/$filename").path)
        return DemoGetResourceUtils::class.java.classLoader.getResource("demo/$filename").readText()
    }

    // get resource file from src/main/resources in jar file of poc-common-lib
    fun getResourceAtCommonLibFromDemo(filename: String): String {
        logger.info(Thread.currentThread().contextClassLoader.getResource("common/$filename").path)
        logger.info(GetResourceUtils::class.java.classLoader.getResource("common/$filename").path)
        return GetResourceUtils::class.java.classLoader.getResource("common/$filename").readText()
    }
}