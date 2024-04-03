package pt.ulisboa.ewp.node.api.admin.security;

import com.google.common.collect.Lists;
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
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import pt.ulisboa.ewp.node.api.admin.security.handler.AdminApiAuthenticationFailureHandler;
import pt.ulisboa.ewp.node.api.admin.security.handler.AdminApiAuthenticationSuccessHandler;
import pt.ulisboa.ewp.node.api.admin.security.handler.AdminApiLogoutSuccessHandler;
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
  private final AdminApiLogoutSuccessHandler logoutSuccessHandler;

  public AdminApiSecurityConfig(
      @Value("${admin.security.username}") String username,
      @Value("${admin.security.password}") String password,
      AdminApiAuthenticationSuccessHandler authenticationSuccessHandler,
      AdminApiAuthenticationFailureHandler authenticationFailureHandler,
      AdminApiLogoutSuccessHandler logoutSuccessHandler) {
    this.username = username;
    this.password = password;
    this.authenticationSuccessHandler = authenticationSuccessHandler;
    this.authenticationFailureHandler = authenticationFailureHandler;
    this.logoutSuccessHandler = logoutSuccessHandler;
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
        .logout(
            logout ->
                logout
                    .logoutUrl(AdminApiConstants.API_BASE_URI + "auth/logout")
                    .logoutSuccessHandler(logoutSuccessHandler)
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID"))
        .exceptionHandling(
            exceptionHandling ->
                exceptionHandling.authenticationEntryPoint(
                    new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
        .csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
        .cors(cors -> cors.configurationSource(corsConfigurationSource()));
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.inMemoryAuthentication().withUser(username).password(password).roles(ROLE_ADMIN);
  }

  private CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(
        Lists.newArrayList("http://localhost:4200", "https://ewp-node:4200"));
    configuration.setAllowedMethods(Lists.newArrayList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowCredentials(true);
    configuration.setAllowedHeaders(Lists.newArrayList("XSRF-TOKEN"));
    configuration.setMaxAge(60L);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration(AdminApiConstants.API_BASE_URI + "**", configuration);
    return source;
  }
}
