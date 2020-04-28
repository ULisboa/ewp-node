package pt.ulisboa.ewp.node.api.ewp.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.header.HeaderWriterFilter;
import org.springframework.security.web.session.SessionManagementFilter;
import pt.ulisboa.ewp.node.api.common.security.logging.MDCAuthenticationFilter;
import pt.ulisboa.ewp.node.api.ewp.security.filter.EwpApiHttpSignatureAuthenticationFilter;
import pt.ulisboa.ewp.node.api.ewp.security.filter.EwpApiPreAuthenticationFilter;
import pt.ulisboa.ewp.node.api.ewp.security.filter.EwpApiResponseSignerFilter;
import pt.ulisboa.ewp.node.api.ewp.security.filter.EwpApiTlsCertificateAuthenticationFilter;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.config.security.SecurityProperties;
import pt.ulisboa.ewp.node.service.security.ewp.HttpSignatureService;

@Configuration
@Order(3)
public class EwpApiSecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired private HttpSignatureService httpSignatureService;

  @Autowired private SecurityProperties securityProperties;

  @Autowired private RegistryClient registryClient;

  @Autowired private Jaxb2Marshaller jaxb2Marshaller;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.antMatcher(EwpApiConstants.API_BASE_URI + "**")
        .cors()
        .and()
        .csrf()
        .disable()
        .authorizeRequests()
        .antMatchers(EwpApiConstants.API_BASE_URI + "manifest")
        .permitAll()
        .antMatchers(EwpApiConstants.API_BASE_URI + "**")
        .authenticated()
        .and()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .exceptionHandling()
        .authenticationEntryPoint(new CustomAuthenticationEntryPoint(jaxb2Marshaller));

    http.addFilterBefore(
        new EwpApiResponseSignerFilter(httpSignatureService), HeaderWriterFilter.class);

    http.addFilterBefore(
        new EwpApiPreAuthenticationFilter(jaxb2Marshaller), BasicAuthenticationFilter.class);
    http.addFilterAfter(
        new EwpApiHttpSignatureAuthenticationFilter(httpSignatureService),
        EwpApiPreAuthenticationFilter.class);
    http.addFilterAfter(
        new EwpApiTlsCertificateAuthenticationFilter(securityProperties, registryClient),
        EwpApiHttpSignatureAuthenticationFilter.class);

    http.addFilterAfter(new MDCAuthenticationFilter(), SessionManagementFilter.class);
  }

  private static class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private CustomAuthenticationEntryPoint(Jaxb2Marshaller jaxb2Marshaller) {}

    @Override
    public void commence(
        HttpServletRequest request,
        HttpServletResponse response,
        AuthenticationException authException) {
      response.setStatus(HttpStatus.UNAUTHORIZED.value());
    }
  }
}
