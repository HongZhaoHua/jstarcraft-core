package com.jstarcraft.core.event.kafka;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Future;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.salesforce.kafka.test.KafkaTestServer;
import com.salesforce.kafka.test.KafkaTestUtils;

public class KafkaTestCase {

    protected static final Logger logger = LoggerFactory.getLogger(KafkaTestCase.class);

    private static KafkaTestServer server;

    static {
        Properties properties = new Properties();
        properties.put("broker.id", "1");
        properties.put("port", "9092");
        // 注意Maven的配置
        // https://github.com/salesforce/kafka-junit/tree/master/kafka-junit4
        server = new KafkaTestServer(properties);
    }

    @BeforeClass
    public static void start() throws Exception {
        server.start();
    }

    @AfterClass
    public static void stop() throws Exception {
        server.stop();
    }

    @Test
    public void test() throws Exception {
        KafkaTestUtils utility = new KafkaTestUtils(server);

        // Create a topic
        final String topicName = "KafkaTestCase";
        utility.createTopic(topicName, 1, (short) 1);
        final int partitionId = 0;
        // Define our message
        final String messageKey = "key";
        final String messageValue = "value";

        // Create producer
        try (final KafkaProducer<String, String> kafkaProducer = utility.getKafkaProducer(StringSerializer.class, StringSerializer.class)) {
            // Define the record we want to produce
            final ProducerRecord<String, String> producerRecord = new ProducerRecord<>(topicName, partitionId, messageKey, messageValue);
            // Produce it & wait for it to complete.
            final Future<RecordMetadata> future = kafkaProducer.send(producerRecord);
            kafkaProducer.flush();
            while (!future.isDone()) {
                Thread.sleep(500L);
            }
        }

        // Create consumer
        try (final KafkaConsumer<String, String> kafkaConsumer = utility.getKafkaConsumer(StringDeserializer.class, StringDeserializer.class)) {
            final List<TopicPartition> topicPartitionList = new ArrayList<>();
            for (final PartitionInfo partitionInfo : kafkaConsumer.partitionsFor(topicName)) {
                topicPartitionList.add(new TopicPartition(partitionInfo.topic(), partitionInfo.partition()));
            }
            kafkaConsumer.assign(topicPartitionList);
            kafkaConsumer.seekToBeginning(topicPartitionList);
            // Pull records from kafka, keep polling until we get nothing back
            ConsumerRecords<String, String> consumerRecords;
            do {
                consumerRecords = kafkaConsumer.poll(Duration.ofSeconds(2));
                for (ConsumerRecord<String, String> record : consumerRecords) {
                    // Validate
                    Assert.assertEquals(messageKey, record.key());
                    Assert.assertEquals(messageValue, record.value());
                }
            } while (!consumerRecords.isEmpty());
        }
    }

}
