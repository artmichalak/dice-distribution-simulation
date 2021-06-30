package tech.blackfall.dicedist.simulation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.CacheControl;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.WebContentInterceptor;
import tech.blackfall.dicedist.simulation.domain.StringToSimulationModeConverter;

@Configuration
@EnableWebMvc
@EnableJpaRepositories("tech.blackfall.dicedist.simulation.adapter.database")
class SimulationConfig implements WebMvcConfigurer {

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    var interceptor = new WebContentInterceptor();
    interceptor.addCacheMapping(CacheControl.noCache().mustRevalidate(), "/*/simulation");
    registry.addInterceptor(interceptor);
  }

  @Bean
  MethodValidationPostProcessor methodValidationPostProcessor() {
    return new MethodValidationPostProcessor();
  }

  @Override
  public void addFormatters(FormatterRegistry registry) {
    registry.addConverter(new StringToSimulationModeConverter());
  }
}
