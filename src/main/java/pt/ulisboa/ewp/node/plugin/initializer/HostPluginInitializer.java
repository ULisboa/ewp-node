package pt.ulisboa.ewp.node.plugin.initializer;

import pt.ulisboa.ewp.host.plugin.skeleton.HostPlugin;

/**
 * Class that initializes plugins.
 * It may configure initial dependencies, etc.
 */
public interface HostPluginInitializer {

  void init(HostPlugin plugin);
}
