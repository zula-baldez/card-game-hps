package com.example.authservice.kafkaconnections

import com.example.common.kafkaconnections.AccountRegisteredEvent
import com.example.common.kafkaconnections.KafkaProperties
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.support.serializer.JsonDeserializer

@Configuration
class KafkaAccountRegistrationEventConsumerConfig {
    @Bean
    fun consumerFactory(kafkaProperties: KafkaProperties): ConsumerFactory<String, AccountRegisteredEvent> {
        val configProps: MutableMap<String, Any> = HashMap()
        configProps[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = kafkaProperties.bootstrapServers
        configProps[ConsumerConfig.GROUP_ID_CONFIG] = "gr"
        configProps[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        configProps[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = JsonDeserializer::class.java
        val valueDeserializer = JsonDeserializer(AccountRegisteredEvent::class.java)
        valueDeserializer.addTrustedPackages("*")

        return DefaultKafkaConsumerFactory(
            configProps,
            StringDeserializer(),
            valueDeserializer
        )
    }

    @Bean
    fun kafkaListenerContainerFactory(consumerFactory: ConsumerFactory<String, AccountRegisteredEvent>): ConcurrentKafkaListenerContainerFactory<String, AccountRegisteredEvent> {
        val factory: ConcurrentKafkaListenerContainerFactory<String, AccountRegisteredEvent> =
            ConcurrentKafkaListenerContainerFactory()
        factory.consumerFactory = consumerFactory
        return factory
    }
}