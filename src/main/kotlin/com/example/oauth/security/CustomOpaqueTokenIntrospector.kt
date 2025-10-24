package com.example.oauth.security

import com.example.oauth.domain.User
import com.example.oauth.repository.OAuthTokenRepository
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal
import org.springframework.security.oauth2.server.resource.introspection.BadOpaqueTokenException
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector
import org.springframework.stereotype.Component

/**
 * RFC 7662 Token Introspection 구현
 * Resource Server가 토큰의 유효성을 검증하는 로직
 * */
@Component
class CustomOpaqueTokenIntrospector(
    private val oAUthTokenRepository: OAuthTokenRepository
): OpaqueTokenIntrospector {

    override fun introspect(token: String): OAuth2AuthenticatedPrincipal {
        // 1. DB 에서 토큰 조회
        val oauthToken = oAUthTokenRepository.findByAccessToken(token)
            ?: throw BadOpaqueTokenException("유효하지 않은 토큰입니다.")

        // 2. 토큰 만료 확인
        if (oauthToken.isExpired()) {
            throw BadOpaqueTokenException("만료된 토큰입니다.")
        }

        // 3. 사용자 정보와 권한 추출
        val user = oauthToken.user

        // 4. OAuth2AuthenticatedPrincipal 생성
        return createPrincipal(user)
    }

    private fun createPrincipal(user: User): OAuth2AuthenticatedPrincipal {
        val authorities = listOf(
            SimpleGrantedAuthority("ROLE_${user.role.name}")
        )

        val attributes = mapOf(
            "sub" to user.id.toString(),
            "email" to user.email,
            "name" to user.name,
            "role" to user.role.name,
            "active" to true
        )

        return CustomOAuth2AuthenticatedPrincipal(
            name = user.email,
            attributes = attributes,
            authorities = authorities
        )
    }
}

class CustomOAuth2AuthenticatedPrincipal(
    private val name: String,
    private val attributes: Map<String, Any>,
    private val authorities: Collection<SimpleGrantedAuthority>
): OAuth2AuthenticatedPrincipal {
    override fun getName(): String = name

    override fun getAuthorities(): Collection<GrantedAuthority> = authorities

    override fun getAttributes(): Map<String, Any> = attributes
}