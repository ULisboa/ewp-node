package pt.ulisboa.ewp.node.config.bootstrap;

import java.util.List;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Scope;

@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
@ConfigurationProperties(prefix = "bootstrap")
public class BootstrapProperties {

  private List<HostBootstrapProperties> hosts;

  public List<HostBootstrapProperties> getHosts() {
    return hosts;
  }

  public void setHosts(List<HostBootstrapProperties> hosts) {
    this.hosts = hosts;
  }
}
