import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.1.0"
    id("io.spring.dependency-management") version "1.1.0"
    id("org.jetbrains.kotlin.plugin.lombok") version "1.8.0"
    kotlin("jvm") version "1.8.21"
    kotlin("plugin.spring") version "1.8.21"
    id("org.jetbrains.kotlin.plugin.jpa") version "1.5.21"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.7.10"
    id("com.google.protobuf") version "0.8.19"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

extra["springCloudVersion"] = "2022.0.4"

dependencies {
    implementation(project(":common"))
    testImplementation(project(":common"))
    implementation ("org.springframework.boot:spring-boot-starter-websocket")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign:4.1.3")
    implementation("org.springframework.boot:spring-boot-configuration-processor:3.1.0")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    implementation("org.springframework.boot:spring-boot-starter-data-rest:")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.3.3")
    implementation("org.springframework.boot:spring-boot-starter-validation:3.3.3")
    implementation("org.springframework.security:spring-security-core:6.3.0")
    implementation("org.springframework.security:spring-security-oauth2-client:6.3.3")
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")

    implementation("org.springframework.security:spring-security-config:6.2.0")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.2")
    implementation("org.liquibase:liquibase-core:4.22.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("com.google.code.gson:gson:2.10.1")
    compileOnly("org.projectlombok:lombok:1.18.26")
    developmentOnly("org.springframework.boot:spring-boot-devtools:3.0.4")
    annotationProcessor("org.projectlombok:lombok:1.18.26")
    implementation("org.postgresql:postgresql:42.7.4")

    implementation("org.apache.tomcat:tomcat-annotations-api:11.0.0-M9")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.2")

    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")

    implementation("org.springframework.security:spring-security-messaging:6.1.1")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework:spring-websocket:6.0.10")
    implementation("org.springframework:spring-messaging:6.0.10")

    compileOnly("org.projectlombok:lombok:1.18.20")
    annotationProcessor("org.projectlombok:lombok:1.18.20")

    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.0")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.7.0")
    testImplementation("org.assertj:assertj-core:3.17.2")
    testImplementation("org.mockito:mockito-core:3.5.13")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")
    testImplementation("org.webjars:sockjs-client:1.5.1")
    testImplementation("org.webjars:stomp-websocket:2.3.4")
    testImplementation("org.springframework.boot:spring-boot-starter-test:2.7.2")
    testImplementation("org.testcontainers:postgresql:1.20.1")
    testImplementation("org.testcontainers:junit-jupiter:1.20.1")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.0")
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


tasks.withType<Test> {
    useJUnitPlatform()
}
