package com.example.oauth.exception

import org.springframework.http.HttpStatus

enum class ErrorCode (
    val status: HttpStatus,
    val code: String,
    val message: String
){
    /** 인증 관련 */
    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "AUTH001", "인증에 실패했습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH002", "유효하지 않는 토큰입니다."),

    /** 리소스 관련 */
    DUPLICATE_RESOURCE(HttpStatus.CONFLICT, "RES001", "이미 존재하는 리소스입니다."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "RES002", "리소스를 찾을 수 없습니다.")
}