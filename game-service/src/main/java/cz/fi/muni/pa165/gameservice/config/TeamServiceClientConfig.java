package cz.fi.muni.pa165.gameservice.config;

import cz.fi.muni.pa165.service.teamService.api.TeamCharacteristicController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class TeamServiceClientConfig {

	@Value("${services.baseUrl.team-service}")
	private String teamServiceUrl;

	@Bean
	TeamCharacteristicController teamCharacteristicsServiceClient(HttpServiceProxyFactory teamServiceProxy) {
		return teamServiceProxy.createClient(TeamCharacteristicController.class);
	}

	@Bean
	HttpServiceProxyFactory teamServiceProxy() {
		RestClient restClient = RestClient.builder().baseUrl(teamServiceUrl).build();
		RestClientAdapter adapter = RestClientAdapter.create(restClient);
		return HttpServiceProxyFactory.builderFor(adapter).build();
	}

}
