package com.example.oauth.config

import com.example.oauth.filter.OpaqueTokenFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val opaqueTokenFilter: OpaqueTokenFilter
) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .csrf { it.disable() }
            .cors { it.disable() }
            /* Opaque Token은 서버에 저장하지만, HTTP Session은 사용하지 않음 */
            .sessionManagement { session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers(
                        "/api/v1/auth/signup"
                    ).permitAll()
                    .requestMatchers("/api/v1/admin/**")
                    .hasRole("ADMIN")
                    .anyRequest().authenticated()
            }
            .addFilterBefore(
                opaqueTokenFilter,
                UsernamePasswordAuthenticationFilter::class.java
            )
            .exceptionHandling { exceptions ->
                exceptions
                    .authenticationEntryPoint { _, response, _ ->
                        response.sendError(401, "인증이 필요합니다.")
                    }
                    .accessDeniedHandler { _, response, _ ->
                        response.sendError(403, "접근 권한이 없습니다.")
                    }
            }
            .build()
    }
}