package pt.ulisboa.ewp.node.api.host.forward.ewp;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.GroupedOpenApi;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiConstants;

@Configuration
public class ForwardEwpApiConfiguration {

  public static final String API_VERSION = "0.1.0";

  @Bean
  public GroupedOpenApi forwardEwpOpenApi() {
    String[] pathsToMatch = {ForwardEwpApiConstants.API_BASE_URI + "/**"};
    return GroupedOpenApi.builder()
        .setGroup("forward-ewp")
        .pathsToMatch(pathsToMatch)
        .addOpenApiCustomiser(new ForwardEwpOpenApiCustomiser())
        .build();
  }

  private static class ForwardEwpOpenApiCustomiser implements OpenApiCustomiser {

    @Override
    public void customise(OpenAPI openApi) {
      final String securitySchemeName = "bearerAuth";
      openApi.addSecurityItem(new SecurityRequirement().addList(securitySchemeName));
      openApi
          .getComponents()
          .addSecuritySchemes(
              securitySchemeName,
              new SecurityScheme()
                  .name(securitySchemeName)
                  .type(SecurityScheme.Type.HTTP)
                  .description(
                      "JWT token with \"iss\" claim filled with the host's code. "
                          + "The token shall be encoded using the host's secret")
                  .scheme("bearer")
                  .bearerFormat("JWT"));
    }
  }
}