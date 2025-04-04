package cz.fi.muni.pa165.gameservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Random;

@Configuration
public class RandomGeneratorConfig {

	@Bean
	public Random randomGenerator() {
		return new Random();
	}

}
