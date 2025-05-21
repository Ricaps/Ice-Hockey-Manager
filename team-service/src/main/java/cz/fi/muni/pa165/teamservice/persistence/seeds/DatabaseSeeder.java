package cz.fi.muni.pa165.teamservice.persistence.seeds;

import cz.fi.muni.pa165.enums.TeamCharacteristicType;
import cz.fi.muni.pa165.teamservice.persistence.entities.BudgetSystem;
import cz.fi.muni.pa165.teamservice.persistence.entities.FictiveTeam;
import cz.fi.muni.pa165.teamservice.persistence.entities.TeamCharacteristic;
import cz.fi.muni.pa165.teamservice.persistence.repositories.BudgetSystemRepository;
import cz.fi.muni.pa165.teamservice.persistence.repositories.FictiveTeamRepository;
import cz.fi.muni.pa165.teamservice.persistence.repositories.TeamCharacteristicRepository;
import net.datafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Jan Martinek
 */
@Component
public class DatabaseSeeder implements CommandLineRunner {

	private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseSeeder.class);

	private final BudgetSystemRepository budgetSystemRepository;

	private final FictiveTeamRepository fictiveTeamRepository;

	private final TeamCharacteristicRepository teamCharacteristicRepository;

	private final Faker faker = new Faker();

	@Value("${server.database.seed:false}")
	private boolean shouldSeed;

	@Value("${server.database.clear:false}")
	private boolean shouldClear;

	@Autowired
	public DatabaseSeeder(BudgetSystemRepository budgetSystemRepository, FictiveTeamRepository fictiveTeamRepository,
			TeamCharacteristicRepository teamCharacteristicRepository) {
		this.budgetSystemRepository = budgetSystemRepository;
		this.fictiveTeamRepository = fictiveTeamRepository;
		this.teamCharacteristicRepository = teamCharacteristicRepository;
	}

	@Override
	@Transactional
	public void run(String... args) {
		if (!shouldSeed)
			return;
		if (shouldClear)
			clearData();

		LOGGER.info("Database seeding started");

		seedFictiveTeams();
		seedBudgetSystems();
		seedTeamCharacteristics();

		LOGGER.info("Database seeding ended");
	}

	private void seedFictiveTeams() {
		if (fictiveTeamRepository.count() != 0)
			return;

		LOGGER.info("Seeding teams");
		List<UUID> playerIds = generatePlayerIds(11);

		for (int i = 0; i < 50; i++) {
			FictiveTeam team = new FictiveTeam();
			team.setName(faker.team().name());
			team.setPlayerIDs(playerIds);
			team.setOwnerId(UUID.randomUUID());
			fictiveTeamRepository.save(team);
		}
	}

	private void seedBudgetSystems() {
		if (budgetSystemRepository.count() != 0)
			return;

		LOGGER.info("Seeding budget systems");
		for (int i = 0; i < 25; i++) {
			BudgetSystem budget = new BudgetSystem();
			budget.setAmount(faker.number().numberBetween(0, 1000));
			budgetSystemRepository.save(budget);
		}
	}

	private void seedTeamCharacteristics() {
		if (teamCharacteristicRepository.count() != 0)
			return;

		LOGGER.info("Seeding team characteristics");
		List<FictiveTeam> teams = fictiveTeamRepository.findAll();

		if (teams.isEmpty()) {
			LOGGER.warn("No teams available for characteristics");
			return;
		}

		for (int i = 0; i < 100; i++) {
			TeamCharacteristic characteristic = new TeamCharacteristic();
			characteristic.setFictiveTeam(teams.get(i % 50));
			characteristic.setCharacteristicType(faker.options().option(TeamCharacteristicType.class));
			characteristic.setCharacteristicValue(faker.number().numberBetween(0, 100));
			teamCharacteristicRepository.save(characteristic);
		}
	}

	private List<UUID> generatePlayerIds(int count) {
		List<UUID> ids = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			ids.add(UUID.randomUUID());
		}
		return ids;
	}

	public void clearData() {
		teamCharacteristicRepository.deleteAll();
		fictiveTeamRepository.deleteAll();
		budgetSystemRepository.deleteAll();
	}

}