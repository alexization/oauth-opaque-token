package com.example.oauth.filter

import com.example.oauth.service.AuthService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class OpaqueTokenFilter(
    private val authService: AuthService
) : OncePerRequestFilter() {

    companion object {
        private const val AUTHORIZATION_HEADER = "Authorization"
        private const val BEARER_PREFIX = "Bearer "
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            /* 1. 토큰 추출 */
            val token = extractToken(request)

            /* 2. 토큰이 있으면 검증 */
            token?.let { validateAndSetAuthentication(it, request) }
        } catch (e: Exception) { }

        filterChain.doFilter(request, response)
    }

    private fun extractToken(request: HttpServletRequest): String? {
        return request.getHeader(AUTHORIZATION_HEADER)
            ?.takeIf { it.startsWith(BEARER_PREFIX) }
            ?.substring(BEARER_PREFIX.length)
    }

    private fun validateAndSetAuthentication(token: String, request: HttpServletRequest) {
        /* 1. DB 에서 토큰 검증 */
        val user = authService.validateAccessToken(token)
            ?: return

        /* 2. 권한 설정 */
        val authorities = listOf(SimpleGrantedAuthority("ROLE_${user.role.name}"))

        /* 3. Authentication 객체 생성 */
        val authentication = UsernamePasswordAuthenticationToken(
            user.id,
            null,
            authorities
        ).also {
            it.details = WebAuthenticationDetailsSource()
                .buildDetails(request)
        }

        /* 4. SecurityContext 에 저장 */
        SecurityContextHolder.getContext().authentication = authentication
    }
}