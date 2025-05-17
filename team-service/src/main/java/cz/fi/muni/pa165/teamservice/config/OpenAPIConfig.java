package cz.fi.muni.pa165.teamservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@Configuration
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class OpenAPIConfig {

	public static final String SECURITY_SCHEME_BEARER = "Bearer";

	private final BuildProperties buildProperties;

	@Autowired
	public OpenAPIConfig(BuildProperties buildProperties) {
		this.buildProperties = buildProperties;
	}

	@Bean
	public OpenAPI configureOpenAPI() {
		return new OpenAPI().info(new Info().title(buildProperties.getName())
			.version(buildProperties.getVersion())
			.description(buildProperties.get("description")));
	}

	/**
	 * Add security definitions to generated openapi.yaml.
	 */
	@Bean
	public OpenApiCustomizer openAPICustomizer() {
		return openApi -> {
			openApi.getComponents()
				.addSecuritySchemes(SECURITY_SCHEME_BEARER, new SecurityScheme().type(SecurityScheme.Type.HTTP)
					.scheme(SECURITY_SCHEME_BEARER)
					.description(
							"Please provide valid access token. You can get it via OAuth2 client at http://localhost:8084"));
			openApi.addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_BEARER));
			addGlobalResponse(openApi, "401",
					new ApiResponse().description("You are not authorized to access this resource!"));
			addGlobalResponse(openApi, "403",
					new ApiResponse().description("You don't have required permissions to access this resource!"));
		};
	}

	private void addGlobalResponse(OpenAPI openApi, String operationName, ApiResponse apiResponse) {
		openApi.getPaths()
			.values()
			.forEach(path -> path.readOperations()
				.forEach(operation -> operation.getResponses().addApiResponse(operationName, apiResponse)));
	}

}
