package com.dimitribelfor.tfttracker.api

import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest

@RestClientTest(TftMatchService::class)
internal class TftMatchServiceIT {

    @Test
    internal fun `can fetch match data`() {
        TODO("Not yet implemented")
    }
}