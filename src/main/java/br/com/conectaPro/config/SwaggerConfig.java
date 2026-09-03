package br.com.conectaPro.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(
            new Info()
                .title("ConectaPro")
                .version("1.0")
                .description("API do ConectaPro")
                .contact(new Contact().name("ConectaPro").email("conectaPro@hotmail.com.br")));
  }

  @Bean
  public GroupedOpenApi customApi() {
    return GroupedOpenApi.builder()
        .group("api")
        .pathsToMatch("/api/**", "/auth/**")
        .pathsToExclude("/error", "/actuator/**")
        .build();
  }
}
