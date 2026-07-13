package com.expenseTracker.shrawn.shared.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI spendWiseOpenApi() {
        return new OpenAPI()
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("SpendWise API")
                .description("REST API documentation for the SpendWise expense management platform.")
                .version("v1")
                .contact(contact())
                .license(license());
    }

    private Contact contact() {
        return new Contact()
                .name("SpendWise Backend Team")
                .email("support@spendwise.local");
    }

    private License license() {
        return new License()
                .name("Private/Internal Use");
    }
}
