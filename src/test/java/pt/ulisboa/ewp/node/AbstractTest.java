package pt.ulisboa.ewp.node;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = EwpNodeApplication.class)
@ActiveProfiles(profiles = {"dev", "test"})
@ContextConfiguration(classes = EwpNodeApplication.class)
public abstract class AbstractTest {

  @AfterAll
  public static void destroy() {}
}
