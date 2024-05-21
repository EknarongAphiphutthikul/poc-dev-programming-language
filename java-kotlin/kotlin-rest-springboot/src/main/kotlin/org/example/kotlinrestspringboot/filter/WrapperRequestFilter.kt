package org.example.kotlinrestspringboot.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingRequestWrapper


@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class WrapperRequestFilter: OncePerRequestFilter() {

    private val log = LoggerFactory.getLogger(WrapperRequestFilter::class.java)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val contentType = request.contentType
        var req = request
        if (contentType == null || !contentType.contains(MediaType.MULTIPART_FORM_DATA_VALUE)) {
            req = ContentCachingRequestWrapper(request)
        }
        filterChain.doFilter(req, response)
    }
}