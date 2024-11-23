package com.example.gamehandlerservice.kafkaconnections


import com.example.common.kafkaconnections.KafkaProperties
import com.example.common.kafkaconnections.RoomUpdateEvent
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.support.serializer.JsonDeserializer


@Configuration
@Lazy
class KafkaRoomUpdateEventConsumerConfig {
    @Bean
    @Lazy
    fun consumerFactory(kafkaProperties: KafkaProperties): ConsumerFactory<String, RoomUpdateEvent> {
        val configProps: MutableMap<String, Any> = HashMap()
        configProps[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = kafkaProperties.bootstrapServers
        configProps[ConsumerConfig.GROUP_ID_CONFIG] = "gr"
        configProps[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        configProps[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = JsonDeserializer::class.java
        val valueDeserializer = JsonDeserializer(RoomUpdateEvent::class.java)
        valueDeserializer.addTrustedPackages("*")

        return DefaultKafkaConsumerFactory(
            configProps,
            StringDeserializer(),
            valueDeserializer
        )
    }

    @Bean
    @Lazy
    fun kafkaListenerContainerFactory(consumerFactory: ConsumerFactory<String, RoomUpdateEvent>): ConcurrentKafkaListenerContainerFactory<String, RoomUpdateEvent> {
        val factory: ConcurrentKafkaListenerContainerFactory<String, RoomUpdateEvent> =
            ConcurrentKafkaListenerContainerFactory()
        factory.consumerFactory = consumerFactory
        return factory
    }
}
