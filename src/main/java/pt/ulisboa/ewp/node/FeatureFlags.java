package pt.ulisboa.ewp.node;

import java.util.Arrays;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class FeatureFlags {

  public static final String FEATURE_FLAG_WITH_SCHEDULERS = "with-schedulers";
  public static final String FEATURE_FLAG_NO_SCHEDULERS = "no-schedulers";

  private final Environment environment;

  public FeatureFlags(Environment environment) {
    this.environment = environment;
  }

  public boolean isSchedulersEnabled() {
    return Arrays.stream(this.environment.getActiveProfiles())
        .noneMatch(p -> p.equalsIgnoreCase(FEATURE_FLAG_NO_SCHEDULERS));
  }

}
