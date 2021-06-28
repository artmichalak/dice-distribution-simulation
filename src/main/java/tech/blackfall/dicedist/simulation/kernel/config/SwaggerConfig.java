package tech.blackfall.dicedist.simulation.kernel.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class SwaggerConfig {

  @Bean
  OpenAPI simulationApi() {
    return new OpenAPI()
        .info(new Info().title("Dice Distribution Simulator")
            .description("Used to run a random simulation")
            .version("v1.0.0"));
  }
}
