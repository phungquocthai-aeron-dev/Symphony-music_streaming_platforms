package com.phungquocthai.symphony.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

//  Có thể nhận giá trị tham số từ biến môi trường để cấu hình động
@Bean
public OpenAPI customOpenAPI() {

    final String securitySchemeName = "bearerAuth";

    return new OpenAPI()
            .info(new Info()
                    .title("Symphony Music Streaming API")
                    .version("1.0.0")
                    .description("REST API for Symphony Music Streaming Platform")
                    .contact(new Contact()
                            .name("Phùng Quốc Thái")
                            .email("phungquocthai@example.com"))
                    .license(new License()
                            .name("Apache 2.0")
                            .url("https://www.apache.org/licenses/LICENSE-2.0"))
            )

            .servers(List.of(
                    new Server()
                            .url("http://localhost:8080/symphony")
                            .description("Local - Spring Boot"),

                    new Server()
                            .url("http://localhost:8080/symphony")
                            .description("Local - Docker")
            ))

            .components(
                    new Components()
                            .addSecuritySchemes(
                                    securitySchemeName,
                                    new SecurityScheme()
                                            .name("Authorization")
                                            .type(SecurityScheme.Type.HTTP)
                                            .scheme("bearer")
                                            .bearerFormat("JWT")
                            )
            )

            .security(
                    List.of(new SecurityRequirement()
                            .addList(securitySchemeName))
            )
            ;
    }


    @Bean
    public GroupedOpenApi songApi() {
        return GroupedOpenApi.builder()
                .group("Song")
//                .packagesToScan("com.phungquocthai.symphony.controller.song")
                .pathsToMatch("/song/**")
                .build();
    }

    @Bean
    public GroupedOpenApi singerApi() {
        return GroupedOpenApi.builder()
                .group("Singer")
                .pathsToMatch("/singer/**")
                .build();
    }

    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("User")
                .pathsToMatch("/user/**")
                .build();
    }

    @Bean
    public GroupedOpenApi authApi() {
        return GroupedOpenApi.builder()
                .group("Authentication")
                .pathsToMatch("/auth/**")
                .build();
    }

    @Bean
    public GroupedOpenApi albumApi() {
        return GroupedOpenApi.builder()
                .group("Album")
                .pathsToMatch("/album/**")
                .build();
    }

    @Bean
    public GroupedOpenApi homeApi() {
        return GroupedOpenApi.builder()
                .group("Home")
                .pathsToMatch("/home/**")
                .build();
    }

    @Bean
    public GroupedOpenApi paymentApi() {
        return GroupedOpenApi.builder()
                .group("Payment")
                .pathsToMatch("/api/payment/**")
                .build();
    }

    @Bean
    public GroupedOpenApi playlistApi() {
        return GroupedOpenApi.builder()
                .group("Playlist")
                .pathsToMatch("/playlist/**")
                .build();
    }

    @Bean
    public GroupedOpenApi notificationApi() {
        return GroupedOpenApi.builder()
                .group("Notification")
                .pathsToMatch("/notification/**")
                .build();
    }
}
