package com.dimitribelfor.tfttracker.api

import com.dimitribelfor.tfttracker.api.SummonerService.SummonerName
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.stereotype.Service
import org.springframework.web.client.getForEntity
import org.springframework.web.util.DefaultUriBuilderFactory
import java.util.logging.Logger

private const val MATCHES_BY_SUMMONER_ID_ENDPOINT = "/tft/match/v1/matches/by-puuid/{puuid}/ids?count={count}"

private const val MATCH_BY_ID_ENDPOINT = "/tft/match/v1/matches/{matchId}"

private const val MAX_NUMBER_OF_MATCHES_TO_FETCH = 5

@Service
class TftMatchService(
    private val summonerService: SummonerService,
    restTemplateBuilder: RestTemplateBuilder,
    apiConfig: ApiConfig
) {
    private val restTemplate = restTemplateBuilder
        .uriTemplateHandler(DefaultUriBuilderFactory(apiConfig.regionUrl))
        .additionalInterceptors(RiotAuthHeaderInterceptor(apiConfig.key))
        .build()

    companion object {
        val log: Logger = Logger.getLogger(TftMatchService::class.java.name)
    }

    fun getMatchHistoryDetails(summoner: SummonerName) =
        getMatchHistoryIds(summoner)
            .map { getSingleMatchById(it) }

    private fun getSingleMatchById(match: MatchId) =
        try {
            restTemplate.getForEntity<MatchDetailsDTO>(MATCH_BY_ID_ENDPOINT, match.value)
                .body
        } catch (e: Exception) {
            throw MatchNotFetchedException("Failed to fetch match details for match id ${match.value}", e)
        } ?: throw MatchNotFetchedException("Failed to fetch match details for match id ${match.value}")

    private fun getMatchHistoryIds(summoner: SummonerName) =
        try {
            summonerService.getSummonerByName(summoner)
                .puuid
                .let {
                    restTemplate.getForEntity<List<String>>(
                        MATCHES_BY_SUMMONER_ID_ENDPOINT,
                        it,
                        MAX_NUMBER_OF_MATCHES_TO_FETCH
                    )
                }
                .body
                ?.map { MatchId(it) }
                ?.also { log.info { "Fetched ${it.size} match ids for summoner ${summoner.value}" } }
        } catch (e: Exception) {
            throw MatchesNotFetchedException("Failed to fetch match ids for ${summoner.value}", e)
        } ?: throw MatchesNotFetchedException("Failed to fetch match ids for ${summoner.value}")

    data class MatchDetailsDTO(val metadata: MatchMetadataDTO, val info: InfoDTO)

    data class MatchMetadataDTO(@JsonProperty val matchId: MatchId, @JsonProperty val dataVersion: DataVersion)

    data class InfoDTO(@JsonProperty val tftSetNumber: Int, @JsonProperty val participants: List<ParticipantDTO>)

    data class ParticipantDTO(
        @JsonProperty val placement: Int,
        @JsonProperty val lastRound: Int,
        @JsonProperty val puuid: String,
        @JsonProperty val totalDamageToPlayers: Int
    )

    @JvmInline
    @JsonIgnoreProperties
    value class DataVersion(private val value: String)

    @JvmInline
    value class MatchId(val value: String)
}

class MatchNotFetchedException : RuntimeException {
    constructor(message: String, cause: Throwable) : super(message, cause)
    constructor(message: String) : super(message)
}

class MatchesNotFetchedException : RuntimeException {
    constructor(message: String, cause: Throwable) : super(message, cause)
    constructor(message: String) : super(message)
}
