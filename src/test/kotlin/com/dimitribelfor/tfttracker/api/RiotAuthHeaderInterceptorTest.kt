package com.dimitribelfor.tfttracker.api

import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution

private const val TEST_API_KEY = "testy"

@ExtendWith(MockKExtension::class)
internal class RiotAuthHeaderInterceptorTest {

    private val riotAuthHeaderInterceptor = RiotAuthHeaderInterceptor(TEST_API_KEY)

    @Test
    internal fun `should add the riot header when intercepting`() {
        val request = mockk<HttpRequest>()
        val execution = mockk<ClientHttpRequestExecution>()
        val headers = HttpHeaders()

        every { request.headers } returns headers
        every { execution.execute(any(), any()) } returns mockk()

        riotAuthHeaderInterceptor.intercept(request, ByteArray(0), execution)

        assertThat(headers)
            .hasSize(1)
            .containsEntry(TOKEN_HEADER_NAME, listOf(TEST_API_KEY))
    }
}