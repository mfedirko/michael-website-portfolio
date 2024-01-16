package io.mfedirko.common.infra.dynamodb

import io.mfedirko.common.infra.dynamodb.DynamoDbTestConfiguration.LocalStackConfiguration
import io.mfedirko.fixture.DynamoContactRequests
import io.mfedirko.fixture.DynamoLessons
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.testcontainers.containers.Network
import org.testcontainers.containers.localstack.LocalStackContainer
import org.testcontainers.utility.DockerImageName
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughput

@TestConfiguration
@TestPropertySource
@Import(LocalStackConfiguration::class, DynamoDbConfig::class)
@ActiveProfiles("aws")
class DynamoDbTestConfiguration {
    @Autowired
    private lateinit var dynamoDb: DynamoDbEnhancedClient
    
    @PostConstruct
    fun initTables() {
        initContactRequests()
        initLessonsTable()
    }

    private fun initLessonsTable() {
        initDynamoTable(DynamoLesson::class.java, DynamoLesson.TABLE, DynamoLessons.DATA)
    }

    private fun initContactRequests() {
        initDynamoTable(DynamoContactRequest::class.java, DynamoContactRequest.TABLE, DynamoContactRequests.DATA)
    }

    private fun <T> initDynamoTable(clazz: Class<T>, name: String, data: Collection<T>?) {
        val table = dynamoDb.table(name, TableSchema.fromBean(clazz))
        try {
            table.describeTable().table() // table already exists if no exception
        } catch (ex: Exception) { // table does not exist - create and populate
            table.createTable {
                it.provisionedThroughput { prov: ProvisionedThroughput.Builder ->
                    prov.readCapacityUnits(1L).writeCapacityUnits(1L)
                }
            }
            data!!.forEach { table.putItem(it) }
        }
    }

    @TestConfiguration
    class LocalStackConfiguration {
        @Bean
        @Primary
        fun dynamoDbClientTest(): DynamoDbClient {
            return DynamoDbClient.builder()
                .region(Region.US_EAST_1)
                .endpointOverride(localStack.getEndpointOverride(LocalStackContainer.Service.DYNAMODB))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build()
        }

        companion object {
            val localStack: LocalStackContainer = LocalStackContainer(DockerImageName.parse("localstack/localstack:1.0.4.1.nodejs18"))
                .withServices(LocalStackContainer.Service.DYNAMODB)
                .withNetworkAliases("localstack")
                .withNetwork(
                    Network.builder().createNetworkCmdModifier { it.withName("test-net") }
                        .build())

            init {
                localStack.start()
            }
        }
    }
}