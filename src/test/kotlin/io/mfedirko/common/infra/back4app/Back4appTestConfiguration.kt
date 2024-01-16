package io.mfedirko.common.infra.back4app

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.containers.DockerComposeContainer
import java.io.File

@TestConfiguration
@Import(Back4appConfig::class)
@ActiveProfiles("back4app")
class Back4appTestConfiguration {

    companion object {
        val back4appContainer = DockerComposeContainer(
            File("src/test/resources/parse-docker-compose.yml")
        ).withExposedService("postgres", 5432)
        .withExposedService("parse", 1337)

        init {
            back4appContainer.start()
        }
    }
}