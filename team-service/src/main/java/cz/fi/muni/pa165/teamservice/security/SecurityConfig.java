package cz.fi.muni.pa165.teamservice.security;

import cz.fi.muni.pa165.teamservice.security.filters.APIKeyFilter;
import cz.fi.muni.pa165.teamservice.security.filters.ActuatorTokenFilter;
import cz.fi.muni.pa165.teamservice.security.providers.APIKeyAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.util.List;

@Configuration
@Profile({ "!test", "security-test" })
public class SecurityConfig {

	public static final String SCOPE_TEST_WRITE = "SCOPE_test_write";

	public static final String SCOPE_TEST_READ = "SCOPE_test_read";

	public static final String SCOPE_INTERNAL_READ = "SCOPE_internal_read";

	private final ActuatorTokenFilter actuatorTokenfilter;

	@Autowired
	public SecurityConfig(ActuatorTokenFilter actuatorTokenfilter) {
		this.actuatorTokenfilter = actuatorTokenfilter;
	}

	@Bean
	@Order(1)
	SecurityFilterChain prometheusFilterChain(HttpSecurity http) throws Exception {
		http.securityMatcher("/actuator/**")
			.authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
			.addFilterBefore(actuatorTokenfilter, BasicAuthenticationFilter.class)
			.csrf(AbstractHttpConfigurer::disable);

		return http.build();
	}

	@Bean
	@Order(2)
	SecurityFilterChain defaultFilterChain(HttpSecurity http, APIKeyFilter apiKeyFilter) throws Exception {
		http.authorizeHttpRequests(x -> x
			.requestMatchers("/", "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/v3/api-docs.yaml")
			.permitAll()
			.requestMatchers(APIKeyFilter.REQUEST_MATCHER)
			.hasAnyAuthority(SCOPE_INTERNAL_READ, SCOPE_TEST_READ)
			.requestMatchers(HttpMethod.POST, "/**")
			.hasAuthority(SCOPE_TEST_WRITE)
			.requestMatchers(HttpMethod.DELETE, "/**")
			.hasAuthority(SCOPE_TEST_WRITE)
			.requestMatchers(HttpMethod.PUT, "/**")
			.hasAuthority(SCOPE_TEST_WRITE)
			.requestMatchers(HttpMethod.GET, "/**")
			.hasAnyAuthority(SCOPE_TEST_READ, SCOPE_TEST_WRITE))
			.addFilterBefore(apiKeyFilter, BasicAuthenticationFilter.class)
			.oauth2ResourceServer(server -> server.opaqueToken(Customizer.withDefaults()));

		return http.build();
	}

	@Bean
	public AuthenticationManager authenticationManager(APIKeyAuthenticationProvider apiKeyProvider) {
		return new ProviderManager(List.of(apiKeyProvider));
	}

	@Bean
	public APIKeyFilter apiTokenFilter(AuthenticationManager authenticationManager) {
		return new APIKeyFilter(authenticationManager);
	}

}
