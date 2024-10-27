import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.2.1"
    id("io.spring.dependency-management") version "1.1.0"
    kotlin("jvm") version "1.8.21"
    kotlin("plugin.spring") version "1.8.21"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.cloud:spring-cloud-starter-loadbalancer:4.1.4")

    implementation("org.springframework.cloud:spring-cloud-config-server:4.1.3")

    implementation("org.springframework.cloud:spring-cloud-starter-openfeign:4.1.3")
    implementation("org.springframework.boot:spring-boot-configuration-processor:3.2.1")
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
