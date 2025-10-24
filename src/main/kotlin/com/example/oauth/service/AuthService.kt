package com.example.oauth.service

import com.example.oauth.domain.OAuthToken
import com.example.oauth.domain.RefreshToken
import com.example.oauth.domain.User
import com.example.oauth.dto.LoginRequest
import com.example.oauth.dto.SignUpRequest
import com.example.oauth.dto.TokenResponse
import com.example.oauth.exception.AuthenticationException
import com.example.oauth.exception.DuplicateResourceException
import com.example.oauth.exception.ErrorCode
import com.example.oauth.repository.OAuthTokenRepository
import com.example.oauth.repository.RefreshTokenRepository
import com.example.oauth.repository.UserRepository
import org.slf4j.LoggerFactory
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
    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    fun signUp(request: SignUpRequest): User {
        if (userRepository.existsByEmail(request.email)) {
            throw DuplicateResourceException(ErrorCode.DUPLICATE_RESOURCE.message)
        }

        val user = User.create(
            email = request.email,
            password = passwordEncoder.encode(request.password),
            name = request.name
        )

        val savedUser = userRepository.save(user)
        logger.info("새로운 사용자 가입 : ${savedUser.email}")

        return savedUser
    }

    @Transactional
    fun login(request: LoginRequest): TokenResponse {
        val user = (userRepository.findByEmail(request.email)
            ?: throw AuthenticationException(errorCode = ErrorCode.INVALID_CREDENTIALS))

        if (!passwordEncoder.matches(request.password, user.password)) {
            throw AuthenticationException(errorCode = ErrorCode.INVALID_CREDENTIALS)
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

        logger.info("사용자 로그인 : ${user.email}")

        return TokenResponse.of(accessToken, refreshToken, 3600)
    }

    private fun generateOpaqueToken(): String {
        return UUID.randomUUID().toString()
    }
}