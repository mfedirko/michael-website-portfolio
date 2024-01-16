package io.mfedirko.common.infra.dynamodb

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient

@Configuration
@Profile("aws")
class DynamoDbConfig {
    @Bean
    fun dynamoDbClient(): DynamoDbClient {
        val credentialsProvider: AwsCredentialsProvider = DefaultCredentialsProvider.create()
        return DynamoDbClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(credentialsProvider)
            .build()
    }

    @Bean
    fun dynamoDbEnhancedClient(dynamoDbClient: DynamoDbClient): DynamoDbEnhancedClient {
        return DynamoDbEnhancedClient.builder()
            .dynamoDbClient(dynamoDbClient)
            .build()
    }
}