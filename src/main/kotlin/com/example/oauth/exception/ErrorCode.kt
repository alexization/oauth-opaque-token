package com.example.oauth.exception

enum class ErrorCode (
    val code: String,
    val message: String
){
    /** 인증 관련 */
    AUTHENTICATION_FAILED("AUTH001", "인증에 실패했습니다.")
}