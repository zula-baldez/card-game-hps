package com.example.personalaccount.config;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HazelcastConfiguration {
    @Bean
    public HazelcastInstance hazelcastInstance() {
        final ClientConfig config = new ClientConfig();
        config.setClusterName("dev");
        config.getNetworkConfig().getKubernetesConfig().setEnabled(true)
                .setProperty("namespace", "default")
                .setProperty("service-name", "hz-hazelcast");

        return HazelcastClient.newHazelcastClient(config);
    }

}
