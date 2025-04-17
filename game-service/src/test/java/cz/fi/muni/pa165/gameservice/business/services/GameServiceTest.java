package cz.fi.muni.pa165.gameservice.business.services;

import cz.fi.muni.pa165.gameservice.api.exception.ValueIsMissingException;
import cz.fi.muni.pa165.gameservice.business.messages.MatchMessageResolver;
import cz.fi.muni.pa165.gameservice.config.SchedulingConfiguration;
import cz.fi.muni.pa165.gameservice.persistence.entities.Result;
import cz.fi.muni.pa165.gameservice.testdata.MatchTestData;
import cz.fi.muni.pa165.service.teamService.api.TeamCharacteristicController;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jms.core.JmsTemplate;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.is;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

	@Mock
	MatchService matchService;

	@Mock
	TaskSchedulerService taskSchedulerService;

	@Mock
	Random random;

	@Mock
	SchedulingConfiguration schedulingConfiguration;

	@Mock
	JmsTemplate ignoredJmsTemplate;

	@Mock
	TeamCharacteristicController teamCharacteristicController;

	@Mock
	MatchMessageResolver matchMessageResolver;

	@InjectMocks
	GameService gameService;

	@Test
	void scheduleMatch_nullMatch_throwsException() {
		assertThatThrownBy(() -> gameService.scheduleMatch(null)).hasMessage("Please provide match for scheduling")
			.isInstanceOf(ValueIsMissingException.class);

		Mockito.verify(taskSchedulerService, Mockito.never()).scheduleTask(Mockito.any(), Mockito.any(), Mockito.any());
	}

	@Test
	void scheduleMatch_validMatch_isScheduled() {
		var match = MatchTestData.getRandomMatches().stream().toList().getFirst();

		gameService.scheduleMatch(match);

		Mockito.verify(taskSchedulerService, Mockito.times(1))
			.scheduleTask(Mockito.any(), Mockito.eq(match.getGuid()), Mockito.eq(match.getStartAt().toInstant()));
	}

	@Test
	void runMatch_sameScore_noWinner() {
		var match = MatchTestData.getRandomMatches().stream().toList().getFirst();
		var expectedResult = Result.builder()
			.matchGuid(match.getGuid())
			.scoreAwayTeam(1)
			.scoreHomeTeam(1)
			.winnerTeam(null)
			.build();

		Mockito.when(random.nextInt(Mockito.anyInt(), Mockito.anyInt())).thenReturn(1);
		Mockito.when(schedulingConfiguration.getMatchSleepPlaceholder()).thenReturn(100);
		Mockito.when(matchService.publishResult(expectedResult, match)).thenReturn(match);

		gameService.runMatch(match).run();

		Mockito.verify(matchService, Mockito.times(1)).publishResult(expectedResult, match);
		Mockito.verify(matchMessageResolver, Mockito.times(1)).sendMatchEndedTopic(match);
	}

	@Test
	void runMatch_awayTeamWinner_expectedWinnerTeam() {
		var match = MatchTestData.getRandomMatches().stream().toList().getFirst();

		Mockito.when(random.nextInt(Mockito.anyInt(), Mockito.anyInt())).thenReturn(1).thenReturn(2);
		Mockito.when(schedulingConfiguration.getMatchSleepPlaceholder()).thenReturn(100);

		gameService.runMatch(match).run();

		var expectedResult = Result.builder()
			.matchGuid(match.getGuid())
			.scoreHomeTeam(1)
			.scoreAwayTeam(2)
			.winnerTeam(match.getAwayTeamUid())
			.build();

		Mockito.verify(matchService, Mockito.times(1)).publishResult(expectedResult, match);
	}

	@Test
	void runMatch_homeTeamWinner_expectedWinnerTeam() {
		var match = MatchTestData.getRandomMatches().stream().toList().getFirst();

		Mockito.when(random.nextInt(Mockito.anyInt(), Mockito.anyInt())).thenReturn(2).thenReturn(1);
		Mockito.when(schedulingConfiguration.getMatchSleepPlaceholder()).thenReturn(100);

		gameService.runMatch(match).run();

		var expectedResult = Result.builder()
			.matchGuid(match.getGuid())
			.scoreHomeTeam(2)
			.scoreAwayTeam(1)
			.winnerTeam(match.getHomeTeamUid())
			.build();

		Mockito.verify(matchService, Mockito.times(1)).publishResult(expectedResult, match);
	}

	@Test
	void runMatch_checkSleepTimeout() {
		var match = MatchTestData.getRandomMatches().stream().toList().getFirst();

		Mockito.when(random.nextInt(Mockito.anyInt(), Mockito.anyInt())).thenReturn(2).thenReturn(1);
		Mockito.when(schedulingConfiguration.getMatchSleepPlaceholder()).thenReturn(2000);

		var called = new AtomicInteger(0);
		try (var executor = Executors.newSingleThreadExecutor()) {
			executor.submit(() -> {
				gameService.runMatch(match).run();
				called.incrementAndGet();
			});
			Awaitility.await()
				.atLeast(2000, TimeUnit.MILLISECONDS)
				.atMost(2500, TimeUnit.MILLISECONDS)
				.untilAtomic(called, is(1));
		}

	}

}