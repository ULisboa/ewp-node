package pt.ulisboa.ewp.node.config.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class LoggingConfiguration {

  /**
   * Injects a logger configured for the class requiring an injected logger.
   */
  @Bean
  @Scope(BeanDefinition.SCOPE_PROTOTYPE)
  Logger logger(InjectionPoint injectionPoint) {
    var field = injectionPoint.getField();
    if (field != null) {
      return LoggerFactory.getLogger(field.getDeclaringClass());
    }

    var methodParameter = injectionPoint.getMethodParameter();
    if (methodParameter != null) {
      return LoggerFactory.getLogger(methodParameter.getContainingClass());
    }

    throw new IllegalStateException("Invalid injection point" + injectionPoint);
  }

}
