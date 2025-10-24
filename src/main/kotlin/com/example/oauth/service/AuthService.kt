package com.example.oauth.service

import com.example.oauth.domain.OAuthToken
import com.example.oauth.domain.RefreshToken
import com.example.oauth.domain.User
import com.example.oauth.dto.LoginRequest
import com.example.oauth.dto.SignUpRequest
import com.example.oauth.dto.TokenResponse
import com.example.oauth.repository.OAuthTokenRepository
import com.example.oauth.repository.RefreshTokenRepository
import com.example.oauth.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional(readOnly = true)
class AuthService(
    private val userRepository: UserRepository,
    private val oAuthTokenRepository: OAuthTokenRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
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

    @Transactional
    fun login(request: LoginRequest): TokenResponse {
        val user = userRepository.findByEmail(request.email)
            ?: throw IllegalArgumentException("사용자를 찾을 수 없습니다.")

        require(passwordEncoder.matches(request.password, user.password)) {
            "비밀번호가 일치하지 않습니다."
        }

        val accessToken = generateOpaqueToken()
        val refreshToken = generateOpaqueToken()

        val oauthToken = OAuthToken.create(
            user = user,
            accessToken = accessToken,
            expiresInSeconds = 3600
        )
        oAuthTokenRepository.save(oauthToken)

        val savedRefreshToken = RefreshToken.create(
            user = user,
            refreshToken = refreshToken,
            expiresInSeconds = 1209600
        )
        refreshTokenRepository.save(savedRefreshToken)

        return TokenResponse.of(accessToken, refreshToken, 3600)
    }

    private fun generateOpaqueToken(): String {
        return UUID.randomUUID().toString()
    }
}