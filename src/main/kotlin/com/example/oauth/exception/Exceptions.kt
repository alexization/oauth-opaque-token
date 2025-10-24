package com.example.oauth.exception

class AuthenticationException (
    errorCode: ErrorCode = ErrorCode.AUTHENTICATION_FAILED,
    message: String = errorCode.message
): CustomException(errorCode, message)