package com.example.gamehandlerservice.kafkaconnections

import com.example.common.kafkaconnections.GameUpdateEvent
import com.example.common.kafkaconnections.KafkaProperties
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.support.serializer.JsonSerializer

@Configuration
class KafkaGameUpdateEventProducerConfig {
    @Bean
    fun producerFactory(kafkaProperties: KafkaProperties): ProducerFactory<String, GameUpdateEvent> {
        val configProps: MutableMap<String, Any?> = HashMap()
        configProps[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = kafkaProperties.bootstrapServers
        configProps[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        configProps[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = JsonSerializer::class.java
        return DefaultKafkaProducerFactory(configProps)
    }

    @Bean
    fun kafkaTemplate(producerFactory: ProducerFactory<String, GameUpdateEvent>): KafkaTemplate<String, GameUpdateEvent> {
        return KafkaTemplate(producerFactory)
    }
}