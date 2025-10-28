package com.example.oauth.domain

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "refresh_tokens")
@EntityListeners(AuditingEntityListener::class)
data class RefreshToken(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(name = "refresh_token", unique = true, nullable = false)
    val refreshToken: String,

    @Column(name = "expires_at", nullable = false)
    val expiresAt: LocalDateTime,

    @Column(nullable = false)
    var revoked: Boolean = false,

    @Column(name = "ip_address")
    val ipAddress: String? = null,

    @Column(name = "last_used_at")
    var lastUsedAt: LocalDateTime? = null,

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime? = null
) {
    companion object {
        fun create(
            user: User,
            refreshToken: String,
            expiresInSeconds: Long = 1209600,   // 14일
            ipAddress: String? = null
        ): RefreshToken {
            return RefreshToken(
                user = user,
                refreshToken = refreshToken,
                expiresAt = LocalDateTime.now().plusSeconds(expiresInSeconds),
                ipAddress = ipAddress
            )
        }
    }

    fun isExpired(): Boolean {
        return LocalDateTime.now().isAfter(expiresAt)
    }

    fun isValid(): Boolean {
        return !revoked && !isExpired()
    }

    fun revoke() {
        this.revoked = true
    }

    fun updateLastUsed() {
        this.lastUsedAt = LocalDateTime.now()
    }
}
