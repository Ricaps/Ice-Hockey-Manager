package cz.fi.muni.pa165.gameservice.business.services;

import cz.fi.muni.pa165.gameservice.api.exception.ValueIsMissingException;
import cz.fi.muni.pa165.gameservice.business.services.seed.ArenaSeed;
import cz.fi.muni.pa165.gameservice.persistence.entities.CompetitionHasTeam;
import cz.fi.muni.pa165.gameservice.persistence.entities.Match;
import cz.fi.muni.pa165.gameservice.testdata.CompetitionTestData;
import cz.fi.muni.pa165.gameservice.testdata.TeamsTestData;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class MatchGenerationServiceTest {

	@Mock
	ArenaService arenaService;

	@Mock
	Random randomGenerator;

	@InjectMocks
	MatchGenerationService matchGenerationService;

	@InjectMocks
	ArenaSeed arenaSeed;

	@Test
	void createCombinations_1double3teams_returnsCombinations() {
		var teams = TeamsTestData.getTeamEntities().stream().limit(4).toList();

		// A
		// B
		// C
		// D
		// A-B, A-C, A-D, B-C, B-D, C-D
		var combinations = matchGenerationService.createCombinations(teams, 1);
		assertThat(combinations).hasSize(6);

		checkCombinations(teams, combinations, 1);
	}

	@Test
	void createCombinations_1double2team_1combination() {
		var teams = TeamsTestData.getTeamEntities().stream().limit(2).toList();

		var combinations = matchGenerationService.createCombinations(teams, 1);

		assertThat(combinations).hasSize(1);
		checkCombinations(teams, combinations, 1);
	}

	@Test
	void createCombinations_2double3teams_returnsCombinationsMultipliedByDoubles() {
		var teams = TeamsTestData.getTeamEntities().stream().limit(4).toList();

		var combinations = matchGenerationService.createCombinations(teams, 2);

		assertThat(combinations).hasSize(12);
		checkCombinations(teams, combinations, 2);
	}

	@Test
	void createCombinations_doublesLowerThan1_throwsException() {
		var teams = TeamsTestData.getTeamEntities().stream().limit(2).toList();

		assertThatThrownBy(() -> matchGenerationService.createCombinations(teams, 0))
			.hasMessage("Number of doubles cannot be lower than 1")
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void createCombinations_1double1team_throwException() {
		var teams = TeamsTestData.getTeamEntities().stream().limit(1).toList();

		assertThatThrownBy(() -> matchGenerationService.createCombinations(teams, 1))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Cannot generate combinations for number of team lower than 2");
	}

	private void checkCombinations(List<CompetitionHasTeam> teams, List<Pair<UUID, UUID>> combinations, int doubles) {
		Map<UUID, AtomicInteger> checkMap = teams.stream()
			.collect(Collectors.toMap(CompetitionHasTeam::getTeamUid, val -> new AtomicInteger(0)));
		for (var combination : combinations) {
			var team1 = combination.getLeft();
			var team2 = combination.getRight();

			assertThat(team1).withFailMessage("Pair cannot contain same team").isNotEqualTo(team2);
			checkMap.get(team1).incrementAndGet();
			checkMap.get(team2).incrementAndGet();
		}
		// Example: Checks that each team appears in 3 pairs when the number of teams is 4
		checkMap.values().forEach(count -> assertThat(count.get()).isEqualTo((teams.size() - 1) * doubles));
	}

	@Test
	void createMatches_6matches6days_equallyDistributed() {
		var arena = arenaSeed.getTemplateData().getFirst();
		Mockito.when(arenaService.findAllArenas()).thenReturn(List.of(arena));
		Mockito.when(randomGenerator.nextInt(Mockito.anyInt())).thenReturn(0);

		var combinations = new ArrayList<>(TeamsTestData.getPairs());
		var competition = CompetitionTestData.getCompetitionEntity();
		competition.setStartAt(LocalDate.now());
		competition.setEndAt(LocalDate.now().plusDays(combinations.size()));

		var createdMatches = matchGenerationService.createMatches(competition, combinations);
		assertThat(createdMatches).hasSize(combinations.size());

		for (int i = 0; i < createdMatches.size(); i++) {
			var match = createdMatches.get(i);

			// Matches should be equally distributed in the given time interval
			assertThat(match).isNotNull();
			assertThat(LocalDate.from(match.getStartAt()))
				.withFailMessage("Start date of match no. %s doesn't match", i)
				.isEqualTo(LocalDate.now().plusDays(i));

			assertThat(LocalDate.from(match.getEndAt())).withFailMessage("Start date of match no. %s doesn't match", i)
				.isEqualTo(LocalDate.now().plusDays(i));

			assertThat(match.getCompetition()).isEqualTo(competition);
			assertThat(match.getArena()).isEqualTo(arena);

			var currentCombination = combinations.get(i);
			assertThat(match.getHomeTeamUid()).isEqualTo(currentCombination.getLeft());
			assertThat(match.getAwayTeamUid()).isEqualTo(currentCombination.getRight());
		}
	}

	@Test
	void createMatches_3Days6Matches_twoMatchesPerDay() {
		var arena = arenaSeed.getTemplateData().getFirst();
		Mockito.when(arenaService.findAllArenas()).thenReturn(List.of(arena));
		Mockito.when(randomGenerator.nextInt(Mockito.anyInt())).thenReturn(0);

		var combinations = new ArrayList<>(TeamsTestData.getPairs());
		var competition = CompetitionTestData.getCompetitionEntity();
		competition.setStartAt(LocalDate.now());
		competition.setEndAt(LocalDate.now().plusDays(combinations.size() / 2));

		var createdMatches = matchGenerationService.createMatches(competition, combinations);
		assertThat(createdMatches).hasSize(combinations.size());

		var startDates = createdMatches.stream().map(Match::getStartAt).toList();
		for (var date : startDates) {
			assertThat(Collections.frequency(startDates, date)).isEqualTo(2);
		}
	}

	@Test
	void createMatches_12Days6Matches_everySecondDay() {
		var arena = arenaSeed.getTemplateData().getFirst();
		Mockito.when(arenaService.findAllArenas()).thenReturn(List.of(arena));
		Mockito.when(randomGenerator.nextInt(Mockito.anyInt())).thenReturn(0);

		var combinations = new ArrayList<>(TeamsTestData.getPairs());
		var competition = CompetitionTestData.getCompetitionEntity();
		competition.setStartAt(LocalDate.now());
		competition.setEndAt(LocalDate.now().plusDays(combinations.size() * 2L));

		var createdMatches = matchGenerationService.createMatches(competition, combinations);
		assertThat(createdMatches).hasSize(combinations.size());

		var startDates = createdMatches.stream().map(match -> LocalDate.from(match.getStartAt())).toList();

		var isEven = new AtomicBoolean(true);
		competition.getStartAt().datesUntil(competition.getEndAt()).forEach(date -> {
			var isEvenLocal = isEven.getAndSet(!isEven.get());
			assertThat(Collections.frequency(startDates, date)).isEqualTo(isEvenLocal ? 1 : 0);
		});
	}

	@Test
	void createMatches_competitionNull_throwsException() {
		var combinations = new ArrayList<>(TeamsTestData.getPairs());

		assertThatThrownBy(() -> matchGenerationService.createMatches(null, combinations))
			.isInstanceOf(ValueIsMissingException.class)
			.hasMessage("Please provide competition for matches generation");

		Mockito.verify(arenaService, Mockito.never()).findAllArenas();
	}

	@Test
	void createMatches_combinationsNull_throwsException() {
		var competition = CompetitionTestData.getCompetitionEntity();

		assertThatThrownBy(() -> matchGenerationService.createMatches(competition, null))
			.isInstanceOf(ValueIsMissingException.class)
			.hasMessage("Please provide combinations for matches generation");

		Mockito.verify(arenaService, Mockito.never()).findAllArenas();
	}

	@Test
	void createMatches_combinationsEmpty_throwsException() {
		var competition = CompetitionTestData.getCompetitionEntity();
		List<Pair<UUID, UUID>> combinations = new ArrayList<>();

		assertThatThrownBy(() -> matchGenerationService.createMatches(competition, combinations))
			.isInstanceOf(ValueIsMissingException.class)
			.hasMessage("Cannot create matches for empty combinations");

		Mockito.verify(arenaService, Mockito.never()).findAllArenas();
	}

}