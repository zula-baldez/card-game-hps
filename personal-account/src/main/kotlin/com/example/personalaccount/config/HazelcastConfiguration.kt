package com.example.personalaccount.config

import com.hazelcast.client.HazelcastClient
import com.hazelcast.client.config.ClientConfig
import com.hazelcast.core.HazelcastInstance
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class HazelcastConfiguration {
    @Bean
    fun hazelcastInstance(): HazelcastInstance {
        val config = ClientConfig()
        config.setClusterName("dev")
        config.networkConfig.kubernetesConfig.setEnabled(true)
            .setProperty("namespace", "default")
            .setProperty("service-name", "hz-hazelcast")

        return HazelcastClient.newHazelcastClient(config)
    }
}
