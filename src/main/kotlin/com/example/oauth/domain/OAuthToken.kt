package com.example.oauth.domain

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Duration
import java.time.LocalDateTime

@Entity
@Table(name = "oauth_tokens")
@EntityListeners(AuditingEntityListener::class)
data class OAuthToken(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    /* Opaque Access Token */
    @Column(name = "access_token", unique = true, nullable = false)
    val accessToken: String,

    @Column(name = "expires_at", nullable = false)
    val expiresAt: LocalDateTime,

    @Column(name = "ip_address")
    val ipAddress: String? = null,

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime? = null,
) {
    companion object {
        fun create(
            user: User,
            accessToken: String,
            expiresInSeconds: Long = 3600,  // 기본 1시간
            ipAddress: String? = null
        ): OAuthToken {
            return OAuthToken(
                user = user,
                accessToken = accessToken,
                expiresAt = LocalDateTime.now().plusSeconds(expiresInSeconds),
                ipAddress = ipAddress
            )
        }
    }

    fun isExpired(): Boolean {
        return LocalDateTime.now().isAfter(expiresAt)
    }

    fun isValid(): Boolean {
        return !isExpired()
    }

    fun getTimeToExpire(): Long {
        val now = LocalDateTime.now()
        return if (now.isBefore(expiresAt)) {
            Duration.between(now, expiresAt).seconds
        } else {
            0L
        }
    }
}