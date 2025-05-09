package cz.fi.muni.pa165.teamservice.security.filters;

import cz.fi.muni.pa165.teamservice.security.tokens.APIKeyAuthToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class APIKeyFilter extends OncePerRequestFilter {

	private static final String TOKEN_HEADER = "X-Api-Key";

	private static final RequestMatcher REQUEST_MATCHER = new AntPathRequestMatcher(
			"/api/team-characteristics/team/**");

	private final AuthenticationManager authenticationManager;

	public APIKeyFilter(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
			@NonNull FilterChain filterChain) throws ServletException, IOException {

		var token = request.getHeader(TOKEN_HEADER);

		if (token != null) {
			var authRequest = new APIKeyAuthToken(token);
			try {
				var authenticatedContext = authenticationManager.authenticate(authRequest);
				SecurityContextHolder.getContext().setAuthentication(authenticatedContext);
			}
			catch (AuthenticationException ignored) {
				// We don't want to throw the exception, since the Bearer OAuth2
				// authentication is fallback
			}
		}

		filterChain.doFilter(request, response);
	}

	protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
		return !REQUEST_MATCHER.matches(request);
	}

}
