package br.com.somestudy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI().info(new Info().title("RESTful API with Java 21 and Spring Boot 4").version("v1")
				.description("The best description possible").termsOfService("https//github.com/hyukiody")
				.license(new License().name("Apache 2.0").url("https//github.com/hyukiody")));
	}

}
