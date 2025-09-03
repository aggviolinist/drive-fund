package com.drivefundproject.drive_fund.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "WEKA DOO",
        description = "API Endpoints Testing",
        version = "1.0.0",
        contact = @Contact(
            name = "Dev-Team",
            url = "https://www.dev-team.com/",
            email = "dev-team@gmail.com"
        )
    ),
    servers = {
        @Server(url = "http://localhost:8080", description = "Local server")
    }
)
public class OpenApiConfig {
}