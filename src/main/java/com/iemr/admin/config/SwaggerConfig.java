package com.iemr.admin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration

public class SwaggerConfig {
    private static final String DEFAULT_SERVER_URL = "http://localhost:9090";

    @Bean
    public OpenAPI customOpenAPI(Environment env) {
        String devUrl = env.getProperty("api.dev.url", DEFAULT_SERVER_URL);
        String uatUrl = env.getProperty("api.uat.url", DEFAULT_SERVER_URL);
        String demoUrl = env.getProperty("api.demo.url", DEFAULT_SERVER_URL);
        return new OpenAPI()
            .info(new Info().title("Admin API").version("version").description("Microservice for administration, configuration, user and role management, and service-level operations."))
            .addSecurityItem(new SecurityRequirement().addList("my security"))
            .components(new Components().addSecuritySchemes("my security",
                new SecurityScheme().name("my security").type(SecurityScheme.Type.HTTP).scheme("bearer")))
            .servers(java.util.Arrays.asList(
                new io.swagger.v3.oas.models.servers.Server().url(devUrl).description("Dev"),
                new io.swagger.v3.oas.models.servers.Server().url(uatUrl).description("UAT"),
                new io.swagger.v3.oas.models.servers.Server().url(demoUrl).description("Demo")
            ));
    }

}
