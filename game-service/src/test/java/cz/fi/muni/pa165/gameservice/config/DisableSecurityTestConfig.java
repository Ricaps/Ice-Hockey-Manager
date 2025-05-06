package cz.fi.muni.pa165.gameservice.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@TestConfiguration
public class DisableSecurityTestConfig {

	@Bean
	@Primary
	public SecurityFilterChain testSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.authorizeHttpRequests(request -> request.anyRequest().permitAll());
		httpSecurity.csrf(AbstractHttpConfigurer::disable);
		httpSecurity.oauth2ResourceServer(AbstractHttpConfigurer::disable);

		return httpSecurity.build();
	}

}
