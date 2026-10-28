package com.rogers.api;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableCaching
@SpringBootApplication
@EnableFeignClients
@OpenAPIDefinition(info = @Info(title = "Residential Experience Oneview Common Layer Template", version = "1.0", description = "Spring Boot microservice template for Residential Experience Oneview Common Layer. Provides APIs and utilities to support Domain Services"))
public class Application {
  public static void main(final String[] args) {
    System.setProperty("spring.devtools.restart.enabled", "false");
    SpringApplication.run(Application.class, args);
  }
}
