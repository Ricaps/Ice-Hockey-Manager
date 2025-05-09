package cz.fi.muni.pa165.userservice.util;

import cz.fi.muni.pa165.userservice.persistence.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.stereotype.Component;

@Component
public class AuthUtil {

	private final UserRepository userRepository;

	@Autowired
	public AuthUtil(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public String getAuthMail() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		OAuth2AuthenticatedPrincipal principal = (OAuth2AuthenticatedPrincipal) auth.getPrincipal();

		return principal.getAttribute("sub");
	}

	public Boolean isAuthenticatedUserAdmin() {
		var isUserAdmin = userRepository.findIsAdminByMail(getAuthMail());
		return isUserAdmin.isPresent() && isUserAdmin.get();
	}

}
