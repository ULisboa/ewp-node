package pt.ulisboa.ewp.node.api.actuator.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import pt.ulisboa.ewp.node.AbstractIntegrationTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestPropertySource(properties = {
        "actuator.security.username=actuator",
        "actuator.security.password={bcrypt}$2a$10$HmxuRyMdk5DEAcXg95QrR.NpV5inrl7RMN868bzhWosQhS.J.OnKC" // test123
})
public class ActuatorSecurityIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private TestRestTemplate template;

    @Test
    public void givenInvalidAuthToActuator_shouldFailWith401() {
        ResponseEntity<String> result = template.withBasicAuth("invalid", "invalid")
                .getForEntity("/actuator", String.class);
        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
    }

    @Test
    public void givenValidAuthToActuator_shouldSucceedWith200() {
        ResponseEntity<String> result = template.withBasicAuth("actuator", "test123")
                .getForEntity("/actuator", String.class);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

}
