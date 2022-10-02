package com.dimitribelfor.tfttracker.api

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.util.DefaultUriBuilderFactory

@Configuration
@ConfigurationProperties(prefix = "dimitribelfor.riot.api")
data class ApiConfig(var key: String = "", val basePath: String = "")

@Configuration
class RestTemplateConfig(private val apiConfig: ApiConfig) {

    @Bean
    fun riotRestTemplate(restTemplateBuilder: RestTemplateBuilder) =
        restTemplateBuilder
            .uriTemplateHandler(DefaultUriBuilderFactory("https://euw1.api.riotgames.com"))
            .additionalInterceptors(RiotAuthHeaderInterceptor(apiConfig.key))
            .build()
}