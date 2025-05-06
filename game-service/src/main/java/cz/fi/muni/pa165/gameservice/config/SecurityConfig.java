package cz.fi.muni.pa165.gameservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Profile({ "!test", "security-test" })
public class SecurityConfig {

	public static final String SCOPE_TEST_WRITE = "SCOPE_test_write";

	public static final String SCOPE_TEST_READ = "SCOPE_test_read";

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(x -> x
			.requestMatchers("/", "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/v3/api-docs.yaml")
			.permitAll()
			.requestMatchers(HttpMethod.POST, "/**")
			.hasAuthority(SCOPE_TEST_WRITE)
			.requestMatchers(HttpMethod.DELETE, "/**")
			.hasAuthority(SCOPE_TEST_WRITE)
			.requestMatchers(HttpMethod.PUT, "/**")
			.hasAuthority(SCOPE_TEST_WRITE)
			.requestMatchers(HttpMethod.GET, "/**")
			.hasAnyAuthority(SCOPE_TEST_READ, SCOPE_TEST_WRITE))
			.oauth2ResourceServer(server -> server.opaqueToken(Customizer.withDefaults()));

		return http.build();
	}

}
