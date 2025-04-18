package cz.fi.muni.pa165.userservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

	private final BuildProperties buildProperties;

	@Autowired
	public OpenApiConfig(BuildProperties buildProperties) {
		this.buildProperties = buildProperties;
	}

	@Bean
	public OpenAPI configureOpenAPI() {
		return new OpenAPI().info(new Info().title(buildProperties.getName())
			.version(buildProperties.getVersion())
			.description(buildProperties.get("description")));
	}

}