package com.carrot.market.global.config.kafka;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import com.carrot.market.chat.presentation.dto.Entry;
import com.carrot.market.chat.presentation.dto.Message;
import com.carrot.market.global.util.KafkaConstant;

@EnableKafka
@Configuration
public class ListenerConfiguration {

	@Bean
	ConcurrentKafkaListenerContainerFactory<String, Message> kafkaMessageListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, Message> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(messageConsumerFactory());
		return factory;
	}

	@Bean
	public ConsumerFactory<String, Message> messageConsumerFactory() {
		Map<String, Object> configurations = new HashMap<>();
		configurations.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaConstant.KAFKA_BROKER);
		configurations.put(ConsumerConfig.GROUP_ID_CONFIG, KafkaConstant.GROUP_ID);
		configurations.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		configurations.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
		configurations.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		return new DefaultKafkaConsumerFactory<>(configurations, new StringDeserializer(),
			new JsonDeserializer<>(Message.class));
	}

	@Bean
	ConcurrentKafkaListenerContainerFactory<String, Entry> kafkaEntryListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, Entry> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(entryConsumerFactory());
		return factory;
	}

	@Bean
	public ConsumerFactory<String, Entry> entryConsumerFactory() {
		Map<String, Object> configurations = new HashMap<>();
		configurations.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaConstant.KAFKA_BROKER);
		configurations.put(ConsumerConfig.GROUP_ID_CONFIG, KafkaConstant.GROUP_ID);
		configurations.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		configurations.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
		configurations.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		return new DefaultKafkaConsumerFactory<>(configurations, new StringDeserializer(),
			new JsonDeserializer<>(Entry.class));
	}

}
