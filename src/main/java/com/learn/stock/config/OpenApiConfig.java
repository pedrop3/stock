package com.learn.stock.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI stockOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Stock Management API")
                        .description("API for managing inventory, movements, alerts, turnover, and ABC classification")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Pedro Santos")
                                .url("https://exemplo.com"))
                        .license(new License().name("Apache 2.0").url("http://springdoc.org"))
                );
    }
}