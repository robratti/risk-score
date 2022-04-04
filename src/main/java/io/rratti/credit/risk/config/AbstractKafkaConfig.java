package io.rratti.credit.risk.config;

import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import lombok.AllArgsConstructor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;
import org.springframework.core.env.Environment;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@AllArgsConstructor
public abstract class AbstractKafkaConfig <T> {
    protected final Environment environment;

    protected Map<String, Object> kafkaProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.IntegerSerde.class);
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde.class);
        props.put(
                AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG,
                environment.getProperty("spring.kafka.properties.schema.registry.url")
        );
        props.put(
                StreamsConfig.APPLICATION_ID_CONFIG,
                environment.getProperty("spring.kafka.properties.application-id")
        );

        return props;
    }

    public KafkaTemplate<Integer, T> getKafkaTemplate(String topic) {
        var kafkaTemplate = new KafkaTemplate<>(getProducerFactory());
        kafkaTemplate.setDefaultTopic(topic);

        return kafkaTemplate;
    }

    public KafkaTemplate<Integer, T> getJsonKafkaTemplate(String topic) {
        var kafkaTemplate = new KafkaTemplate<>(getJsonProducerFactory());
        kafkaTemplate.setDefaultTopic(topic);

        return kafkaTemplate;
    }

    public ProducerFactory<Integer, T> getProducerFactory() {
        return new DefaultKafkaProducerFactory<>(getProducerConfigs());
    }

    public Map<String, Object> getProducerConfigs() {
        var props = kafkaProps();
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        return props;
    }

    public ProducerFactory<Integer, T> getJsonProducerFactory() {
        return new DefaultKafkaProducerFactory<>(getJsonProducerConfigs());
    }

    public Map<String, Object> getJsonProducerConfigs() {
        var props = kafkaProps();
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return props;
    }
}
