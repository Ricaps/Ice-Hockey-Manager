package cz.fi.muni.pa165.teamservice.security.providers;

import cz.fi.muni.pa165.teamservice.security.tokens.APIKeyAuthToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;

import static cz.fi.muni.pa165.teamservice.security.SecurityConfig.SCOPE_INTERNAL_READ;

@Component
public class APIKeyAuthenticationProvider implements AuthenticationProvider {

	@Value("${spring.security.api-key:#{null}}")
	private String token;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		var apiKey = (String) authentication.getCredentials();
		if (apiKey.equals(token)) {
			var authority = new SimpleGrantedAuthority(SCOPE_INTERNAL_READ);
			return new APIKeyAuthToken(apiKey, List.of(authority));
		}

		throw new BadCredentialsException("The API key doesn't match!");
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return APIKeyAuthToken.class.isAssignableFrom(authentication);
	}

}
