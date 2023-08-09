package pt.ulisboa.ewp.node.api.admin.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import pt.ulisboa.ewp.node.api.admin.security.handler.AdminApiAuthenticationFailureHandler;
import pt.ulisboa.ewp.node.api.admin.security.handler.AdminApiAuthenticationSuccessHandler;
import pt.ulisboa.ewp.node.api.admin.utils.AdminApiConstants;

@Configuration
@ConditionalOnProperty(value = "admin.security.password")
@Order(6)
public class AdminApiSecurityConfig extends WebSecurityConfigurerAdapter {

  private static final Logger LOG = LoggerFactory.getLogger(AdminApiSecurityConfig.class);

  private static final String ROLE_ADMIN = "ADMIN";

  private final String username;
  private final String password;
  private final AdminApiAuthenticationSuccessHandler authenticationSuccessHandler;
  private final AdminApiAuthenticationFailureHandler authenticationFailureHandler;

  public AdminApiSecurityConfig(
      @Value("${admin.security.username}") String username,
      @Value("${admin.security.password}") String password,
      AdminApiAuthenticationSuccessHandler authenticationSuccessHandler,
      AdminApiAuthenticationFailureHandler authenticationFailureHandler) {
    this.username = username;
    this.password = password;
    this.authenticationSuccessHandler = authenticationSuccessHandler;
    this.authenticationFailureHandler = authenticationFailureHandler;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    LOG.info("Initializing security for Admin endpoints");

    http.antMatcher(AdminApiConstants.API_BASE_URI + "**")
        .authorizeRequests()
        .antMatchers(AdminApiConstants.API_BASE_URI + "auth/login")
        .permitAll()
        .anyRequest()
        .hasRole(ROLE_ADMIN)
        .and()
        .formLogin(
            formLogin ->
                formLogin
                    .usernameParameter("username")
                    .passwordParameter("password")
                    .loginProcessingUrl(AdminApiConstants.API_BASE_URI + "auth/login")
                    .successHandler(authenticationSuccessHandler)
                    .failureHandler(authenticationFailureHandler))
        .exceptionHandling(
            exceptionHandling ->
                exceptionHandling.authenticationEntryPoint(
                    new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
        .csrf(AbstractHttpConfigurer::disable);
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.inMemoryAuthentication().withUser(username).password(password).roles(ROLE_ADMIN);
  }
}
