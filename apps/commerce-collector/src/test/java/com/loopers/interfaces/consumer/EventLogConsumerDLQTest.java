package com.loopers.interfaces.consumer;

import com.loopers.domain.EventLog;
import com.loopers.domain.EventLogService;
import com.loopers.interfaces.dto.KafkaMessage;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

@SpringBootTest
@Testcontainers
class EventLogConsumerDLQTest {

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
            admin.createTopics(List.of(
                    new NewTopic("catalog-events", 1, (short) 1),
                    new NewTopic("catalog-events.DLT", 1, (short) 1)
            ));
        }
    }

    @Autowired
    private KafkaTemplate<Object, Object> kafkaTemplate;

    @MockitoBean
    private EventLogService eventLogService;

    @BeforeEach
    void setUp() {
        doThrow(new RuntimeException("DB ERROR"))
                .when(eventLogService)
                .save(any(EventLog.class));
    }

    @DisplayName("예외가 발생하면, DLQ로 메시지가 전송된다.")
    @Test
    void whenExceptionOccurs_thenMessageSendToDLQ() {
        // arrange
        KafkaMessage<Map<String, Object>> message =
                KafkaMessage.of(Map.of("productId", 1L, "userId", 1L), "LIKE_ADD");

        // act
        kafkaTemplate.send("catalog-events", "1", message);

        // assert
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-consumer");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(List.of("catalog-events.DLT"));

            await().atMost(15, TimeUnit.SECONDS)
                    .untilAsserted(() -> {
                        ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
                        assertThat(records.count()).isGreaterThan(0);
                    });
        }
    }
}
