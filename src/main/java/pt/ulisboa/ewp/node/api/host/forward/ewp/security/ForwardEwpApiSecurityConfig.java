package pt.ulisboa.ewp.node.api.host.forward.ewp.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import pt.ulisboa.ewp.node.api.common.filter.security.logging.MDCAuthenticationFilter;
import pt.ulisboa.ewp.node.api.host.forward.ewp.security.jwt.filter.ForwardEwpApiJwtTokenAuthenticationFilter;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiConstants;
import pt.ulisboa.ewp.node.domain.repository.host.forward.ewp.client.HostForwardEwpApiClientRepository;
import pt.ulisboa.ewp.node.utils.http.converter.xml.Jaxb2HttpMessageConverter;

@Configuration
@Order(2)
public class ForwardEwpApiSecurityConfig extends WebSecurityConfigurerAdapter {

  private final HostForwardEwpApiClientRepository repository;

  private final Jaxb2HttpMessageConverter jaxb2HttpMessageConverter;

  public ForwardEwpApiSecurityConfig(HostForwardEwpApiClientRepository repository,
      Jaxb2HttpMessageConverter jaxb2HttpMessageConverter) {
    this.repository = repository;
    this.jaxb2HttpMessageConverter = jaxb2HttpMessageConverter;
  }

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
        new ForwardEwpApiJwtTokenAuthenticationFilter(authenticationManager(), repository,
            jaxb2HttpMessageConverter));
    http.addFilterAfter(
        new MDCAuthenticationFilter(), ForwardEwpApiJwtTokenAuthenticationFilter.class);
  }
}
