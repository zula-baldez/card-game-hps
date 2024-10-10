package com.example.common

import org.junit.jupiter.api.TestInstance
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.TestPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.wait.strategy.Wait


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = ["spring.config.location=classpath:application.yml"])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class E2EDbInit {
    companion object {
        private val postgresSQLContainer: PostgreSQLContainer<*> = PostgreSQLContainer("postgres:15")
            .withReuse(false)
            .withDatabaseName("testdb")
            .waitingFor(Wait.forLogMessage(".*database system is ready to accept connections.*", 1))

        init {
            postgresSQLContainer.start()
        }

        @DynamicPropertySource
        @JvmStatic
        fun configureProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgresSQLContainer::getJdbcUrl)
            registry.add("spring.datasource.username", postgresSQLContainer::getUsername)
            registry.add("spring.datasource.password", postgresSQLContainer::getPassword)
        }
    }
}
