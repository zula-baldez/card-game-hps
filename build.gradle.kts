import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.google.protobuf.gradle.*

plugins {
    id("org.springframework.boot") version "3.1.0"
    id("io.spring.dependency-management") version "1.1.0"
    id ("org.jetbrains.kotlin.plugin.lombok") version "1.8.0"
    kotlin("jvm") version "1.8.21"
    kotlin("plugin.spring") version "1.8.21"
    id ("org.jetbrains.kotlin.plugin.jpa") version "1.5.21"
    id ("org.jetbrains.kotlin.plugin.allopen") version "1.7.10"
    id("com.google.protobuf") version "0.8.19"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-configuration-processor:3.1.0")
    implementation("org.springframework.boot:spring-boot-starter-data-rest:")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.3.3")
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

    api("io.grpc:grpc-kotlin-stub:1.2.1")
    api("io.grpc:grpc-stub:1.44.0")
    runtimeOnly("io.grpc:grpc-netty:1.44.0")
    sourceSets {
        main {
            proto {
                srcDir("src/main/protobuf")
            }
        }
    }
    compileOnly("org.projectlombok:lombok:1.18.20")
    annotationProcessor("org.projectlombok:lombok:1.18.20")

    runtimeOnly("org.postgresql:postgresql")
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
