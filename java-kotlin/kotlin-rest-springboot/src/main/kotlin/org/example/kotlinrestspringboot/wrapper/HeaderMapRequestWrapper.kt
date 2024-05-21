package org.example.kotlinrestspringboot.wrapper

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletRequestWrapper
import java.util.*
import kotlin.collections.HashMap

class HeaderMapRequestWrapper(request: HttpServletRequest): HttpServletRequestWrapper(request) {
    private val headerMap: MutableMap<String, String> = HashMap()
    fun addHeader(name: String, value: String) {
        headerMap[name] = value
    }

    override fun getHeaderNames(): Enumeration<String> {
        val names: MutableList<String> = Collections.list(super.getHeaderNames())
        for (name in headerMap.keys) {
            names.add(name)
        }
        return Collections.enumeration(names)
    }

    override fun getHeader(name: String?): String? {
        val value = if (headerMap[name] != null){
            headerMap[name]
        }else super.getHeader(name)
        return value
    }

    override fun getHeaders(name: String): Enumeration<String> {
        val values: MutableList<String?> = Collections.list(super.getHeaders(name))
        if (headerMap.containsKey(name)) {
            values.add(headerMap[name])
        }
        return Collections.enumeration(values)
    }
}