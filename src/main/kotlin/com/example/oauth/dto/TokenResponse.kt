package com.example.oauth.dto

data class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String = "Bearer",
    val expiresIn: Long
) {
    companion object {
        fun of(
            accessToken: String,
            refreshToken: String,
            expiresIn: Long
        ): TokenResponse {
            return TokenResponse(
                accessToken = accessToken,
                refreshToken = refreshToken,
                expiresIn = expiresIn
            )
        }
    }
}
