package com.example.oauth.exception

class AuthenticationException(
    errorCode: ErrorCode = ErrorCode.AUTHENTICATION_FAILED,
    message: String = errorCode.message
) : CustomException(errorCode, message)

class DuplicateResourceException(
    message: String = ErrorCode.DUPLICATE_RESOURCE.message,
) : CustomException(ErrorCode.DUPLICATE_RESOURCE, message)

class InvalidTokenException(
    message: String = ErrorCode.INVALID_TOKEN.message
) : CustomException(ErrorCode.INVALID_TOKEN, message)

class ExpiredTokenException(
    message: String = ErrorCode.EXPIRED_TOKEN.message
) : CustomException(ErrorCode.EXPIRED_TOKEN, message)