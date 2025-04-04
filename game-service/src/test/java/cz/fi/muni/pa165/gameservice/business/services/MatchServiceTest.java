package cz.fi.muni.pa165.gameservice.business.services;

import cz.fi.muni.pa165.gameservice.api.exception.ResourceNotFoundException;
import cz.fi.muni.pa165.gameservice.api.exception.ValueIsMissingException;
import cz.fi.muni.pa165.gameservice.persistence.repositories.MatchRepository;
import cz.fi.muni.pa165.gameservice.persistence.repositories.ResultRepository;
import cz.fi.muni.pa165.gameservice.testdata.CompetitionTestData;
import cz.fi.muni.pa165.gameservice.testdata.MatchTestData;
import cz.fi.muni.pa165.gameservice.testdata.TeamsTestData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class MatchServiceTest {

	@Mock
	MatchGenerationService matchGenerationService;

	@Mock
	MatchRepository matchRepository;

	@Mock
	ResultRepository resultRepository;

	@InjectMocks
	MatchService matchService;

	@Test
	void generateMatches_nullCompetition_throwsException() {
		assertThatThrownBy(() -> matchService.generateMatches(null))
			.hasMessage("Please provide competition to generate matches")
			.isInstanceOf(ValueIsMissingException.class);

		Mockito.verify(matchGenerationService, Mockito.never()).createCombinations(Mockito.any(), Mockito.anyInt());
		Mockito.verify(matchGenerationService, Mockito.never()).createMatches(Mockito.any(), Mockito.any());
	}

	@Test
	void generateMatches_alreadyGenerated_returnMatches() {
		var competition = CompetitionTestData.getCompetitionEntity();
		var matches = MatchTestData.getRandomMatches();
		competition.setMatches(matches);

		var returnedMatches = matchService.generateMatches(competition);
		assertThat(returnedMatches).containsAll(matches);

		Mockito.verify(matchGenerationService, Mockito.never()).createCombinations(Mockito.any(), Mockito.anyInt());
		Mockito.verify(matchGenerationService, Mockito.never()).createMatches(Mockito.any(), Mockito.any());
	}

	@Test
	void generateMatches_emptyNotNullMatchesInCompetition_generateMatches() {
		var competition = CompetitionTestData.getCompetitionEntity();
		competition.setMatches(Set.of());
		var pairs = TeamsTestData.getPairs();
		var teams = TeamsTestData.getTeamEntities();
		competition.setTeams(teams);
		var matches = MatchTestData.getRandomMatches();

		Mockito.when(matchGenerationService.createCombinations(teams.stream().toList(), 1)).thenReturn(pairs);

		Mockito.when(matchGenerationService.createMatches(competition, pairs)).thenReturn(matches.stream().toList());

		var returnedMatches = matchService.generateMatches(competition);

		assertThat(returnedMatches).containsAll(matches);
		Mockito.verify(matchGenerationService, Mockito.times(1)).createCombinations(Mockito.any(), Mockito.anyInt());
		Mockito.verify(matchGenerationService, Mockito.times(1)).createMatches(Mockito.any(), Mockito.any());
	}

	@Test
	void generateMatches_allGood_returnMatches() {
		var competition = CompetitionTestData.getCompetitionEntity();
		var teams = TeamsTestData.getTeamEntities();
		competition.setTeams(teams);
		var pairs = TeamsTestData.getPairs();
		var matches = MatchTestData.getRandomMatches();

		Mockito.when(matchGenerationService.createCombinations(teams.stream().toList(), 1)).thenReturn(pairs);

		Mockito.when(matchGenerationService.createMatches(competition, pairs)).thenReturn(matches.stream().toList());

		var returnedMatches = matchService.generateMatches(competition);

		assertThat(returnedMatches).containsAll(matches);
		Mockito.verify(matchGenerationService, Mockito.times(1)).createCombinations(Mockito.any(), Mockito.anyInt());
		Mockito.verify(matchGenerationService, Mockito.times(1)).createMatches(Mockito.any(), Mockito.any());
	}

	@Test
	void generateMatches_emptyTeams_throwsException() {
		var competition = CompetitionTestData.getCompetitionEntity();

		assertThatThrownBy(() -> matchService.generateMatches(competition))
			.hasMessage("Please assign some teams to the competition")
			.isInstanceOf(ValueIsMissingException.class);

		Mockito.verify(matchGenerationService, Mockito.never()).createCombinations(Mockito.any(), Mockito.anyInt());
		Mockito.verify(matchGenerationService, Mockito.never()).createMatches(Mockito.any(), Mockito.any());
	}

	@Test
	void generateMatches_nullTeams_throwsException() {
		var competition = CompetitionTestData.getCompetitionEntity();
		competition.setTeams(null);

		assertThatThrownBy(() -> matchService.generateMatches(competition))
			.hasMessage("Please assign some teams to the competition")
			.isInstanceOf(ValueIsMissingException.class);

		Mockito.verify(matchGenerationService, Mockito.never()).createCombinations(Mockito.any(), Mockito.anyInt());
		Mockito.verify(matchGenerationService, Mockito.never()).createMatches(Mockito.any(), Mockito.any());
	}

	@Test
	void saveMatches_nullMatches_throwsException() {
		assertThatThrownBy(() -> matchService.saveMatches(null)).hasMessage("Please provide matches to save")
			.isInstanceOf(ValueIsMissingException.class);

		Mockito.verify(matchRepository, Mockito.never()).saveAll(Mockito.any());
	}

	@Test
	void saveMatches_emptyMatches_throwsException() {
		assertThatThrownBy(() -> matchService.saveMatches(List.of())).hasMessage("Please provide matches to save")
			.isInstanceOf(ValueIsMissingException.class);

		Mockito.verify(matchRepository, Mockito.never()).saveAll(Mockito.any());
	}

	@Test
	void saveMatches_allGood_savesAndReturns() {
		var matches = MatchTestData.getRandomMatches().stream().toList();

		Mockito.when(matchRepository.saveAll(matches)).thenReturn(matches);

		var returnedMatches = matchService.saveMatches(matches);

		assertThat(returnedMatches).containsAll(matches);
		Mockito.verify(matchRepository, Mockito.times(1)).saveAll(matches);
	}

	@Test
	void saveMatch_nullEntity_throwsException() {
		assertThatThrownBy(() -> matchService.saveMatch(null)).isInstanceOf(ValueIsMissingException.class)
			.hasMessage("Please provide match to save.");

		Mockito.verify(matchRepository, Mockito.never()).save(Mockito.any());
	}

	@Test
	void saveMatch_allGood_returnsSavedMatch() {
		var match = MatchTestData.getRandomMatches().stream().toList().getFirst();

		Mockito.when(matchRepository.save(match)).thenReturn(match);

		var savedMatch = matchService.saveMatch(match);
		assertThat(savedMatch).isEqualTo(match);

		Mockito.verify(matchRepository, Mockito.times(1)).save(match);
	}

	@Test
	void getMatchesOfCompetition_nullCompetition_throwsException() {
		assertThatThrownBy(() -> matchService.getMatchesOfCompetition(null))
			.hasMessage("Please provide competition UUID")
			.isInstanceOf(ValueIsMissingException.class);

		Mockito.verify(matchRepository, Mockito.never()).getMatchByCompetition_Guid(Mockito.any());
	}

	@Test
	void getMatchesOfCompetition_allGood_returnsMatches() {
		var matches = MatchTestData.getRandomMatches().stream().toList();
		var competitionUUID = UUID.randomUUID();

		Mockito.when(matchRepository.getMatchByCompetition_Guid(competitionUUID)).thenReturn(matches);

		var returnedMatches = matchService.getMatchesOfCompetition(competitionUUID);
		assertThat(returnedMatches).containsAll(matches);

		Mockito.verify(matchRepository, Mockito.times(1)).getMatchByCompetition_Guid(competitionUUID);
	}

	@Test
	void getMatch_nullUUID_throwsException() {
		assertThatThrownBy(() -> matchService.getMatch(null)).hasMessage("Please provide match UUID")
			.isInstanceOf(ValueIsMissingException.class);

		Mockito.verify(matchRepository, Mockito.never()).getMatchByGuid(Mockito.any());
	}

	@Test
	void getMatch_unknownMatch_throwsException() {
		var uuid = UUID.randomUUID();
		Mockito.when(matchRepository.getMatchByGuid(uuid)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> matchService.getMatch(uuid))
			.hasMessage("Match with UUID %s was not found".formatted(uuid))
			.isInstanceOf(ResourceNotFoundException.class);
	}

	@Test
	void getMatch_allGood_returnsMatch() {
		var match = MatchTestData.getRandomMatches().stream().toList().getFirst();
		Mockito.when(matchRepository.getMatchByGuid(match.getGuid())).thenReturn(Optional.of(match));

		var returnedMatch = matchService.getMatch(match.getGuid());

		assertThat(returnedMatch).isEqualTo(match);
	}

	@Test
	void getMatchesForScheduling_zeroNumber_throwsException() {
		assertThatThrownBy(() -> matchService.getMatchesForScheduling(0)).hasMessage("Offset should be positive number")
			.isInstanceOf(IllegalArgumentException.class);

		Mockito.verify(matchRepository, Mockito.never()).getMatchesForScheduling(Mockito.any());
	}

	@Test
	void getMatchesForScheduling_positiveNumber_returnedMatches() {
		var matches = MatchTestData.getRandomMatches().stream().toList();
		var offset = 1;
		Mockito.when(matchRepository.getMatchesForScheduling(Mockito.any())).thenReturn(matches);

		var returnedMatches = matchService.getMatchesForScheduling(offset);
		assertThat(returnedMatches).containsAll(matches);
	}

	@Test
	void publishResult_nullResult_throwsException() {
		var match = MatchTestData.getRandomMatches().stream().toList().getFirst();
		assertThatThrownBy(() -> matchService.publishResult(null, match))
			.hasMessage("Please provide result for publishing")
			.isInstanceOf(ValueIsMissingException.class);

		Mockito.verify(resultRepository, Mockito.never()).save(Mockito.any());
		Mockito.verify(matchRepository, Mockito.never()).save(Mockito.any());
	}

	@Test
	void publishResult_nullMatch_throwsException() {
		var result = MatchTestData.getResult(UUID.randomUUID());
		assertThatThrownBy(() -> matchService.publishResult(result, null)).hasMessage("Please provide match")
			.isInstanceOf(ValueIsMissingException.class);

		Mockito.verify(resultRepository, Mockito.never()).save(Mockito.any());
		Mockito.verify(matchRepository, Mockito.never()).save(Mockito.any());
	}

	@Test
	void publishResult_allGood_expectedEntitiesSaved() {
		var result = MatchTestData.getResult(UUID.randomUUID());
		var match = MatchTestData.getRandomMatches().stream().toList().getFirst();
		var zonedID = match.getStartAt().getZone();
		match.setEndAt(ZonedDateTime.now(zonedID));

		matchService.publishResult(result, match);

		Mockito.verify(resultRepository, Mockito.times(1)).save(result);
		Mockito.verify(matchRepository, Mockito.times(1)).save(match);
	}

}