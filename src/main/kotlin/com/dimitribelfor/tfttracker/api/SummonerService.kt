package com.dimitribelfor.tfttracker.api

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForEntity
import java.util.logging.Logger

private const val SUMMONER_BY_NAME_ENDPOINT = "/tft/summoner/v1/summoners/by-name/{summonerName}"

@Service
class SummonerService(@Qualifier("riotPlatformRestTemplate") private val restTemplate: RestTemplate) {
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
                throw SummonerNotFetchedException("Failed to fetch summoner details for ${summoner.value}", e)
            } ?: throw SummonerNotFetchedException("Failed to fetch summoner details for ${summoner.value}")

    data class Summoner(@JsonProperty val accountId: String, @JsonProperty val puuid: String)

    @JvmInline
    value class SummonerName(val value: String)
}

class SummonerNotFetchedException : RuntimeException {
    constructor(message: String, cause: Throwable) : super(message, cause)
    constructor(message: String) : super(message)
}
