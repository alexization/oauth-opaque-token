package com.example.oauth.controller

import com.example.oauth.dto.ApiResponse
import com.example.oauth.dto.LoginRequest
import com.example.oauth.dto.SignUpRequest
import com.example.oauth.dto.TokenResponse
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
        val user = authService.signUp(request)
        val response = UserResponse.from(user)

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success(response))
    }

    @PostMapping("/login")
    fun login(
        @Valid @RequestBody request: LoginRequest
    ): ResponseEntity<ApiResponse<TokenResponse>> {
        val tokenResponse = authService.login(request)

        return ResponseEntity.ok(ApiResponse.success(tokenResponse))
    }
}