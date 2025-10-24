package com.example.oauth.exception

import com.example.oauth.dto.ApiResponse
import com.example.oauth.dto.ErrorResponse
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    private val logger = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(CustomException::class)
    fun handleCustomException(e: CustomException): ResponseEntity<ApiResponse<Nothing>> {
        logger.warn("[${e.errorCode.code} ${e.message}]", e)

        return ResponseEntity
            .status(e.errorCode.status)
            .body(
                ApiResponse.error(
                    ErrorResponse(
                        code = e.errorCode.code,
                        message = e.message
                    )
                )
            )
    }
}