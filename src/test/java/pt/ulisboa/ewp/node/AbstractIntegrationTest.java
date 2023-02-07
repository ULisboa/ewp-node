package pt.ulisboa.ewp.node;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
    classes = {EwpNodeApplication.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = {"dev", "test"})
@ContextConfiguration(classes = EwpNodeApplication.class)
@Import(ValidationAutoConfiguration.class)
public abstract class AbstractIntegrationTest extends AbstractTest {

}
