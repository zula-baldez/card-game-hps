package com.example.personalaccount.config

import com.example.common.dto.personalaccout.CreateAccountDto
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
class CreateAccountConsumerConfig {
    @Bean
    fun createAccountConsumerFactory(kafkaProperties: KafkaProperties): ConsumerFactory<String, CreateAccountDto> {
        val configProps: MutableMap<String, Any> = HashMap()
        configProps[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = kafkaProperties.bootstrapServers
        configProps[ConsumerConfig.GROUP_ID_CONFIG] = "gr"
        configProps[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        configProps[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = JsonDeserializer::class.java
        val valueDeserializer = JsonDeserializer(CreateAccountDto::class.java)
        valueDeserializer.addTrustedPackages("*")

        return DefaultKafkaConsumerFactory(
            configProps,
            StringDeserializer(),
            valueDeserializer
        )
    }

    @Bean
    fun createAccountKafkaListenerContainerFactory(consumerFactory: ConsumerFactory<String, CreateAccountDto>): ConcurrentKafkaListenerContainerFactory<String, CreateAccountDto> {
        val factory: ConcurrentKafkaListenerContainerFactory<String, CreateAccountDto> =
            ConcurrentKafkaListenerContainerFactory()
        factory.consumerFactory = consumerFactory
        return factory
    }
}