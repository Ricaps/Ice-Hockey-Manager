package cz.fi.muni.pa165.gameservice.business.services;

import cz.fi.muni.pa165.gameservice.api.exception.ValidationHelper;
import cz.fi.muni.pa165.gameservice.config.SchedulingConfiguration;
import cz.fi.muni.pa165.gameservice.persistence.entities.Match;
import cz.fi.muni.pa165.gameservice.persistence.entities.Result;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class GameService {

	private static final Logger LOGGER = LoggerFactory.getLogger(GameService.class);

	private final MatchService matchService;

	private final TaskSchedulerService taskSchedulerService;

	private final Random random;

	private final SchedulingConfiguration schedulingConfiguration;

	@Autowired
	public GameService(MatchService matchService, TaskSchedulerService taskSchedulerService, Random random,
			SchedulingConfiguration schedulingConfiguration) {
		this.matchService = matchService;
		this.taskSchedulerService = taskSchedulerService;
		this.random = random;
		this.schedulingConfiguration = schedulingConfiguration;
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

			var scoreHomeTeam = getRandomScore();
			var scoreAwayTeam = getRandomScore();
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
			matchService.publishResult(result, match);
			LOGGER.debug("Ended match {}", match.getGuid());
		};
	}

	/**
	 * TODO: Will be replaced with randomization based on team characteristics when the
	 * services are connected
	 * @return random score
	 */
	private int getRandomScore() {
		return random.nextInt(0, 5);
	}

}
