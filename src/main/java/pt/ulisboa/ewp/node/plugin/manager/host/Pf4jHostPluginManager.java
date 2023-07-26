package pt.ulisboa.ewp.node.plugin.manager.host;

import java.nio.file.Path;
import java.util.Collection;
import java.util.stream.Collectors;
import org.pf4j.DefaultPluginManager;
import org.pf4j.PluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import pt.ulisboa.ewp.host.plugin.skeleton.HostPlugin;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.HostProvider;
import pt.ulisboa.ewp.node.plugin.initializer.HostPluginInitializer;

public class Pf4jHostPluginManager extends AbstractHostPluginManager {

  private static final Logger LOG = LoggerFactory.getLogger(Pf4jHostPluginManager.class);

  private final PluginManager pf4jPluginManager;

  public Pf4jHostPluginManager(
      @Value("${plugins.path}") String pluginsPath, HostPluginInitializer hostPluginInitializer) {
    super(hostPluginInitializer);
    this.pf4jPluginManager = new CustomDefaultPluginManager(pluginsPath);
  }

  @Override
  protected void loadPlugins() {
    LOG.info(
        "Preparing to load plugins from path: {}",
        this.pf4jPluginManager.getPluginsRoot().toAbsolutePath());

    this.pf4jPluginManager.loadPlugins();
    this.pf4jPluginManager.startPlugins();
  }

  @Override
  protected Collection<HostPlugin> getAllPlugins() {
    Class<HostPlugin> classType = HostPlugin.class;
    return this.pf4jPluginManager.getPlugins().stream()
        .filter(p -> classType.isAssignableFrom(p.getPlugin().getClass()))
        .map(p -> classType.cast(p.getPlugin()))
        .collect(Collectors.toList());
  }

  @Override
  protected <T extends HostProvider> Collection<T> getProvidersOfPlugin(
      HostPlugin hostPlugin, Class<T> providerType) {
    return this.pf4jPluginManager.getExtensions(
        providerType, hostPlugin.getWrapper().getPluginId());
  }

  private static class CustomDefaultPluginManager extends DefaultPluginManager {

    public CustomDefaultPluginManager(
        @Value("${plugins.path}") String pluginsPath) {
      super(Path.of(pluginsPath));
    }
  }
}
