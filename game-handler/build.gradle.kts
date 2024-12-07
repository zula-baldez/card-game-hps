import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.2.1"
    id("io.spring.dependency-management") version "1.1.0"
    id("org.jetbrains.kotlin.plugin.lombok") version "1.8.0"
    kotlin("jvm") version "1.8.21"
    kotlin("plugin.spring") version "1.8.21"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.7.10"
    id("com.google.protobuf") version "0.8.19"
    id("org.sonarqube") version "5.1.0.4882"
    id("jacoco")
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17
extra["springCloudVersion"] = "2023.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":common"))
    testImplementation(project(":common"))
    implementation("io.github.resilience4j:resilience4j-circuitbreaker:2.2.0")
    implementation("org.springframework.kafka:spring-kafka:3.3.0")

    implementation("org.springframework.cloud:spring-cloud-starter-config:4.1.3")
    implementation("org.springframework.cloud:spring-cloud-starter-bootstrap:4.1.4")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign:4.1.3")
    implementation("com.playtika.reactivefeign:feign-reactor-spring-cloud-starter:4.2.1")
    implementation("org.springframework.boot:spring-boot-configuration-processor:3.2.1")
    implementation("org.springframework.boot:spring-boot-starter-validation:3.3.3")
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")

    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.2")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    compileOnly("org.projectlombok:lombok:1.18.26")
    developmentOnly("org.springframework.boot:spring-boot-devtools:3.0.4")
    annotationProcessor("org.projectlombok:lombok:1.18.26")

    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.2")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    implementation("org.apache.tomcat:tomcat-annotations-api:11.0.0-M9")
    implementation("org.aspectj:aspectjrt:1.9.7")
    implementation("org.springframework.cloud:spring-cloud-starter-loadbalancer:4.1.4")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework:spring-websocket:6.1.14")
    implementation("org.springframework:spring-messaging:6.1.14")
    testImplementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.18.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.18.0")
    implementation("org.springframework.security:spring-security-messaging:6.3.4")
    implementation("org.springframework.boot:spring-boot-starter-security:3.3.5")

    compileOnly("org.projectlombok:lombok:1.18.20")
    annotationProcessor("org.projectlombok:lombok:1.18.20")
    testImplementation("org.assertj:assertj-core:3.17.2")
    testImplementation("org.mockito:mockito-core:3.5.13")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")
    testImplementation("org.webjars:sockjs-client:1.5.1")
    testImplementation("org.webjars:stomp-websocket:2.3.4")
    testImplementation("org.springframework.boot:spring-boot-starter-test:2.7.2")

}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
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
        "**/GameHandlerApplication.kt",
        "**/kafkaconnections/**"
    )
    properties {
        property("sonar.projectKey", "game_handler")
        property("sonar.projectName", "Game Handler")
        property("sonar.host.url", System.getenv("SONAR_HOST_URL") ?: "")
        property("sonar.login", "admin")
        property("sonar.password", "penki")
        property("sonar.sourceEncoding", "UTF-8")
        property("sonar.coverage.exclusions", exclusions)
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
