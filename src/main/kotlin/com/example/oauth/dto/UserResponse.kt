package com.example.oauth.dto

import com.example.oauth.domain.User

data class UserResponse(
    val id: Long,
    val email: String,
    val name: String,
    val role: String
) {
    companion object {
        fun from(user: User): UserResponse {
            return UserResponse(
                id = user.id!!,
                email = user.email,
                name = user.name,
                role = user.role.name
            )
        }
    }
}
