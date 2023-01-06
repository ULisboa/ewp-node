package pt.ulisboa.ewp.node.api.actuator.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@ConditionalOnProperty(value = "actuator.security.password")
@Order(5)
public class ActuatorApiSecurityConfig extends WebSecurityConfigurerAdapter {

  public static final String BASE_PATH_URI = "/actuator";

  private static final String ROLE_ACTUATOR = "ACTUATOR";

  @Value("${actuator.security.username}")
  private String username;

  @Value("${actuator.security.password}")
  private String password;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .antMatcher(BASE_PATH_URI + "/**")
        .authorizeRequests()
        .antMatchers(BASE_PATH_URI + "/**").hasRole(ROLE_ACTUATOR)
        .anyRequest().authenticated()
        .and()
        .httpBasic()
        .and()
        .csrf().disable();
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.inMemoryAuthentication()
        .withUser(username).password(password).roles(ROLE_ACTUATOR);
  }

}
