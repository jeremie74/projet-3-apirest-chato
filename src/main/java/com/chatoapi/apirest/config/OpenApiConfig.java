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
                                .info(new Info()
                                                .title("Chato API")
                                                .version("v1")
                                                .description("""
                                                                Pour tester les routes protégées :
                                                                1. Faites un appel à l'endpoint **/login** avec vos identifiants.
                                                                2. Récupérez le `token` JWT dans la réponse.
                                                                3. Cliquez sur **Authorize** en haut à droite.
                                                                4. Entrez `Bearer <votre_token>` puis validez.

                                                                Les routes publiques (permitAll) sont testables directement.
                                                                """))
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
