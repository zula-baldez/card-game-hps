package com.example.common.config

import org.springframework.boot.context.properties.ConfigurationProperties
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

@ConfigurationProperties("rsa")
data class RsaKeyProperties(val publicKey : RSAPublicKey, val privateKey: RSAPrivateKey?)