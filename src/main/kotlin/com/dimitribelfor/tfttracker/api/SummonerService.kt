package com.dimitribelfor.tfttracker.api

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.stereotype.Service
import org.springframework.web.client.getForEntity
import org.springframework.web.util.DefaultUriBuilderFactory
import java.util.logging.Logger

private const val SUMMONER_BY_NAME_ENDPOINT = "/tft/summoner/v1/summoners/by-name/{summonerName}"

@Service
class SummonerService(
    restTemplateBuilder: RestTemplateBuilder,
    apiConfig: ApiConfig
) {
    private val restTemplate = restTemplateBuilder
        .uriTemplateHandler(DefaultUriBuilderFactory(apiConfig.platformUrl))
        .additionalInterceptors(RiotAuthHeaderInterceptor(apiConfig.key))
        .build()

    companion object {
        val log: Logger = Logger.getLogger(SummonerService::class.java.name)
    }

    fun getSummonerByName(summoner: SummonerName) =
        try {
            restTemplate
                .getForEntity<Summoner>(SUMMONER_BY_NAME_ENDPOINT, summoner.value)
                .body
                ?.also { log.info { "Fetched summoner details for ${summoner.value}" } }
        } catch (e: Exception) {
            log.warning { "Did not fetch summoner details." }
            throw SummonerNotFetchedException("Failed to fetch summoner details for ${summoner.value}", e)
        } ?: run {
            log.warning { "Did not fetch summoner details." }
            throw SummonerNotFetchedException("Failed to fetch summoner details for ${summoner.value}")
        }

    data class Summoner(
        @JsonProperty val accountId: String,
        @JsonProperty val puuid: String,
        @JsonProperty val name: SummonerName
    )

    @JvmInline
    value class SummonerName(val value: String)
}

class SummonerNotFetchedException : RuntimeException {
    constructor(message: String, cause: Throwable) : super(message, cause)
    constructor(message: String) : super(message)
}
