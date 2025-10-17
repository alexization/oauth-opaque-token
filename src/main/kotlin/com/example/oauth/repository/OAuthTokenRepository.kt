package com.example.oauth.repository

import com.example.oauth.domain.OAuthToken
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface OAuthTokenRepository : JpaRepository<OAuthToken, Long> {
    fun findByAccessToken(accessToken: String): OAuthToken?

    fun findAllByUserId(userId: Long): List<OAuthToken>

    @Modifying
    @Query("DELETE FROM OAuthToken t WHERE t.user.id = :userId")
    fun deleteAllByUserId(@Param("userId") userId: Long)
}