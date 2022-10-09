package com.dimitribelfor.tfttracker.api

import com.dimitribelfor.tfttracker.api.SummonerService.SummonerName
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.header
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withStatus
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess

@Import(ApiConfig::class)
@RestClientTest(SummonerService::class)
internal class SummonerServiceIT {
    companion object {
        const val SUMMONER_FILE = "/api/summoner.json"
    }

    @Autowired
    private lateinit var apiConfig: ApiConfig

    @Autowired
    private lateinit var summonerService: SummonerService

    @Autowired
    private lateinit var mockRestServiceServer: MockRestServiceServer

    @Test
    internal fun `can fetch summoner data`() {
        val summonerName = SummonerName("drsFuntimes")
        val summonerResponse = this::class.java.getResource(SUMMONER_FILE)!!.readText()
        this.mockRestServiceServer
            .expect(requestTo("${apiConfig.platformUrl}/tft/summoner/v1/summoners/by-name/${summonerName.value}"))
            .andExpect(header(TOKEN_HEADER_NAME, "test-key"))
            .andRespond(withSuccess(summonerResponse, APPLICATION_JSON))

        val summoner = summonerService.getSummonerByName(summonerName)

        assertThat(summoner)
            .returns(summonerName) { it.name }
    }

    @ParameterizedTest
    @EnumSource(value = HttpStatus::class, names = ["NOT_FOUND", "BAD_REQUEST", "INTERNAL_SERVER_ERROR", "UNAUTHORIZED", "FORBIDDEN"])
    internal fun `will throw a summoner not found exception on error response`(erroCode: HttpStatus) {
        val summonerName = SummonerName("drsFuntimes")
        this.mockRestServiceServer
            .expect(requestTo("${apiConfig.platformUrl}/tft/summoner/v1/summoners/by-name/${summonerName.value}"))
            .andExpect(header(TOKEN_HEADER_NAME, "test-key"))
            .andRespond(withStatus(erroCode))

        assertThatThrownBy { summonerService.getSummonerByName(summonerName) }
            .isInstanceOf(SummonerNotFetchedException::class.java)
    }
}