package pt.ulisboa.ewp.node;

import org.junit.AfterClass;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = EwpNodeApplication.class)
@ActiveProfiles(profiles = {"dev", "test"})
@ContextConfiguration(classes = EwpNodeApplication.class)
public abstract class AbstractTest {

  @AfterClass
  public static void destroy() {}
}
