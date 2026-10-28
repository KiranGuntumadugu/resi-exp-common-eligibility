package com.rogers.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger Configuration class
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI(@Value("${application.description}") String appDescription,
                                 @Value("${api.version}") String appVersion, @Value("${api.title}") String title) {

        return new OpenAPI()
                .info(new Info()
                        .title(title)
                        .version(appVersion)
                        .description(appDescription)
                        .termsOfService("https://Rogers.com/terms-of-use")
                        .contact(new Contact().name("Rogers").url("Rogers.com")));
    }

}

