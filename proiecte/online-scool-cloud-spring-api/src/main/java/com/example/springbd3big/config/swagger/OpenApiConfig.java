package com.example.springbd3big.config.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(AppDocsProperties.class)
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI(AppDocsProperties docsProperties) {
        String usersSummary = docsProperties.getBootstrapUsers().isEmpty()
                ? "No bootstrap users configured."
                : String.join(", ", docsProperties.getBootstrapUsers());

        String description = """
                %s

                Bootstrap users available after the initial Liquibase migration:
                %s

                Default password for all bootstrap users:
                %s

                Protected endpoints use Bearer JWT tokens and each operation documents the required permission.
                """.formatted(
                docsProperties.getDescription(),
                usersSummary,
                docsProperties.getDefaultPassword()
        );

        String schemeName = docsProperties.getBearerSchemeName();

        return new OpenAPI()
                .info(new Info()
                        .title(docsProperties.getTitle())
                        .version(docsProperties.getVersion())
                        .description(description))
                .components(new Components()
                        .addSecuritySchemes(schemeName, new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Paste the JWT token returned by /auth/login.")));
    }
}
