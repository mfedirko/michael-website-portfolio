package io.mfedirko.common.infra.back4app

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import io.mfedirko.common.util.Logging.logger
import org.springframework.http.client.ClientHttpResponse
import org.springframework.web.client.DefaultResponseErrorHandler
import java.io.InputStreamReader

internal class Back4appErrorHandler: DefaultResponseErrorHandler() {
    private val log = logger()
    private val objectMapper = ObjectMapper()

    override fun handleError(response: ClientHttpResponse) {
        var rawResp: String? = null
        try {
            rawResp = InputStreamReader(response.body).readText()
            val error = objectMapper.readValue(rawResp, ErrorResponse::class.java)
            log.error("Back4app error status: {} response: {}", response.statusCode, error)
        } catch (ex: Exception) {
            log.error("Could not parse Back4app error response {}", rawResp ?: response.statusCode, ex)
        }
        super.handleError(response)
    }

    internal class ErrorResponse
        @JsonCreator
        constructor (@JsonProperty("code") code: Int, @JsonProperty("error") private val message: String?) {
        private val code: Back4appErrorType? = Back4appErrorType.fromCode(code)

        override fun toString(): String {
            return "ErrorResponse(message=$message, code=$code)"
        }

    }
}