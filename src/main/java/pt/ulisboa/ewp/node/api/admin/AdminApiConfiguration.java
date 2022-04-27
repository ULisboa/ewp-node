package pt.ulisboa.ewp.node.api.admin;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.GroupedOpenApi;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdminApiConfiguration {

  @Bean
  public GroupedOpenApi adminOpenApi() {
    String[] packagesToScan = {getClass().getPackage().getName()};
    return GroupedOpenApi.builder()
        .group("admin")
        .packagesToScan(packagesToScan)
        .addOpenApiCustomiser(new AdminOpenApiCustomiser())
        .build();
  }

  private static class AdminOpenApiCustomiser implements OpenApiCustomiser {

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
                  .description("JWT token encoded using the admin's secret")
                  .scheme("bearer")
                  .bearerFormat("JWT"));
    }
  }
}
