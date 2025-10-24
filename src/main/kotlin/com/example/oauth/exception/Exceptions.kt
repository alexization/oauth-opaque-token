package com.example.oauth.exception

class AuthenticationException (
    errorCode: ErrorCode = ErrorCode.AUTHENTICATION_FAILED,
    message: String = errorCode.message
): CustomException(errorCode, message)

class DuplicateResourceException(
    message: String = ErrorCode.DUPLICATE_RESOURCE.message,
): CustomException(ErrorCode.DUPLICATE_RESOURCE, message)