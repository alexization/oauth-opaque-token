package com.example.oauth.domain

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener::class)
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(unique = true, nullable = false)
    val email: String,

    @Column(nullable = false)
    var password: String,

    @Column(nullable = false)
    var name: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var role: UserRole = UserRole.USER,

    @CreatedDate
    @Column(nullable = false, updatable = false)
    var createdAt: LocalDateTime? = null,

    @LastModifiedDate
    var updatedAt: LocalDateTime? = null
) {
    /*
    * JPA 엔티티는 no-arg constructor가 필요하다.
    * Kotlin 에서는 기본값을 주거나 kotlin-jpa 플러그인을 사용한다.
    * */
    companion object {
        fun create(email: String, password: String, name: String): User {
            return User(
                email = email,
                password = password,
                name = name
            )
        }
    }
}

enum class UserRole {
    USER, ADMIN
}