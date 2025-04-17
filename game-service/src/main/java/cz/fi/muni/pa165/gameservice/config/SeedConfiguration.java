package cz.fi.muni.pa165.gameservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationProperties(prefix = "seed")
@ConfigurationPropertiesScan
@Getter
@Setter
public class SeedConfiguration {

	private boolean enabled;

	private boolean logData = false;

}
