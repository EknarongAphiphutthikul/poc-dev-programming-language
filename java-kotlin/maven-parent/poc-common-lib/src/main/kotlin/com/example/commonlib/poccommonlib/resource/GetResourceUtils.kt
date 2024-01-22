package com.example.commonlib.poccommonlib.resource

import com.example.commonlib.poccommonlib.utils.getLogger
import org.springframework.stereotype.Component

@Component
class GetResourceUtils {

    var logger = getLogger { }

    // get resource file from src/main/resources
    fun getResourceCommonLib(filename: String): String {
        logger.info(Thread.currentThread().contextClassLoader.getResource("common/$filename").path)
        logger.info(GetResourceUtils::class.java.classLoader.getResource("common/$filename").path)
        return GetResourceUtils::class.java.classLoader.getResource("common/$filename").readText()
    }

    // get resource file from src/main/resources in jar file of demo-use-common-lib-parent
    fun getResourceAtDemoFromCommonLib(filename: String): String {
        logger.info(Thread.currentThread().contextClassLoader.getResource("demo/$filename").path)
        logger.info(GetResourceUtils::class.java.classLoader.getResource("demo/$filename").path)
        return GetResourceUtils::class.java.classLoader.getResource("demo/$filename").readText()
    }
}