package cz.fi.muni.pa165.gameservice.business.services.seed;

import cz.fi.muni.pa165.gameservice.persistence.entities.Competition;
import cz.fi.muni.pa165.gameservice.persistence.entities.CompetitionHasTeam;
import cz.fi.muni.pa165.gameservice.persistence.entities.Match;
import cz.fi.muni.pa165.gameservice.persistence.entities.MatchType;
import cz.fi.muni.pa165.gameservice.persistence.repositories.ArenaRepository;
import cz.fi.muni.pa165.gameservice.persistence.repositories.CompetitionRepository;
import cz.fi.muni.pa165.gameservice.persistence.repositories.MatchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.*;

@Component
@Order(3)
public class MatchSeed implements Seed<Match> {

	private static final Logger LOGGER = LoggerFactory.getLogger(MatchSeed.class);

	private final MatchRepository matchRepository;

	private final CompetitionRepository competitionRepository;

	private final ArenaRepository arenaRepository;

	private List<Match> data;

	@Autowired
	public MatchSeed(MatchRepository matchRepository, CompetitionRepository competitionRepository,
			ArenaRepository arenaRepository) {
		this.matchRepository = matchRepository;
		this.competitionRepository = competitionRepository;
		this.arenaRepository = arenaRepository;
	}

	@Override
	public void runSeed(boolean logData) {
		if (matchRepository.count() != 0) {
			LOGGER.info("Match entities are already seeded. Skipping...");
			return;
		}

		List<Match> matches = new ArrayList<>(getTemplateData());

		data = matchRepository.saveAllAndFlush(matches);
		if (logData) {
			LOGGER.debug("Seeded data: {}", data);
		}
	}

	private List<Match> getTemplateData() {
		var competitionPool = competitionRepository.findAll();
		var arenas = arenaRepository.findAll();
		var matches = new ArrayList<Match>();
		Random random = new Random();

		// Exclude one competition (it won't have any matches)
		competitionPool.remove(random.nextInt(competitionPool.size()));

		int matchCount = 50;

		for (int i = 0; i < matchCount; i++) {
			// Pick a competition that has at least 2 teams
			Competition competition;
			Set<CompetitionHasTeam> teamLinks;
			do {
				competition = competitionPool.get(random.nextInt(competitionPool.size()));
				teamLinks = competition.getTeams();
			}
			while (teamLinks == null || teamLinks.isEmpty());

			// Get a list of team UIDs from the competition
			List<UUID> teamUids = teamLinks.stream().map(CompetitionHasTeam::getTeamUid).distinct().toList();

			// Select two different teams
			UUID homeTeam = teamUids.get(random.nextInt(teamUids.size()));
			UUID awayTeam;
			do {
				awayTeam = teamUids.get(random.nextInt(teamUids.size()));
			}
			while (awayTeam.equals(homeTeam));

			boolean isPast = random.nextBoolean();

			OffsetDateTime startAt;
			OffsetDateTime endAt = null;

			if (isPast) {
				startAt = OffsetDateTime.now().minusDays(random.nextInt(10) + 1);
				endAt = startAt.plusHours(2);

			}
			else {
				startAt = OffsetDateTime.now().plusDays(random.nextInt(30) + 1);
				// endAt and result stay null
			}

			Match match = Match.builder()
				.competition(competition)
				.arena(arenas.get(random.nextInt(arenas.size() - 1))) // Replace with your
																		// actual arena
																		// creation or
																		// lookup
				.startAt(startAt)
				.endAt(endAt)
				.homeTeamUid(homeTeam)
				.awayTeamUid(awayTeam)
				.matchType(MatchType.GROUP_STAGE)
				.build();

			competition.getMatches().add(match);
			matches.add(match);
		}
		return matches;
	}

	@Override
	public List<Match> getData() {
		return this.data;
	}

}
