package pt.ulisboa.ewp.node.api.actuator.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@ConditionalOnProperty(value = "actuator.security.password")
public class ActuatorApiSecurityConfig {

  public static final String BASE_PATH_URI = "/actuator";

  private static final String ROLE_ACTUATOR = "ACTUATOR";

  @Value("${actuator.security.username}")
  private String username;

  @Value("${actuator.security.password}")
  private String password;

  @Bean
  @Order(5)
  public SecurityFilterChain actuatorFilterChain(HttpSecurity http) throws Exception {
    // @formatter:off
    http.securityMatcher(BASE_PATH_URI + "/**")

        .authorizeHttpRequests(auth -> auth
            .anyRequest().hasRole(ROLE_ACTUATOR))

        .userDetailsService(inMemoryUserDetailsManager())

        .httpBasic(Customizer.withDefaults())
        .csrf(AbstractHttpConfigurer::disable);
    // @formatter:on

    return http.build();
  }

  private InMemoryUserDetailsManager inMemoryUserDetailsManager() {
    UserDetails userDetails =
        User.withUsername(username).password(password).roles(ROLE_ACTUATOR).build();
    return new InMemoryUserDetailsManager(userDetails);
  }

}
