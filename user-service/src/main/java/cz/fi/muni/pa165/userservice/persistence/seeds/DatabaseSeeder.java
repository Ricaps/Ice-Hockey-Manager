package cz.fi.muni.pa165.userservice.persistence.seeds;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DatabaseSeeder implements CommandLineRunner {

	private final UserServiceSeeder userServiceSeeder;

	@Value("${server.database.seed:true}")
	private boolean shouldSeed;

	@Value("${server.database.clear:false}")
	private boolean shouldClear;

	@Autowired
	public DatabaseSeeder(UserServiceSeeder userServiceSeeder) {
		this.userServiceSeeder = userServiceSeeder;
	}

	@Override
	public void run(String... args) {
		if (shouldClear) {
			userServiceSeeder.clearDatabase();
		}

		if (!shouldSeed) {
			log.info("Seeding is disabled. No data were seeded.");
			return;
		}

		userServiceSeeder.seedTestData(5, 50);
	}

}
