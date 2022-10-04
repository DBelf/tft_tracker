package com.dimitribelfor.tfttracker.api

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.util.DefaultUriBuilderFactory

@Configuration
@ConfigurationProperties(prefix = "dimitribelfor.riot.api")
data class ApiConfig(var key: String = "", var platformUrl: String = "", var regionUrl: String = "")

@Configuration
class RestTemplateConfig(private val apiConfig: ApiConfig) {

    @Bean
    fun riotPlatformRestTemplate(restTemplateBuilder: RestTemplateBuilder) =
            restTemplateBuilder
                    .uriTemplateHandler(DefaultUriBuilderFactory(apiConfig.platformUrl))
                    .additionalInterceptors(RiotAuthHeaderInterceptor(apiConfig.key))
                    .build()

    @Bean
    fun riotRegionRestTemplate(restTemplateBuilder: RestTemplateBuilder) =
            restTemplateBuilder
                    .uriTemplateHandler(DefaultUriBuilderFactory(apiConfig.regionUrl))
                    .additionalInterceptors(RiotAuthHeaderInterceptor(apiConfig.key))
                    .build()
}