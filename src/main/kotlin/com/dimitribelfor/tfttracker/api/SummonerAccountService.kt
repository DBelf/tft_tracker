package com.dimitribelfor.tfttracker.api

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForEntity
import java.util.logging.Logger

@Service
class SummonerAccountService(private val riotRestTemplate: RestTemplate) {
    companion object {
        val log = Logger.getLogger(SummonerAccountService::class.java.name)
    }

    fun getSummonerData(summonerName: String) {
        val summoner = riotRestTemplate.getForEntity<Summoner>("/lol/summoner/v4/summoners/by-name/$summonerName").body
        log.info { "$summoner" }
    }

    data class Summoner(@JsonProperty val accountId: String)
}