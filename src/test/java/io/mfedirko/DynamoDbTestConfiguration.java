package io.mfedirko;

import io.mfedirko.common.infra.dynamodb.DynamoLesson;
import io.mfedirko.fixture.DynamoContactRequests;
import io.mfedirko.common.infra.dynamodb.DynamoContactRequest;
import io.mfedirko.fixture.DynamoLessons;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.Collection;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.DYNAMODB;

@TestConfiguration
@TestPropertySource
@Import(DynamoDbTestConfiguration.LocalStackConfiguration.class)
public class DynamoDbTestConfiguration {
    @Autowired
    private DynamoDbEnhancedClient dynamoDb;


    @PostConstruct
    public void initTables() {
        initContactRequests();
        initLessonsTable();
    }

    private void initLessonsTable() {
        initDynamoTable(DynamoLesson.class, DynamoLesson.TABLE, DynamoLessons.DATA);
    }

    private void initContactRequests() {
        initDynamoTable(DynamoContactRequest.class, DynamoContactRequest.TABLE, DynamoContactRequests.DATA);
    }

    private <T> void initDynamoTable(Class<T> clazz, String name, Collection<T> data) {
        DynamoDbTable<T> table = dynamoDb.table(name, TableSchema.fromBean(clazz));
        table.createTable(t -> t
                .provisionedThroughput(prov -> prov.readCapacityUnits(1L).writeCapacityUnits(1L)));

        data.forEach(table::putItem);
    }

    @TestConfiguration
    public static class LocalStackConfiguration {
        static LocalStackContainer localStack =
                new LocalStackContainer(DockerImageName.parse("localstack/localstack:1.0.4.1.nodejs18"))
                        .withServices(DYNAMODB)
                        .withNetworkAliases("localstack")
                        .withNetwork(Network.builder().createNetworkCmdModifier(cmd -> cmd.withName("test-net")).build());

        static {
            localStack.start();
        }

        @Bean
        @Primary
        public DynamoDbClient dynamoDbClientTest() {
            return DynamoDbClient.builder()
                    .region(Region.US_EAST_1)
                    .endpointOverride(localStack.getEndpointOverride(DYNAMODB))
                    .credentialsProvider(DefaultCredentialsProvider.create())
                    .build();
        }
    }
}
