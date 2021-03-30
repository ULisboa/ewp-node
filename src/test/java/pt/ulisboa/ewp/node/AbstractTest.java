package pt.ulisboa.ewp.node;

import org.junit.jupiter.api.AfterAll;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles(profiles = {"dev", "test"})
public abstract class AbstractTest {

  @AfterAll
  public static void destroy() {}
}
