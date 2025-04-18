package cz.fi.muni.pa165.gameservice.business.services;

import cz.fi.muni.pa165.gameservice.api.exception.ValidationHelper;
import cz.fi.muni.pa165.gameservice.persistence.entities.Competition;
import cz.fi.muni.pa165.gameservice.persistence.entities.CompetitionHasTeam;
import cz.fi.muni.pa165.gameservice.persistence.entities.Match;
import cz.fi.muni.pa165.gameservice.persistence.entities.MatchType;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class MatchGenerationService {

	private final ArenaService arenaService;

	private final Random randomGenerator;

	@Autowired
	public MatchGenerationService(Random randomGenerator, ArenaService arenaService) {
		this.randomGenerator = randomGenerator;
		this.arenaService = arenaService;
	}

	/**
	 * Creates combinations of teams for matches. Each team in combination with each other
	 * team
	 * @param numberOfDoubles how many matches should two exact teams play. For each
	 * double, the home and away teams are switched
	 * @return List of tuples, where each tuple represents one combination of teams
	 */
	public List<Pair<UUID, UUID>> createCombinations(final List<CompetitionHasTeam> competitionHasTeam,
			final int numberOfDoubles) {
		if (competitionHasTeam.size() < 2) {
			throw new IllegalArgumentException("Cannot generate combinations for number of team lower than 2");
		}

		if (numberOfDoubles < 1) {
			throw new IllegalArgumentException("Number of doubles cannot be lower than 1");
		}

		final List<Pair<UUID, UUID>> combinations = new ArrayList<>();
		final var numberOfTeams = competitionHasTeam.size();

		for (int i = 0; i < numberOfTeams; i++) {
			for (int j = i + 1; j < numberOfTeams; j++) {
				for (int z = 0; z < numberOfDoubles; z++) {
					Pair<UUID, UUID> pair;
					var team1 = competitionHasTeam.get(i).getTeamUid();
					var team2 = competitionHasTeam.get(j).getTeamUid();

					pair = resolveDouble(z, team1, team2);
					combinations.add(pair);
				}
			}
		}

		return combinations;
	}

	private Pair<UUID, UUID> resolveDouble(int z, UUID team1, UUID team2) {
		Pair<UUID, UUID> pair;
		if (z % 2 == 0) {
			pair = Pair.of(team1, team2);
		}
		else {
			pair = Pair.of(team2, team1);
		}
		return pair;
	}

	/**
	 * Creates match for given competition and teams combinations Match dates are equally
	 * distributed among the competition time interval
	 * @param competition competition for which matches should be generated
	 * @param combinations teams combinations for which the matches will be generated
	 * @return List of Match entities, not saved in the database
	 */
	public List<Match> createMatches(@NotNull Competition competition, @NotNull List<Pair<UUID, UUID>> combinations) {
		ValidationHelper.requireNonNull(competition, "Please provide competition for matches generation");
		ValidationHelper.requireNonNull(combinations, "Please provide combinations for matches generation");
		ValidationHelper.requireNotEmpty(combinations, "Cannot create matches for empty combinations");

		var numberOfDays = ChronoUnit.DAYS.between(competition.getStartAt(), competition.getEndAt());
		var matchesInterval = (double) numberOfDays / combinations.size();
		Collections.shuffle(combinations, randomGenerator);

		var arenas = arenaService.findAllArenas();

		var startDate = competition.getStartAt();
		var zoneOffset = ZonedDateTime.now().getOffset();

		var matches = new ArrayList<Match>();
		for (int i = 0; i < combinations.size(); i++) {
			var currentCombination = combinations.get(i);
			var currentMatchDate = startDate.plusDays((long) Math.floor(i * matchesInterval));

			var match = Match.builder()
				.matchType(MatchType.GROUP_STAGE)
				.arena(arenas.get(randomGenerator.nextInt(arenas.size())))
				.homeTeamUid(currentCombination.getLeft())
				.awayTeamUid(currentCombination.getRight())
				.startAt(currentMatchDate.atTime(8, 0).atOffset(zoneOffset))
				.endAt(currentMatchDate.atTime(9, 0).atOffset(zoneOffset))
				.competition(competition)
				.build();

			competition.getMatches().add(match);
			matches.add(match);
		}
		return matches;
	}

}
