package com.dimitribelfor.tfttracker.api

import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse

const val TOKEN_HEADER_NAME = "X-Riot-Token"

class RiotAuthHeaderInterceptor(private val apiKey: String) : ClientHttpRequestInterceptor {

    override fun intercept(
        request: HttpRequest,
        body: ByteArray,
        execution: ClientHttpRequestExecution
    ): ClientHttpResponse {
        request.headers.add(TOKEN_HEADER_NAME, apiKey)
        return execution.execute(request, body)
    }
}