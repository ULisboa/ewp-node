package pt.ulisboa.ewp.node.api.admin.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

import pt.ulisboa.ewp.node.api.admin.security.handler.AdminApiAccessDeniedResponseHandler;
import pt.ulisboa.ewp.node.api.admin.utils.AdminApiConstants;
import pt.ulisboa.ewp.node.api.common.security.logging.MDCAuthenticationFilter;
import pt.ulisboa.ewp.node.config.security.SecurityProperties;

@Configuration
@Order(1)
public class AdminApiSecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired private SecurityProperties securityProperties;

  @Autowired private AdminApiAccessDeniedResponseHandler accessDeniedHandler;

  @Autowired private AdminApiUserRolesPopulator userRolesPopulator;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.antMatcher(AdminApiConstants.API_BASE_URI + "**")
        .httpBasic()
        .realmName("API")
        .and()
        .cors()
        .and()
        .csrf()
        .disable()
        .authorizeRequests()
        .antMatchers(AdminApiConstants.API_BASE_URI + "**")
        .authenticated()
        .and()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .exceptionHandling()
        .accessDeniedHandler(accessDeniedHandler);

    http.addFilter(
        new AdminApiJwtTokenAuthenticationFilter(
            authenticationManager(),
            userRolesPopulator,
            securityProperties.getApi().getAdmin().getSecret()));
    http.addFilterAfter(new MDCAuthenticationFilter(), AdminApiJwtTokenAuthenticationFilter.class);
  }
}
