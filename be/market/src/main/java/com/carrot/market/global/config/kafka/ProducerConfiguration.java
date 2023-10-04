package com.carrot.market.global.config.kafka;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.carrot.market.chat.presentation.dto.Entry;
import com.carrot.market.chat.presentation.dto.Message;
import com.carrot.market.global.util.KafkaConstant;

@EnableKafka
@Configuration
@EnableTransactionManagement
public class ProducerConfiguration {

	@Bean
	public ProducerFactory<String, Message> messageProducerFactory() {
		DefaultKafkaProducerFactory<String, Message> factory = new DefaultKafkaProducerFactory<>(
			messageProducerConfigurations());
		factory.setTransactionIdPrefix("tx-");
		return factory;
	}

	@Bean
	public ProducerFactory<String, Entry> entryProducerFactory() {
		return new DefaultKafkaProducerFactory<>(entryProducerConfigurations());
	}

	@Bean
	public Map<String, Object> messageProducerConfigurations() {
		Map<String, Object> configurations = new HashMap<>();
		configurations.put(org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
			KafkaConstant.KAFKA_BROKER);
		configurations.put(org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
			StringSerializer.class);
		configurations.put(org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
			JsonSerializer.class);

		return configurations;
	}

	@Bean
	public Map<String, Object> entryProducerConfigurations() {
		Map<String, Object> configurations = new HashMap<>();
		configurations.put(org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
			KafkaConstant.KAFKA_BROKER);
		configurations.put(org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
			StringSerializer.class);
		configurations.put(org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
			JsonSerializer.class);
		return configurations;
	}

	@Bean
	public KafkaTemplate<String, Message> messageKafkaTemplate(
	) {
		return new KafkaTemplate<>(messageProducerFactory());
	}

	@Bean
	public KafkaTemplate<String, Entry> entryKafkaTemplate() {
		return new KafkaTemplate<>(entryProducerFactory());
	}
}
