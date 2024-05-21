package org.example.kotlinrestspringboot.interceptor

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.boot.web.servlet.DispatcherType
import org.springframework.core.MethodParameter
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.http.server.ServletServerHttpRequest
import org.springframework.util.ObjectUtils
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice
import org.springframework.web.util.ContentCachingRequestWrapper
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.collections.HashMap

private val PATTERN_EXTRACT_NUMBER = Pattern.compile("\\d+")

@ControllerAdvice
//@RestControllerAdvice
open class LogInterceptor(
    private val objectMapper: ObjectMapper = ObjectMapper(),
    private val appName: String = "appName"
) : HandlerInterceptor, ResponseBodyAdvice<Any> {

    protected open val contentTypeThatGonnaLogged = setOf(MediaType.APPLICATION_JSON)
    protected open val ignorePath: List<String> = listOf("/health", "/info", "/sanity", "/prometheus")
    protected open val logClientIpKey = "clientip"
    protected open val correlationIdKey = "correlationId"
    protected open val xCorrelationIdKey = "x-correlation-id"

    private val log = LoggerFactory.getLogger(LogInterceptor::class.java)

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        if (ignorePath.contains(request.servletPath)) {
            return true
        }
        setLoggingMetadata(request)
        if (DispatcherType.REQUEST.name == request.dispatcherType.name && request.method != HttpMethod.OPTIONS.name()) {
            log.info(getLog(request).toString())
        }
        return true
    }

    protected open fun setLoggingMetadata(request: HttpServletRequest) {
        var correlationId = request.getHeader(correlationIdKey)
        var xCorrelationId = request.getHeader(xCorrelationIdKey)
        if (ObjectUtils.isEmpty(correlationId)) {
            correlationId = UUID.randomUUID().toString()
        }
        if (ObjectUtils.isEmpty(xCorrelationId)) {
            xCorrelationId = String.format("%s%s", this.appName, correlationId)
        }
        clearMDC()
        MDC.put(correlationIdKey, correlationId)
        MDC.put(xCorrelationIdKey, xCorrelationId)
        MDC.put(logClientIpKey, request.remoteAddr)
    }

    protected open fun getLog(httpServletRequest: HttpServletRequest): StringBuilder {
        val stringBuilder = StringBuilder()
        stringBuilder.append("LOGGING_INTERCEPTOR :: ")
        stringBuilder.append("logType=[REQUEST] ")
        stringBuilder.append("method=[").append(httpServletRequest.method).append("] ")
        stringBuilder.append("path=[").append(getUriPathWithMask(httpServletRequest.requestURI)).append("] ")
        stringBuilder.append("headers=[").append(objectMapper.writeValueAsString(buildHeadersMap(httpServletRequest))).append("] ")
        val parameters = buildParametersMap(httpServletRequest)
        if (parameters.isNotEmpty()) {
            stringBuilder.append("parameters=[").append(objectMapper.writeValueAsString(parameters)).append("] ")
        }
        return stringBuilder
    }

    override fun supports(returnType: MethodParameter, converterType: Class<out HttpMessageConverter<*>>): Boolean {
        return true
    }

    override fun beforeBodyWrite(
        o: Any?,
        methodParameter: MethodParameter,
        mediaType: MediaType,
        aClass: Class<out HttpMessageConverter<*>>,
        serverHttpRequest: ServerHttpRequest,
        serverHttpResponse: ServerHttpResponse
    ): Any? {
        try {
            val servletRequest = (serverHttpRequest as ServletServerHttpRequest).servletRequest
            if (ignorePath.contains(servletRequest.servletPath)) {
                return o
            }
            val body = buildBody(servletRequest)
            if (body.isNotEmpty()) {
                log.info(String.format("LOGGING_INTERCEPTOR :: logType=[REQUEST] body[%s]", body))
            }
            log.info(
                String.format(
                    "LOGGING_INTERCEPTOR :: logType=[RESPONSE] method=[%s] path=[%s] headers=[%s] body=[%s] ",
                    serverHttpRequest.getMethod(),
                    getUriPathWithMask(servletRequest.requestURI),
                    objectMapper.writeValueAsString(buildHeadersMap(serverHttpResponse)),
                    if (shouldLogResponseBody(mediaType)) objectMapper.writeValueAsString(o)
                    else "The body will not be logged due to the content-type is not in configured list."
                )
            )
        } catch (ex: Exception) {
            log.error("Cannot log Response Body", ex)
        } finally {
            clearMDC()
        }
        return o
    }

    protected open fun buildBody(httpServletRequest: HttpServletRequest): String {
        val contentType = httpServletRequest.contentType
        return when {
            (contentType == null || !contentType.contains(MediaType.MULTIPART_FORM_DATA_VALUE)) -> {
                buildBodyApplicationJson(httpServletRequest)
            }
            else -> {
                buildBodyMultipartFormData(httpServletRequest)
            }
        }
    }

    protected open fun buildBodyApplicationJson(httpServletRequest: HttpServletRequest): String {
        when(httpServletRequest){
            is ContentCachingRequestWrapper -> {
                return httpServletRequest.contentAsString.replace("\n", "")
            }
            else -> {
                throw IllegalArgumentException("Unsupported request type, expected ContentCachingRequestWrapper but got ${httpServletRequest.javaClass.simpleName}")
            }
        }
    }

    protected open fun buildBodyMultipartFormData(httpServletRequest: HttpServletRequest): String {
        val multipartRequest = httpServletRequest as StandardMultipartHttpServletRequest
        val multipartMapRequest = mutableMapOf<String, String>()

        multipartRequest.parameterMap.takeUnless{ it.isEmpty() }?.map { it.key to it.value.let { values ->
            when(values.size) {
                1 -> values[0]
                else -> values.toList().toString()
            }
        }
        }?.toMap().also { multipartMapRequest.putAll(it ?: emptyMap()) }

        multipartRequest.multiFileMap.takeUnless{ it.isEmpty() }?.map { it.key to it.value.let { files ->
            files.map { file ->
                "${file.originalFilename} size: ${file.size} bytes"
            }.toList().toString()
        }
        }?.toMap().also { multipartMapRequest.putAll(it ?: emptyMap()) }

        return multipartMapRequest.takeIf { multipartMapRequest.isNotEmpty() }?.let {
            StringBuilder("multipart form data: $multipartMapRequest").toString()
        }?: ""
    }

    protected open fun getUriPathWithMask(path: String): String {
        var temp: String = path
        if (!ObjectUtils.isEmpty(temp) && temp.matches(Regex("^(?=.*\\d{13}).+$"))) {
            val numberInUrl: Matcher = PATTERN_EXTRACT_NUMBER.matcher(temp)

            while (numberInUrl.find()) {
                if (numberInUrl.group().length == 13) {
                    temp = temp.replace(numberInUrl.group(), maskStringValue(numberInUrl.group()))
                }
            }
        }
        return temp
    }

    protected open fun maskStringValue(value: String): String {
        if (ObjectUtils.isEmpty(value)) {
            return value
        } else {
            val var10000: String = value.substring(0, value.length - 4)
            return var10000 + "XXXX"
        }
    }

    protected open fun clearMDC() {
        MDC.clear()
    }

    private fun buildHeadersMap(request: HttpServletRequest): Map<String, String> {
        val map: MutableMap<String, String> = HashMap()
        val headerNames = request.headerNames
        while (headerNames.hasMoreElements()) {
            val key = headerNames.nextElement()
            map[key] = request.getHeader(key)
        }
        return map
    }

    private fun buildParametersMap(httpServletRequest: HttpServletRequest): Map<String, String> {
        val resultMap: MutableMap<String, String> = HashMap()
        val parameterNames = httpServletRequest.parameterNames
        while (parameterNames.hasMoreElements()) {
            val key = parameterNames.nextElement()
            resultMap[key] = httpServletRequest.getParameter(key)
        }
        return resultMap
    }

    private fun buildHeadersMap(httpResponse: ServerHttpResponse): Map<String, String> {
        return httpResponse.headers.map { it.key to it.value.toString() }.toMap()
    }

    private fun shouldLogResponseBody(mediaType: MediaType): Boolean {
        return  contentTypeThatGonnaLogged.contains(
            mediaType
        )
    }
}