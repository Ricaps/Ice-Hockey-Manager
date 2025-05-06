package cz.fi.muni.pa053.oauthclient.api.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TemplateController {

	@GetMapping("/")
	public String index(Model model, @AuthenticationPrincipal OidcUser user) {
		model.addAttribute("user", user);

		return "redirect:/login";
	}

	@GetMapping("/token")
	public String tokenPage(Model model, @AuthenticationPrincipal OidcUser user,
			@RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient oauth2Client) {
		model.addAttribute("email", user.getUserInfo().getEmail());
		model.addAttribute("name", user.getUserInfo().getFullName());
		model.addAttribute("token", oauth2Client.getAccessToken().getTokenValue());

		return "token";
	}

}
