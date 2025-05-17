package cz.fi.muni.pa165.gameservice.config.client;

import cz.fi.muni.pa165.service.teamservice.api.TeamCharacteristicController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class TeamServiceClientConfig {

	public static final String API_KEY_HEADER = "X-Api-Key";

	@Value("${services.baseUrl.team-service}")
	private String teamServiceUrl;

	@Value("${services.api-key.team-service}")
	private String teamServiceApiKey;

	@Bean
	TeamCharacteristicController teamCharacteristicsServiceClient(HttpServiceProxyFactory teamServiceProxy) {
		return teamServiceProxy.createClient(TeamCharacteristicController.class);
	}

	@Bean
	HttpServiceProxyFactory teamServiceProxy() {
		RestClient restClient = RestClient.builder()
			.baseUrl(teamServiceUrl)
			.defaultHeader(API_KEY_HEADER, teamServiceApiKey)
			.build();
		RestClientAdapter adapter = RestClientAdapter.create(restClient);
		return HttpServiceProxyFactory.builderFor(adapter).build();
	}

}
