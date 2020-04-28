package pt.ulisboa.ewp.node.api.ewp.controller;

import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import eu.erasmuswithoutpaper.api.architecture.ManifestApiEntryBase;

public abstract class EwpApiManifestEntryStrategy {

  @Autowired private Logger log;

  @Value("${baseContextPath}")
  private String baseContextPath;

  public abstract ManifestApiEntryBase getManifestEntry(HttpServletRequest request);

  protected String getBaseUrl(HttpServletRequest request) {
    try {
      URL requestUrl = new URL(request.getRequestURL().toString());
      return new URL(
              "https://"
                  + requestUrl.getHost()
                  + (requestUrl.getPort() == -1 ? "" : ":" + requestUrl.getPort())
                  + baseContextPath)
          .toString();
    } catch (MalformedURLException e) {
      log.error("Failed to get base URL", e);
    }
    return null;
  }
}
