package com.example

import com.example.avatarsservice.model.S3Config
import jakarta.validation.Validation
import jakarta.validation.Validator
import jakarta.validation.ValidatorFactory
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class DtoInitializationTest {
    private val validator: Validator

    init {
        val factory: ValidatorFactory = Validation.buildDefaultValidatorFactory()
        validator = factory.validator
    }

    @Test
    fun `S3Config should initialize properly`() {
        val s3config = S3Config("test","test","test","test","test")
        
        Assertions.assertEquals("test", s3config.endpoint)
        Assertions.assertEquals("test", s3config.region)
        Assertions.assertEquals("test", s3config.bucket)
        Assertions.assertEquals("test", s3config.accessKeyId)
        Assertions.assertEquals("test", s3config.secretAccessKey)
    }

}