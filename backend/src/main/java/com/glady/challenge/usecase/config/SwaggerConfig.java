package com.glady.challenge.usecase.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public GroupedOpenApi api() {
        return GroupedOpenApi
                .builder()
                .group("Main group")
                .packagesToScan("com.glady.challenge.usecase.openapi.api")
                .pathsToMatch("/api/**")
                .build();

    }
}
