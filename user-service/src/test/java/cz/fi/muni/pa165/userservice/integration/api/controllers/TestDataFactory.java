package cz.fi.muni.pa165.userservice.integration.api.controllers;

import cz.fi.muni.pa165.userservice.persistence.seeds.UserServiceSeeder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
class TestDataFactory implements CommandLineRunner {

	private final UserServiceSeeder userServiceSeeder;

	@Value("${server.database.seedTestData:true}")
	private boolean shouldSeed;

	@Autowired
	public TestDataFactory(UserServiceSeeder userServiceSeeder) {
		this.userServiceSeeder = userServiceSeeder;
	}

	@Override
	public void run(String... args) {
		if (!shouldSeed) {
			log.info("Seeding test data is turned off. No data were seeded.");
			return;
		}

		userServiceSeeder.seedTestData(50);
	}

}
