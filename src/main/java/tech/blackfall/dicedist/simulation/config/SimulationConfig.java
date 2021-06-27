package tech.blackfall.dicedist.simulation.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.WebContentInterceptor;

@Configuration
@EnableWebMvc
class SimulationConfig implements WebMvcConfigurer {

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    var interceptor = new WebContentInterceptor();
    interceptor.addCacheMapping(CacheControl.noCache(), "/*/simulation");
    registry.addInterceptor(interceptor);
  }

  @Bean
  MethodValidationPostProcessor methodValidationPostProcessor() {
    return new MethodValidationPostProcessor();
  }

  @Bean
  OpenAPI simulationApi() {
    return new OpenAPI()
        .info(new Info().title("Dice Distribution Simulator")
            .description("Used to run a random simulation")
            .version("v1.0.0"));
  }
}
