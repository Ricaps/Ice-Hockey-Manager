package cz.fi.muni.pa165.gameservice.business.services;

import cz.fi.muni.pa165.gameservice.api.exception.ResourceNotFoundException;
import cz.fi.muni.pa165.gameservice.api.exception.ValidationHelper;
import cz.fi.muni.pa165.gameservice.persistence.entities.Competition;
import cz.fi.muni.pa165.gameservice.persistence.entities.Match;
import cz.fi.muni.pa165.gameservice.persistence.entities.Result;
import cz.fi.muni.pa165.gameservice.persistence.repositories.MatchRepository;
import cz.fi.muni.pa165.gameservice.persistence.repositories.ResultRepository;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class MatchService {

	private static final Logger LOGGER = LoggerFactory.getLogger(MatchService.class);

	private final MatchGenerationService matchGenerationService;

	private final MatchRepository matchRepository;

	private final ResultRepository resultRepository;

	public MatchService(MatchGenerationService matchGenerationService, MatchRepository matchRepository,
			ResultRepository resultRepository) {
		this.matchGenerationService = matchGenerationService;
		this.matchRepository = matchRepository;
		this.resultRepository = resultRepository;
	}

	/**
	 * Generates matches for given competition TODO: Matches are generated with only one
	 * double and MatchType.GROUP_STAGE
	 * @param competition competition for which you want to generate matches
	 * @return list of generated matches
	 */
	public List<Match> generateMatches(@NotNull Competition competition) {
		ValidationHelper.requireNonNull(competition, "Please provide competition to generate matches");

		if (competition.getMatches() != null && !competition.getMatches().isEmpty()) {
			return competition.getMatches().stream().toList();
		}

		var teams = competition.getTeams();
		ValidationHelper.requireNonNull(teams, "Please assign some teams to the competition");
		ValidationHelper.requireNotEmpty(teams, "Please assign some teams to the competition");

		LOGGER.debug("Started generating matches for competition with UID {}", competition.getGuid());
		final var combinations = matchGenerationService.createCombinations(competition.getTeams().stream().toList(), 1);
		final var matches = matchGenerationService.createMatches(competition, combinations);

		LOGGER.debug("Generated {} matches for competition with UUID {}", matches.size(), competition.getGuid());
		return matches;
	}

	public List<Match> saveMatches(@NotNull List<Match> matches) {
		ValidationHelper.requireNonNull(matches, "Please provide matches to save");
		ValidationHelper.requireNotEmpty(matches, "Please provide matches to save");

		return matchRepository.saveAll(matches);
	}

	public Match saveMatch(@NotNull Match matchEntity) {
		ValidationHelper.requireNonNull(matchEntity, "Please provide match to save.");

		return matchRepository.save(matchEntity);
	}

	public List<Match> getMatchesOfCompetition(@NotNull UUID competitionUUID) {
		ValidationHelper.requireNonNull(competitionUUID, "Please provide competition UUID");

		return this.matchRepository.getMatchByCompetition_Guid(competitionUUID);
	}

	public Match getMatch(@NotNull UUID matchUUID) {
		ValidationHelper.requireNonNull(matchUUID, "Please provide match UUID");

		var match = matchRepository.getMatchByGuid(matchUUID);

		return match
			.orElseThrow(() -> new ResourceNotFoundException("Match with UUID %s was not found".formatted(matchUUID)));
	}

	/**
	 * Gets matches for scheduling
	 * @param offset positive number from current date defining when the matches should be
	 * loaded for scheduling
	 */
	public List<Match> getMatchesForScheduling(int offset) {
		if (offset <= 0) {
			throw new IllegalArgumentException("Offset should be positive number");
		}
		var schedulingTime = ZonedDateTime.now().plusHours(offset);
		return matchRepository.getMatchesForScheduling(schedulingTime);
	}

	@Transactional
	public Match publishResult(@NotNull Result result, @NotNull Match match) {
		ValidationHelper.requireNonNull(result, "Please provide result for publishing");
		ValidationHelper.requireNonNull(match, "Please provide match");

		var returnedResult = resultRepository.save(result);

		match.setResult(returnedResult);
		var zonedID = match.getStartAt().getZone();
		match.setEndAt(ZonedDateTime.now(zonedID));
		return matchRepository.save(match);
	}

}
