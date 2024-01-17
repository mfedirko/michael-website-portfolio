package io.mfedirko.common.infra.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.filter.OncePerRequestFilter

class ParseWebhookAuthorizerFilter(private val webhookKey: String) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        if (request.getHeader("X-Parse-Webhook-Key") != webhookKey) {
            response.sendError(403)
            return
        }
        filterChain.doFilter(request, response)
    }

}
