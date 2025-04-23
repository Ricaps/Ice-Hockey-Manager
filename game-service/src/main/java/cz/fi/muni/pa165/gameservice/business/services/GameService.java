package cz.fi.muni.pa165.gameservice.business.services;

import cz.fi.muni.pa165.dto.teamService.TeamCharacteristicDTO;
import cz.fi.muni.pa165.enums.TeamCharacteristicType;
import cz.fi.muni.pa165.gameservice.api.exception.ValidationHelper;
import cz.fi.muni.pa165.gameservice.business.messages.MatchMessageResolver;
import cz.fi.muni.pa165.gameservice.config.SchedulingConfiguration;
import cz.fi.muni.pa165.gameservice.persistence.entities.Match;
import cz.fi.muni.pa165.gameservice.persistence.entities.Result;
import cz.fi.muni.pa165.service.teamService.api.TeamCharacteristicController;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class GameService {

	private static final Logger LOGGER = LoggerFactory.getLogger(GameService.class);

	private final MatchService matchService;

	private final TaskSchedulerService taskSchedulerService;

	private final Random random;

	private final SchedulingConfiguration schedulingConfiguration;

	private final MatchMessageResolver matchMessageResolver;

	private final TeamCharacteristicController teamCharacteristicController;

	@Autowired
	public GameService(MatchService matchService, TaskSchedulerService taskSchedulerService, Random random,
			SchedulingConfiguration schedulingConfiguration, MatchMessageResolver matchMessageResolver,
			TeamCharacteristicController teamCharacteristicController) {
		this.matchService = matchService;
		this.taskSchedulerService = taskSchedulerService;
		this.random = random;
		this.schedulingConfiguration = schedulingConfiguration;
		this.matchMessageResolver = matchMessageResolver;
		this.teamCharacteristicController = teamCharacteristicController;
	}

	@Scheduled(fixedRateString = "${tasks.schedule.fetch-interval}", timeUnit = TimeUnit.SECONDS)
	private void periodicScheduler() {
		var toBeScheduled = matchService.getMatchesForScheduling(schedulingConfiguration.getMatchScheduleOffset());

		for (var match : toBeScheduled) {
			scheduleMatch(match);
		}
	}

	public void scheduleMatch(@NotNull Match match) {
		ValidationHelper.requireNonNull(match, "Please provide match for scheduling");

		var runnable = runMatch(match);
		taskSchedulerService.scheduleTask(runnable, match.getGuid(), match.getStartAt().toInstant());
	}

	public Runnable runMatch(Match match) {
		return () -> {
			LOGGER.debug("Started match {}", match.getGuid());
			try {
				Thread.sleep(schedulingConfiguration.getMatchSleepPlaceholder()); // Game
																					// running
																					// placeholder
			}
			catch (InterruptedException e) {
				LOGGER.error("Running match {} failed", match.getGuid(), e);
			}

			var scoreHomeTeam = getRandomScore(match.getHomeTeamUid());
			var scoreAwayTeam = getRandomScore(match.getAwayTeamUid());
			UUID winnerTeam = null;
			if (scoreHomeTeam != scoreAwayTeam) {
				winnerTeam = scoreHomeTeam < scoreAwayTeam ? match.getAwayTeamUid() : match.getHomeTeamUid();
			}
			var result = Result.builder()
				.matchGuid(match.getGuid())
				.scoreHomeTeam(scoreHomeTeam)
				.scoreAwayTeam(scoreAwayTeam)
				.winnerTeam(winnerTeam)
				.build();
			var savedMatch = matchService.publishResult(result, match);

			matchMessageResolver.sendMatchEndedTopic(savedMatch);
			LOGGER.debug("Ended match {}", match.getGuid());
		};
	}

	/**
	 * Gets teams characteristics from TeamService and computes goals using simple
	 * randomization
	 * @return random score
	 */
	private int getRandomScore(UUID teamUUID) {
		var teamChars = getTeamCharacteristics(teamUUID);

		if (teamChars.isEmpty()) {
			LOGGER.error(
					"Failed to fetch teams characteristics for team ID={}. Falling back to the simple randomization...",
					teamUUID);
			return random.nextInt(0, 5);
		}

		LOGGER.debug("Received team characteristics {}", teamChars);
		var characteristics = convertCharacteristicsToMap(teamChars);
		var performance = getTeamAverage(characteristics) + getRandomNoise();
		performance = Math.max(0, performance);

		return (int) Math.round(performance * 0.1);
	}

	private double getRandomNoise() {
		return random.nextGaussian() * 10;
	}

	private Map<TeamCharacteristicType, Integer> convertCharacteristicsToMap(
			List<TeamCharacteristicDTO> characteristicDTOList) {
		return characteristicDTOList.stream()
			.collect(Collectors.toMap(TeamCharacteristicDTO::getCharacteristicType,
					TeamCharacteristicDTO::getCharacteristicValue));
	}

	/**
	 * Uses team characteristics to compute average
	 * @return double representing team characteristics
	 */
	private double getTeamAverage(Map<TeamCharacteristicType, Integer> characteristicMap) {
		return Stream.of(TeamCharacteristicType.values())
			.mapToInt(value -> characteristicMap.getOrDefault(value, 0))
			.average()
			.orElse(0);
	}

	private List<TeamCharacteristicDTO> getTeamCharacteristics(UUID teamUUID) {
		try {
			return teamCharacteristicController.findByTeamId(teamUUID);
		}
		catch (RestClientException e) {
			LOGGER.error("Team Service is not available", e);
		}

		return List.of();
	}

}
