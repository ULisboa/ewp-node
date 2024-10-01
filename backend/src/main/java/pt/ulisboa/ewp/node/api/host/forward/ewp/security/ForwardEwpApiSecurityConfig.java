package pt.ulisboa.ewp.node.api.host.forward.ewp.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import pt.ulisboa.ewp.node.api.common.filter.security.logging.MDCAuthenticationFilter;
import pt.ulisboa.ewp.node.api.host.forward.ewp.security.jwt.filter.ForwardEwpApiJwtTokenAuthenticationFilter;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiConstants;
import pt.ulisboa.ewp.node.domain.repository.host.forward.ewp.client.HostForwardEwpApiClientRepository;
import pt.ulisboa.ewp.node.utils.http.converter.xml.Jaxb2HttpMessageConverter;

@Configuration
public class ForwardEwpApiSecurityConfig {

  private final HostForwardEwpApiClientRepository repository;

  private final Jaxb2HttpMessageConverter jaxb2HttpMessageConverter;

  public ForwardEwpApiSecurityConfig(HostForwardEwpApiClientRepository repository,
      Jaxb2HttpMessageConverter jaxb2HttpMessageConverter) {
    this.repository = repository;
    this.jaxb2HttpMessageConverter = jaxb2HttpMessageConverter;
  }

  @Bean
  @Order(2)
  public SecurityFilterChain forwardEwpFilterChain(HttpSecurity http) throws Exception {
    // @formatter:off
    http.securityMatcher(ForwardEwpApiConstants.API_BASE_URI + "**")
        .httpBasic(httpBasic -> httpBasic.realmName("API"))
        .cors(Customizer.withDefaults())
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
        .sessionManagement(
            sessionManagement ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
    // @formatter:on

    http.addFilterBefore(
        new ForwardEwpApiJwtTokenAuthenticationFilter(repository, jaxb2HttpMessageConverter),
        UsernamePasswordAuthenticationFilter.class);
    http.addFilterAfter(
        new MDCAuthenticationFilter(), ForwardEwpApiJwtTokenAuthenticationFilter.class);

    return http.build();
  }
}
