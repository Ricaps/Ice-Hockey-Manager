package cz.fi.muni.pa165.gameservice.config;

import cz.fi.muni.pa165.gameservice.business.services.seed.Seed;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

import java.util.List;

@ConfigurationProperties(prefix = "seed")
@ConfigurationPropertiesScan
@Getter
@Setter
public class SeedConfiguration {

	private boolean enabled;

	private List<Class<Seed<?>>> included;

}
