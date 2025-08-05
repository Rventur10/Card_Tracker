package com.example.cardtracker

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.retry.annotation.EnableRetry
import org.springframework.web.client.RestTemplate

@TestConfiguration
@EnableRetry
class TestApplication {

    @Bean
    @Primary
    fun testRestTemplate(): RestTemplate {
        return RestTemplate()
    }
}