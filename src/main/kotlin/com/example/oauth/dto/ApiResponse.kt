package com.example.oauth.dto

data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val error: ErrorResponse? = null,
    val timeStamp: Long = System.currentTimeMillis()
) {
    companion object {
        fun <T> success(data: T): ApiResponse<T> {
            return ApiResponse(true, data = data)
        }

        fun success(message: String): ApiResponse<MessageResponse> {
            return ApiResponse(true, data = MessageResponse(message))
        }

        fun <T> error(error: ErrorResponse): ApiResponse<T> {
            return ApiResponse(false, error = error)
        }
    }
}

data class MessageResponse(
    val message: String
)

data class ErrorResponse(
    val code: String,
    val message: String,
    val details: Map<String, String>? = null
)