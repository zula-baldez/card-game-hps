package com.example.common

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.PostgreSQLContainer.POSTGRESQL_PORT
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ContextConfiguration(initializers = [E2EDbInit.Initializer::class])
@TestPropertySource(properties = ["spring.config.location=classpath:application.yml"])
class E2EDbInit {
    companion object {
        @Container
        val postgresSQLContainer: PostgreSQLContainer<*> = PostgreSQLContainer("postgres:16-alpine")
            .withReuse(true)
            .withDatabaseName("test-db")
            .waitingFor(Wait.forLogMessage(".*database system is ready to accept connections.*", 1))
    }

    internal class Initializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
        override fun initialize(configurableApplicationContext: ConfigurableApplicationContext) {
            println(postgresSQLContainer.host)
            TestPropertyValues.of(
                "POSTGRES_HOST=" + postgresSQLContainer.host,
                "POSTGRES_PORT=" + postgresSQLContainer.getMappedPort(POSTGRESQL_PORT),
                "POSTGRES_USER=" + postgresSQLContainer.username,
                "POSTGRES_PASSWORD=" + postgresSQLContainer.password,
                "POSTGRES_DB=" + postgresSQLContainer.databaseName,
            ).applyTo(configurableApplicationContext.environment)
        }
    }
}
