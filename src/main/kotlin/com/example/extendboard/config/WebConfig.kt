package com.example.extendboard.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.io.File

@Configuration
class WebConfig : WebMvcConfigurer {
    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        val uploadDir = File("uploads").absolutePath
        registry.addResourceHandler("/images/**")
            .addResourceLocations("file:$uploadDir/")
    }
}