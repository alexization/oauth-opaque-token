package com.example.oauth.dto

data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String? = null
) {
    companion object {
        fun <T> success(data: T): ApiResponse<T> {
            return ApiResponse(true, data = data)
        }

        fun success(message: String): ApiResponse<Unit> {
            return ApiResponse(true, message = message)
        }

        fun <T> fail(message: String): ApiResponse<T> {
            return ApiResponse(false, message = message)
        }
    }
}
