package com.example.oauth

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableJpaAuditing
class OauthApplication

fun main(args: Array<String>) {
	runApplication<OauthApplication>(*args)
}
