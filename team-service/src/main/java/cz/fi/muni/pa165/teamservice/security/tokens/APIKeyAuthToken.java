package cz.fi.muni.pa165.teamservice.security.tokens;

import cz.fi.muni.pa165.teamservice.security.principals.TechnicalUser;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

public class APIKeyAuthToken extends AbstractAuthenticationToken {

	private final String apiKey;

	/**
	 * This constructor should be used before the request for the Authentication by
	 * Authentication Provider It automatically sets authenticated property to false
	 * @param apiKey apiKey which should be checked
	 */
	public APIKeyAuthToken(String apiKey) {
		super(null);
		this.apiKey = apiKey;
		setAuthenticated(false);
	}

	/**
	 * This constructor should be used when the Authentication was successful and
	 * authorities can be assigned
	 * @param apiKey apiKey which subject was authenticated with
	 * @param authorities granted authorities
	 */
	public APIKeyAuthToken(String apiKey, List<? extends GrantedAuthority> authorities) {
		super(authorities);
		this.apiKey = apiKey;
		setAuthenticated(true);
	}

	@Override
	public Object getCredentials() {
		return apiKey;
	}

	@Override
	public Object getPrincipal() {
		return new TechnicalUser();
	}

}
