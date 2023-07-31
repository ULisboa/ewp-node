package pt.ulisboa.ewp.node.plugin.manager.host;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import org.aopalliance.aop.Advice;
import org.pf4j.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.host.plugin.skeleton.HostPlugin;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.HostProvider;
import pt.ulisboa.ewp.node.config.plugins.PluginsProperties;
import pt.ulisboa.ewp.node.plugin.initializer.HostPluginInitializer;

public class Pf4jHostPluginManager extends AbstractHostPluginManager {

  private static final Logger LOG = LoggerFactory.getLogger(Pf4jHostPluginManager.class);

  private final PluginManager pf4jPluginManager;

  public Pf4jHostPluginManager(
          PluginsProperties pluginsProperties,
      HostPluginInitializer hostPluginInitializer,
      CustomDefaultPluginManager customDefaultPluginManager) {
    super(pluginsProperties, hostPluginInitializer);
    this.pf4jPluginManager = customDefaultPluginManager;
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
  protected Collection<HostProvider> getAllProvidersOfPlugin(HostPlugin hostPlugin) {
    return this.pf4jPluginManager.getExtensions(
        HostProvider.class, hostPlugin.getWrapper().getPluginId());
  }

  @Service
  public static class CustomDefaultPluginManager extends DefaultPluginManager {

    public CustomDefaultPluginManager(PluginsProperties pluginsProperties) {
      super(Path.of(pluginsProperties.getPath()));
    }

    @Override
    protected PluginLoader createPluginLoader() {
      return new CompoundPluginLoader().add(new CustomDefaultPluginLoader(this));
    }
  }

  private static class CustomDefaultPluginLoader extends DefaultPluginLoader {

    public CustomDefaultPluginLoader(PluginManager pluginManager) {
      super(pluginManager);
    }

    @Override
    protected PluginClassLoader createPluginClassLoader(
        Path pluginPath, PluginDescriptor pluginDescriptor) {
      return new CustomPluginClassLoader(
          pluginManager, pluginDescriptor, getClass().getClassLoader());
    }
  }

  private static class CustomPluginClassLoader extends PluginClassLoader {

    public CustomPluginClassLoader(
        PluginManager pluginManager, PluginDescriptor pluginDescriptor, ClassLoader parent) {
      super(pluginManager, pluginDescriptor, parent);
    }

    @Override
    public Class<?> loadClass(String className) throws ClassNotFoundException {
      if (className.equals(Advice.class.getName())) {
        return getParent().loadClass(className);
      }

      return super.loadClass(className);
    }
  }
}
