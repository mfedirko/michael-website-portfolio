package io.mfedirko.common.infra.back4app

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.boot.web.client.RestTemplateCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.http.client.ClientHttpRequestInterceptor

@Configuration
@Profile("back4app")
internal class Back4appConfig(
    @param:Value("\${back4app.application-id}") private val appId: String,
    @param:Value("\${back4app.rest-api-key}") private val restApiKey: String
) {
    @Bean(name = ["back4appTemplate"])
    fun back4appRestTemplateBuilder(): RestTemplateBuilder {
        return RestTemplateBuilder(RestTemplateCustomizer { restTemplate ->
            restTemplate.interceptors.add(ClientHttpRequestInterceptor { request, body, execution ->
                with(request.headers) {
                    set("X-Parse-Application-Id", appId)
                    set("X-Parse-REST-API-Key", restApiKey)
                }
                execution.execute(request, body)
            })
        }).rootUri("https://parseapi.back4app.com")
          .errorHandler(Back4appErrorHandler())
    }
}