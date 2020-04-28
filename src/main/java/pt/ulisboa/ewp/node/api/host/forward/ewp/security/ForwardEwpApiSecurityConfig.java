package pt.ulisboa.ewp.node.api.host.forward.ewp.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import pt.ulisboa.ewp.node.api.common.security.logging.MDCAuthenticationFilter;
import pt.ulisboa.ewp.node.api.host.forward.ewp.security.filter.ForwardEwpApiJwtTokenAuthenticationFilter;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiConstants;
import pt.ulisboa.ewp.node.domain.repository.HostRepository;

@Configuration
@Order(2)
public class ForwardEwpApiSecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired private HostRepository repository;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.antMatcher(ForwardEwpApiConstants.API_BASE_URI + "**")
        .httpBasic()
        .realmName("API")
        .and()
        .cors()
        .and()
        .csrf()
        .disable()
        .authorizeRequests()
        .antMatchers(ForwardEwpApiConstants.API_BASE_URI + "**")
        .authenticated()
        .and()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    http.addFilter(
        new ForwardEwpApiJwtTokenAuthenticationFilter(authenticationManager(), repository));
    http.addFilterAfter(
        new MDCAuthenticationFilter(), ForwardEwpApiJwtTokenAuthenticationFilter.class);
  }
}
