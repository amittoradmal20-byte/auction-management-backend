package com.auction.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "Bearer Authentication";

    @Bean
    public OpenAPI openAPI() {

        return new OpenAPI()

                .info(apiInfo())

                .addSecurityItem(
                        new SecurityRequirement()
                                .addList(SECURITY_SCHEME_NAME))

                .components(
                        new Components()
                                .addSecuritySchemes(
                                        SECURITY_SCHEME_NAME,
                                        securityScheme()));
    }

    private Info apiInfo() {

        return new Info()

                .title("Auction Management API")

                .description(
                        "Enterprise Auction Management System")

                .version("v1.0")

                .contact(
                        new Contact()
                                .name("Amit Toradmal")
                                .email("amit@example.com"))

                .license(
                        new License()
                                .name("Apache 2.0"));
    }

    private SecurityScheme securityScheme() {

        return new SecurityScheme()

                .name("Authorization")

                .type(SecurityScheme.Type.HTTP)

                .scheme("bearer")

                .bearerFormat("JWT");
    }
}