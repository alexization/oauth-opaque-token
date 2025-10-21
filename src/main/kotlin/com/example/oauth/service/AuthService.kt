package com.example.oauth.service

import com.example.oauth.domain.User
import com.example.oauth.dto.SignUpRequest
import com.example.oauth.repository.OAuthTokenRepository
import com.example.oauth.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AuthService(
    private val userRepository: UserRepository,
    private val oAuthTokenRepository: OAuthTokenRepository,
    private val passwordEncoder: PasswordEncoder
) {
    @Transactional
    fun signUp(request: SignUpRequest): User {
        require(!userRepository.existsByEmail(request.email)) {
            "이미 존재하는 이메일입니다: ${request.email}"
        }

        val user = User.create(
            email = request.email,
            password = passwordEncoder.encode(request.password),
            name = request.name
        )

        return userRepository.save(user)
    }

    fun validateAccessToken(accessToken: String): User? {
        /* 1. DB 에서 토큰 조회 */
        val oauthToken = oAuthTokenRepository.findByAccessToken(accessToken)
            ?: return null

        /* 2. 만료 확인 */
        if (oauthToken.isExpired()) {
            return null
        }

        return oauthToken.user
    }
}