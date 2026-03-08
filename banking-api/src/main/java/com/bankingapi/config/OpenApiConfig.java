package com.bankingapi.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Banking & Finance Tracker API")
                        .description("""
                                A production-grade REST API for managing bank accounts, tracking transactions,
                                setting budgets, and analyzing personal finances.
                                
                                ## Features
                                - **JWT Authentication** — Secure stateless auth with access & refresh tokens
                                - **Account Management** — Create and manage multiple bank accounts
                                - **Transactions** — Deposits, withdrawals, transfers with full audit trail
                                - **Budget Tracking** — Set category budgets with alert thresholds
                                - **Analytics** — Financial summaries, spending trends, and monthly reports
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Banking API")
                                .email("api@bankingtracker.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Enter your JWT token")));
    }
}
