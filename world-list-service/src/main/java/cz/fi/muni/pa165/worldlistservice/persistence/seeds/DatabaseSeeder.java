package cz.fi.muni.pa165.worldlistservice.persistence.seeds;

import cz.fi.muni.pa165.enums.ChampionshipRegionType;
import cz.fi.muni.pa165.enums.PlayerCharacteristicType;
import cz.fi.muni.pa165.worldlistservice.persistence.entities.*;
import cz.fi.muni.pa165.worldlistservice.persistence.repositories.*;
import net.datafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DatabaseSeeder implements CommandLineRunner {

	private static final Logger logger = LoggerFactory.getLogger(DatabaseSeeder.class);

	private final ChampionshipRegionRepository championshipRegionRepository;

	private final ChampionshipRepository championshipRepository;

	private final TeamRepository teamRepository;

	private final PlayerRepository playerRepository;

	private final PlayerCharacteristicRepository playerCharacteristicRepository;

	private final Faker faker = new Faker();

	@Autowired
	public DatabaseSeeder(ChampionshipRegionRepository championshipRegionRepository,
			ChampionshipRepository championshipRepository, TeamRepository teamRepository,
			PlayerRepository playerRepository, PlayerCharacteristicRepository playerCharacteristicRepository) {
		this.championshipRegionRepository = championshipRegionRepository;
		this.championshipRepository = championshipRepository;
		this.teamRepository = teamRepository;
		this.playerRepository = playerRepository;
		this.playerCharacteristicRepository = playerCharacteristicRepository;
	}

	@Override
	public void run(String... args) {
		seedChampionshipRegions();
		seedChampionships();
		seedTeams();
		seedPlayers();
		seedPlayerCharacteristics();
	}

	private void seedChampionshipRegions() {
		if (championshipRegionRepository.count() != 0) {
			return;
		}

		logger.info("Seeding championship regions");

		for (int i = 0; i < 5; i++) {
			ChampionshipRegionEntity region = new ChampionshipRegionEntity();
			region.setName(faker.address().city());
			region.setType(faker.options().option(ChampionshipRegionType.class));
			championshipRegionRepository.save(region);
		}

		logger.info("Championship regions seeded!");
	}

	private void seedChampionships() {
		if (championshipRepository.count() != 0) {
			return;
		}

		logger.info("Seeding championships");

		var hockeyChampionshipNames = new String[] { "NHL", "Stanley Cup", "World Hockey Championship",
				"IIHF World Championship", "European Hockey League", "KHL", "Junior Hockey World Cup", "Spengler Cup" };

		for (int i = 0; i < 5; i++) {
			ChampionshipEntity championship = new ChampionshipEntity();
			championship.setName(hockeyChampionshipNames[i % hockeyChampionshipNames.length]);
			championship.setChampionshipRegion(faker.options()
				.option(championshipRegionRepository.findAll()
					.get((int) faker.number().numberBetween(0, championshipRegionRepository.count()))));
			championshipRepository.save(championship);
		}

		logger.info("Championships seeded!");
	}

	private void seedTeams() {
		if (teamRepository.count() != 0) {
			return;
		}

		logger.info("Seeding teams");

		for (int i = 0; i < 10; i++) {
			TeamEntity team = new TeamEntity();
			team.setName(faker.team().name());
			team.setChampionship(faker.options()
				.option(championshipRepository.findAll()
					.get((int) faker.number().numberBetween(0, championshipRepository.count()))));
			teamRepository.save(team);
		}

		logger.info("Teams seeded!");
	}

	private void seedPlayers() {
		if (playerRepository.count() != 0) {
			return;
		}

		logger.info("Seeding players");

		for (int i = 0; i < 20; i++) {
			PlayerEntity player = new PlayerEntity();
			player.setFirstName(faker.name().firstName());
			player.setLastName(faker.name().lastName());
			player.setTeam(faker.options()
				.option(teamRepository.findAll().get((int) faker.number().numberBetween(0, teamRepository.count()))));
			player.setOverallRating(faker.number().numberBetween(0, 100));
			playerRepository.save(player);
		}

		logger.info("Players seeded!");
	}

	private void seedPlayerCharacteristics() {
		if (playerCharacteristicRepository.count() != 0) {
			return;
		}

		logger.info("Seeding player characteristics");

		for (int i = 0; i < 20; i++) {
			PlayerCharacteristicEntity characteristic = new PlayerCharacteristicEntity();
			characteristic.setPlayer(faker.options()
				.option(playerRepository.findAll()
					.get((int) faker.number().numberBetween(0, playerRepository.count()))));
			characteristic.setType(faker.options().option(PlayerCharacteristicType.class));
			characteristic.setValue(faker.number().numberBetween(0, 100));
			playerCharacteristicRepository.save(characteristic);
		}

		logger.info("Player characteristics seeded!");
	}

}
