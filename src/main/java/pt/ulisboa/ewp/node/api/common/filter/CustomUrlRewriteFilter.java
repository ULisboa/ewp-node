package pt.ulisboa.ewp.node.api.common.filter;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.tuckey.web.filters.urlrewrite.Conf;
import org.tuckey.web.filters.urlrewrite.UrlRewriteFilter;

@Component
@Order(Integer.MIN_VALUE + 1)
public class CustomUrlRewriteFilter extends UrlRewriteFilter {

  private static final String CONFIG_LOCATION = "classpath:/urlrewrite.xml";

  @Value(CONFIG_LOCATION)
  private Resource resource;

  private boolean configLoaded = false;

  @Override
  protected void loadUrlRewriter(FilterConfig filterConfig) throws ServletException {
    try {
      Conf conf =
          new Conf(
              filterConfig.getServletContext(),
              resource.getInputStream(),
              resource.getFilename(),
              "");
      checkConf(conf);
      this.configLoaded = true;
    } catch (IOException ex) {
      throw new ServletException(
          "Unable to load URL-rewrite configuration file from " + CONFIG_LOCATION, ex);
    }
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    if (this.configLoaded) {
      super.doFilter(request, response, chain);
    } else {
      chain.doFilter(request, response);
    }
  }
}
