package com.example.securytyapp.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;

@OpenAPIDefinition(
        info = @io.swagger.v3.oas.annotations.info.Info(
                title = "Alexandre's Spring security authentication API",
                version = "1.0",
                description = "API de gestion d'authentification et d'autorisation"
        ),
        servers = {
                @io.swagger.v3.oas.annotations.servers.Server(
                        description = "Localhost",
                        url = "http://localhost:8080"
                )

        },

        security = {
                @io.swagger.v3.oas.annotations.security.SecurityRequirement(
                        name = "bearerAuth",
                        scopes = {"global"}
                ),
        }


)
public class OpenApiConfig {
}
