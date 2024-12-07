package com.example.common.config

import jakarta.validation.constraints.NotBlank
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("service.robot")
data class RobotCredentialsProperties(
    @NotBlank
    val username: String?,
    @NotBlank
    val password: String?
)