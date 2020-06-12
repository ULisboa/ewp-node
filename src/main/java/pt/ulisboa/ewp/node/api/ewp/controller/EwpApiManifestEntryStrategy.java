package pt.ulisboa.ewp.node.api.ewp.controller;

import eu.erasmuswithoutpaper.api.architecture.ManifestApiEntryBase;
import java.util.Optional;

public abstract class EwpApiManifestEntryStrategy {

  /**
   * Returns the manifest entry for a given HEI. If the given HEI does not implement the API then it
   * returns an empty result.
   */
  public abstract Optional<ManifestApiEntryBase> getManifestEntry(String heiId, String baseUrl);
}
