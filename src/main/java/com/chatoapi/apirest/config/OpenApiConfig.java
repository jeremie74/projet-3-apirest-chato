package com.chatoapi.apirest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfig {
        @Bean
        public OpenAPI api() {
                return new OpenAPI()
                                .info(new Info().title("Chato API").version("v1"))
                                // important: ton servlet-path est /api
                                .addServersItem(new Server().url("/api"))
                                .components(new Components().addSecuritySchemes("bearerAuth",
                                                new SecurityScheme()
                                                                .type(SecurityScheme.Type.HTTP)
                                                                .scheme("bearer")
                                                                .bearerFormat("JWT")))
                                // sécurité par défaut sur toutes les routes (sauf celles en permitAll)
                                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
        }
}
