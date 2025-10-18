package com.example.oauth.controller

import com.example.oauth.dto.ApiResponse
import com.example.oauth.dto.SignUpRequest
import com.example.oauth.dto.UserResponse
import com.example.oauth.service.AuthService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authService: AuthService
) {
    @PostMapping("/signup")
    fun signUp(
        @Valid @RequestBody request: SignUpRequest
    ): ResponseEntity<ApiResponse<UserResponse>> {
        return runCatching {
            val user = authService.signUp(request)
            val response = UserResponse.from(user)

            ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response))
        }.getOrElse { e ->
            val message = when (e) {
                is IllegalArgumentException -> e.message ?: "회원가입 실패"
                else -> "서버 내부 오류가 발생했습니다."
            }

            ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(message))
        }
    }
}