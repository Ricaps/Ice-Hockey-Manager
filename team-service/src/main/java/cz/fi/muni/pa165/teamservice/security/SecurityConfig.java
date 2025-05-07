package cz.fi.muni.pa165.teamservice.security;

import cz.fi.muni.pa165.teamservice.security.filters.ActuatorTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

@Configuration
@Profile({ "!test", "security-test" })
public class SecurityConfig {

	public static final String SCOPE_TEST_WRITE = "SCOPE_test_write";

	public static final String SCOPE_TEST_READ = "SCOPE_test_read";

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
	SecurityFilterChain defaultFilterChain(HttpSecurity http) throws Exception {
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
