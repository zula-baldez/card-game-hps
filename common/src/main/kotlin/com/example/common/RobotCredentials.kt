package com.example.common

import jakarta.validation.constraints.NotBlank
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@ConfigurationProperties("service.robot")
class RobotCredentials(
    @NotBlank
    val username: String,
    @NotBlank
    val password: String
)