import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.2.1"
    id("io.spring.dependency-management") version "1.1.0"
    id("org.jetbrains.kotlin.plugin.lombok") version "1.8.0"
    kotlin("jvm")
    kotlin("plugin.spring") version "1.8.21"
    id("org.jetbrains.kotlin.plugin.jpa") version "1.5.21"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.7.10"
    id("org.sonarqube") version "5.1.0.4882"
    id("jacoco")
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":common"))
    testImplementation(project(":common"))

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.cloud:spring-cloud-starter-circuitbreaker-reactor-resilience4j:3.1.2")
    implementation("org.springframework.cloud:spring-cloud-starter-config:4.1.3")
    implementation("org.springframework.cloud:spring-cloud-starter-bootstrap:4.1.4")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign:4.1.3")
    implementation("com.playtika.reactivefeign:feign-reactor-spring-cloud-starter:4.2.1")

    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client:4.1.3")
    implementation("org.springframework.boot:spring-boot-configuration-processor:3.2.1")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.3.3")
    implementation("org.springframework.boot:spring-boot-starter-validation:3.3.3")
    implementation("org.springframework.security:spring-security-core:6.3.0")
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")

    implementation("org.springframework.security:spring-security-config:6.2.0")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.2")
    implementation("org.liquibase:liquibase-core:4.22.0")
    implementation("org.postgresql:postgresql:42.7.4")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:2.3.0")

    compileOnly("org.projectlombok:lombok:1.18.20")
    annotationProcessor("org.projectlombok:lombok:1.18.20")

    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.3")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.9.3")
    testImplementation("org.assertj:assertj-core:3.17.2")
    testImplementation("org.mockito:mockito-core:3.5.13")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.2.1")
    testImplementation("org.testcontainers:postgresql:1.20.2")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.testcontainers:junit-jupiter:1.20.2")
    testImplementation("org.springframework.security:spring-security-test:6.3.4")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.3")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

sonar {
    val exclusions = listOf(
        "**/security/**",
        "**/config/**",
        "**/AuthServiceApplication.kt"
    )
    properties {
        property("sonar.projectKey", "auth_service")
        property("sonar.projectName", "Auth Service")
        property("sonar.host.url", System.getenv("SONAR_HOST_URL") ?: "")
        property("sonar.login", System.getenv("SONAR_LOGIN") ?: "")
        property("sonar.password", System.getenv("SONAR_UI_PASSWORD") ?: "")
        property("sonar.sourceEncoding", "UTF-8")
        property("sonar.coverage.exclusions",exclusions)
    }
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport)
}
tasks.jacocoTestReport {
    reports {
        xml.required = true
    }
    dependsOn(tasks.test)
    classDirectories.setFrom(files(classDirectories.files.map {
        fileTree(it).apply {
            exclude("**/config/**")
            exclude("**/security/**")
        }
    }))

}

tasks.withType<Test> {
    useJUnitPlatform()
}

