package com.example.oauth.exception

sealed class CustomException(
    message: String,
    val errorCode: ErrorCode
) : RuntimeException(message)

class AuthenticationException(
    message: String = ErrorCode.AUTHENTICATION_FAILED.message,
    errorCode: ErrorCode = ErrorCode.AUTHENTICATION_FAILED
): CustomException(message, errorCode)