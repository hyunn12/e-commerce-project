package com.loopers.interfaces.consumer;

import com.loopers.domain.EventLog;
import com.loopers.infrastructure.EventLogJpaRepository;
import com.loopers.interfaces.dto.KafkaMessage;
import com.loopers.utils.DatabaseCleanUp;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@Testcontainers
@SpringBootTest
class EventLogConsumerTest {

    // test container
    @Container
    static final KafkaContainer KAFKA =
            new KafkaContainer(
                    DockerImageName.parse("confluentinc/cp-kafka:7.3.2.arm64")
                            .asCompatibleSubstituteFor("apache/kafka"));
    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", KAFKA::getBootstrapServers);
        registry.add("spring.kafka.consumer.bootstrap-servers", KAFKA::getBootstrapServers);
        registry.add("spring.kafka.producer.bootstrap-servers", KAFKA::getBootstrapServers);
    }
    @BeforeAll
    static void setupTopics() {
        try (AdminClient admin = AdminClient.create(Map.of(
                AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA.getBootstrapServers()
        ))) {
            admin.createTopics(List.of(new NewTopic("catalog-events", 1, (short) 1)));
        }
    }

    // sut --
    @Autowired
    private KafkaTemplate<Object, Object> kafkaTemplate;

    // orm--
    @Autowired
    private EventLogJpaRepository eventLogJpaRepository;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @Test
    void consumeMessage_saveEventLog() {
        // arrange
        Map<String, Object> payload = Map.of("productId", 1L, "userId", 1L);
        KafkaMessage<Map<String, Object>> message = KafkaMessage.of(payload);

        // act
        kafkaTemplate.send("catalog-events", "1", message);

        // assert
        await().pollDelay(2, TimeUnit.SECONDS)
                .pollInterval(500, TimeUnit.MILLISECONDS)
                .atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    assertThat(eventLogJpaRepository.existsByEventId(message.eventId())).isTrue();
                    EventLog log = eventLogJpaRepository.findByEventId(message.eventId()).orElseThrow();
                    assertThat(log.getPayload()).contains("productId");
                });
    }
}
