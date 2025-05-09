package cz.fi.muni.pa165.gameservice.business.services.seed;

import cz.fi.muni.pa165.gameservice.persistence.entities.Competition;
import cz.fi.muni.pa165.gameservice.persistence.entities.CompetitionHasTeam;
import cz.fi.muni.pa165.gameservice.persistence.repositories.CompetitionRepository;
import cz.fi.muni.pa165.gameservice.persistence.repositories.TeamRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Component
@Order(2)
public class TeamsSeed implements Seed<CompetitionHasTeam> {

	private static final Logger LOGGER = LoggerFactory.getLogger(TeamsSeed.class);

	private final TeamRepository teamRepository;

	private final CompetitionRepository competitionRepository;

	private List<CompetitionHasTeam> data;

	@Autowired
	public TeamsSeed(TeamRepository competitionRepository, CompetitionRepository competitionRepository1) {
		this.teamRepository = competitionRepository;
		this.competitionRepository = competitionRepository1;
	}

	@Override
	public void runSeed(boolean logData) {
		if (teamRepository.count() != 0) {
			LOGGER.info("Teams entities are already seeded. Skipping...");
			return;
		}

		List<CompetitionHasTeam> teams = new ArrayList<>(getTemplateData());

		data = teamRepository.saveAllAndFlush(teams);
		if (logData) {
			LOGGER.debug("Seeded data: {}", data);
		}
	}

	private List<CompetitionHasTeam> getTemplateData() {
		List<Competition> competitions = competitionRepository.findAll();
		List<CompetitionHasTeam> competitionTeams = new ArrayList<>();
		Random random = new Random();

		// Leave on competition without teams
		competitions = competitions.stream().skip(1).toList();

		// Ensure each competition has at least 2 teams
		for (Competition competition : competitions) {
			for (int i = 0; i < 2; i++) {
				var hasTeam = CompetitionHasTeam.builder().teamUid(UUID.randomUUID()).competition(competition).build();
				competition.getTeams().add(hasTeam);
				competitionTeams.add(hasTeam);
			}
		}

		// Calculate how many more teams we want to generate (e.g. total 90)
		int additionalTeams = 90 - competitionTeams.size();
		List<Competition> shuffledCompetitions = new ArrayList<>(competitions);

		// Assign remaining teams randomly
		for (int i = 0; i < additionalTeams; i++) {
			Competition randomCompetition = shuffledCompetitions.get(random.nextInt(shuffledCompetitions.size()));
			var hasTeam = CompetitionHasTeam.builder()
				.teamUid(UUID.randomUUID())
				.competition(randomCompetition)
				.build();
			randomCompetition.getTeams().add(hasTeam);
			competitionTeams.add(hasTeam);
		}

		return competitionTeams;
	}

	@Override
	public List<CompetitionHasTeam> getData() {
		return this.data;
	}

	@Override
	public void clearData() {
		LOGGER.debug("Cleared data of {}", this.getClass().getSimpleName());
		this.teamRepository.deleteAll();
	}

}
