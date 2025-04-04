package cz.fi.muni.pa165.gameservice.business.facades;

import cz.fi.muni.pa165.gameservice.api.exception.ResourceNotFoundException;
import cz.fi.muni.pa165.gameservice.api.exception.ValueIsMissingException;
import cz.fi.muni.pa165.gameservice.business.mappers.MatchMapper;
import cz.fi.muni.pa165.gameservice.business.services.ArenaService;
import cz.fi.muni.pa165.gameservice.business.services.CompetitionService;
import cz.fi.muni.pa165.gameservice.business.services.MatchService;
import cz.fi.muni.pa165.gameservice.business.services.seed.ArenaSeed;
import cz.fi.muni.pa165.gameservice.persistence.entities.Match;
import cz.fi.muni.pa165.gameservice.testdata.CompetitionTestData;
import cz.fi.muni.pa165.gameservice.testdata.MatchTestData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class MatchFacadeTest {

	@Mock
	private MatchService matchService;

	@Mock
	private MatchMapper matchMapper;

	@Mock
	private CompetitionService competitionService;

	@Mock
	private ArenaService arenaService;

	@InjectMocks
	private MatchFacade matchFacade;

	@Test
	void generateMatches_nullUUID_throwsException() {
		assertThatThrownBy(() -> matchFacade.generateMatches(null)).isInstanceOf(ValueIsMissingException.class)
			.hasMessage("Please provide UUID of the competition");

		Mockito.verify(competitionService, Mockito.never()).getCompetition(Mockito.any());
		Mockito.verify(matchService, Mockito.never()).generateMatches(Mockito.any());
		Mockito.verify(matchService, Mockito.never()).saveMatches(Mockito.any());
		Mockito.verify(matchMapper, Mockito.never()).listEntitiesToListViews(Mockito.any());
	}

	@Test
	void generateMatches_unknownCompetition_throwsException() {
		var uuid = UUID.randomUUID();

		String unknownCompetition = "Unknown competition";
		Mockito.when(competitionService.getCompetition(uuid))
			.thenThrow(new ResourceNotFoundException(unknownCompetition));
		assertThatThrownBy(() -> matchFacade.generateMatches(uuid)).hasMessage(unknownCompetition)
			.isInstanceOf(ResourceNotFoundException.class);

		Mockito.verify(matchService, Mockito.never()).generateMatches(Mockito.any());
		Mockito.verify(matchService, Mockito.never()).saveMatches(Mockito.any());
		Mockito.verify(matchMapper, Mockito.never()).listEntitiesToListViews(Mockito.any());
	}

	@Test
	void generateMatches_allPasses_returnsMatches() {
		var matches = MatchTestData.getRandomMatches().stream().limit(3).toList();
		var competition = CompetitionTestData.getCompetitionEntity();
		var matchesView = MatchTestData.getMatchesView();

		Mockito.when(competitionService.getCompetition(competition.getGuid())).thenReturn(competition);
		Mockito.when(matchService.generateMatches(competition)).thenReturn(matches);
		Mockito.when(matchService.saveMatches(matches)).thenReturn(matches);
		Mockito.when(matchMapper.listEntitiesToListViews(matches)).thenReturn(matchesView);

		var returnedView = matchFacade.generateMatches(competition.getGuid());
		assertThat(returnedView).containsAll(matchesView);

		var inOrder = Mockito.inOrder(competitionService, matchService, matchMapper);
		inOrder.verify(competitionService).getCompetition(competition.getGuid());
		inOrder.verify(matchService).generateMatches(competition);
		inOrder.verify(matchService).saveMatches(matches);
		inOrder.verify(matchMapper).listEntitiesToListViews(matches);
		inOrder.verifyNoMoreInteractions();
	}

	@Test
	void createMatch_nullCreate_throwsException() {
		assertThatThrownBy(() -> matchFacade.createMatch(null)).isInstanceOf(ValueIsMissingException.class)
			.hasMessage("You must provide Match Dto to create new match");

		Mockito.verify(arenaService, Mockito.never()).getReferenceIfExists(Mockito.any());
		Mockito.verify(matchMapper, Mockito.never()).matchCreateDtoToEntity(Mockito.any(), Mockito.any());
		Mockito.verify(matchService, Mockito.never()).saveMatch(Mockito.any());
		Mockito.verify(matchMapper, Mockito.never()).matchEntityToMatchViewDto(Mockito.any());
	}

	@Test
	void createMatch_unknownArena_throwsException() {
		var matchCreate = MatchTestData.getMatchCreateDto();
		String unknownArena = "Unknown arena";
		Mockito.when(arenaService.getReferenceIfExists(matchCreate.getArenaUid()))
			.thenThrow(new ResourceNotFoundException(unknownArena));
		assertThatThrownBy(() -> matchFacade.createMatch(matchCreate)).isInstanceOf(ResourceNotFoundException.class)
			.hasMessage(unknownArena);

		Mockito.verify(arenaService, Mockito.times(1)).getReferenceIfExists(matchCreate.getArenaUid());
		Mockito.verify(matchMapper, Mockito.never()).matchCreateDtoToEntity(Mockito.any(), Mockito.any());
		Mockito.verify(matchService, Mockito.never()).saveMatch(Mockito.any());
		Mockito.verify(matchMapper, Mockito.never()).matchEntityToMatchViewDto(Mockito.any());
	}

	@Test
	void createMatch_allGood_returnsMatchView() {
		var matchCreate = MatchTestData.getMatchCreateDto();
		var arena = ArenaSeed.getTemplateData().getFirst();
		arena.setGuid(matchCreate.getArenaUid());
		var matchEntity = MatchTestData.getRandomMatches().stream().toList().getFirst();
		var matchView = MatchTestData.getMatchesView().getFirst();

		Mockito.when(arenaService.getReferenceIfExists(matchCreate.getArenaUid())).thenReturn(arena);
		Mockito.when(matchMapper.matchCreateDtoToEntity(matchCreate, arena)).thenReturn(matchEntity);
		Mockito.when(matchService.saveMatch(matchEntity)).thenReturn(matchEntity);
		Mockito.when(matchMapper.matchEntityToMatchViewDto(matchEntity)).thenReturn(matchView);

		var returnedView = matchFacade.createMatch(matchCreate);
		assertThat(returnedView).isEqualTo(matchView);

		var inOrder = Mockito.inOrder(arenaService, matchMapper, matchService);
		inOrder.verify(arenaService).getReferenceIfExists(matchCreate.getArenaUid());
		inOrder.verify(matchMapper).matchCreateDtoToEntity(matchCreate, arena);
		inOrder.verify(matchService).saveMatch(matchEntity);
		inOrder.verify(matchMapper).matchEntityToMatchViewDto(matchEntity);
		inOrder.verifyNoMoreInteractions();
	}

	@Test
	void getMatchesOfCompetition_nullCompetitionUUID_throwsException() {
		assertThatThrownBy(() -> matchFacade.getMatchesOfCompetition(null, false))
			.hasMessage("Please provide UUID of competition")
			.isInstanceOf(ValueIsMissingException.class);

		Mockito.verify(matchService, Mockito.never()).getMatchesOfCompetition(Mockito.any());
		Mockito.verify(matchMapper, Mockito.never()).listEntitiesToListViews(Mockito.any());
		Mockito.verify(matchMapper, Mockito.never()).listEntitiesToListViewsIgnoreResult(Mockito.any());
	}

	@Test
	void getMatchesOfCompetitionIncludeResults_notExistingCompetition_returnsEmptyList() {
		var competition = CompetitionTestData.getCompetitionEntity();
		List<Match> emptyList = List.of();
		Mockito.when(matchService.getMatchesOfCompetition(competition.getGuid())).thenReturn(emptyList);
		Mockito.when(matchMapper.listEntitiesToListViews(emptyList)).thenReturn(List.of());

		var returnedList = matchFacade.getMatchesOfCompetition(competition.getGuid(), true);
		assertThat(returnedList).isEmpty();

		Mockito.verify(matchMapper, Mockito.times(1)).listEntitiesToListViews(List.of());
		Mockito.verify(matchMapper, Mockito.never()).listEntitiesToListViewsIgnoreResult(Mockito.any());
	}

	@Test
	void getMatchesOfCompetitionDontInclude_notExistingCompetition_returnsEmptyList() {
		var competition = CompetitionTestData.getCompetitionEntity();
		List<Match> emptyList = List.of();
		Mockito.when(matchService.getMatchesOfCompetition(competition.getGuid())).thenReturn(emptyList);
		Mockito.when(matchMapper.listEntitiesToListViewsIgnoreResult(emptyList)).thenReturn(List.of());

		var returnedList = matchFacade.getMatchesOfCompetition(competition.getGuid(), false);
		assertThat(returnedList).isEmpty();

		Mockito.verify(matchMapper, Mockito.times(1)).listEntitiesToListViewsIgnoreResult(List.of());
		Mockito.verify(matchMapper, Mockito.never()).listEntitiesToListViews(Mockito.any());
	}

	@Test
	void getMatchesOfCompetitionInclude_allGood_returnsDtos() {
		var matches = MatchTestData.getRandomMatches().stream().toList();
		var competition = CompetitionTestData.getCompetitionEntity();
		var viewDtos = MatchTestData.getMatchesView();

		Mockito.when(matchService.getMatchesOfCompetition(competition.getGuid())).thenReturn(matches);
		Mockito.when(matchMapper.listEntitiesToListViews(matches)).thenReturn(viewDtos);

		var returnedList = matchFacade.getMatchesOfCompetition(competition.getGuid(), true);
		assertThat(returnedList).isNotEmpty();
		assertThat(returnedList).containsAll(viewDtos);

		Mockito.verify(matchMapper, Mockito.times(1)).listEntitiesToListViews(matches);
		Mockito.verify(matchMapper, Mockito.never()).listEntitiesToListViewsIgnoreResult(Mockito.any());
	}

	@Test
	void getMatchesOfCompetitionDontInclude_allGood_returnsDtos() {
		var matches = MatchTestData.getRandomMatches().stream().toList();
		var competition = CompetitionTestData.getCompetitionEntity();
		var viewDtos = MatchTestData.getMatchesView();

		Mockito.when(matchService.getMatchesOfCompetition(competition.getGuid())).thenReturn(matches);
		Mockito.when(matchMapper.listEntitiesToListViewsIgnoreResult(matches)).thenReturn(viewDtos);

		var returnedList = matchFacade.getMatchesOfCompetition(competition.getGuid(), false);
		assertThat(returnedList).isNotEmpty();
		assertThat(returnedList).containsAll(viewDtos);

		Mockito.verify(matchMapper, Mockito.times(1)).listEntitiesToListViewsIgnoreResult(matches);
		Mockito.verify(matchMapper, Mockito.never()).listEntitiesToListViews(Mockito.any());
	}

	@Test
	void getMatch_nullMatchUUID_throwsException() {
		assertThatThrownBy(() -> matchFacade.getMatch(null, false)).hasMessage("Please provide UUID of desired match")
			.isInstanceOf(ValueIsMissingException.class);

		Mockito.verify(matchService, Mockito.never()).getMatch(Mockito.any());
		Mockito.verify(matchMapper, Mockito.never()).matchEntityToMatchViewDto(Mockito.any());
		Mockito.verify(matchMapper, Mockito.never()).matchEntityToMatchViewDtoIgnoreResult(Mockito.any());
	}

	@Test
	void getMatch_notExistingMatch_throwsException() {
		var uuid = UUID.randomUUID();
		String exceptionMessage = "Desired match doesn't exist";
		Mockito.when(matchService.getMatch(uuid)).thenThrow(new ResourceNotFoundException(exceptionMessage));

		assertThatThrownBy(() -> matchFacade.getMatch(uuid, true)).isInstanceOf(ResourceNotFoundException.class)
			.hasMessage(exceptionMessage);

		Mockito.verify(matchMapper, Mockito.never()).listEntitiesToListViews(List.of());
		Mockito.verify(matchMapper, Mockito.never()).listEntitiesToListViewsIgnoreResult(Mockito.any());
	}

	@Test
	void getMatchResultsInclude_allGood_returnsDto() {
		var match = MatchTestData.getRandomMatches().stream().toList().getFirst();
		var viewDto = MatchTestData.getMatchesView().stream().toList().getFirst();

		Mockito.when(matchService.getMatch(match.getGuid())).thenReturn(match);
		Mockito.when(matchMapper.matchEntityToMatchViewDto(match)).thenReturn(viewDto);

		var returnedView = matchFacade.getMatch(match.getGuid(), true);

		assertThat(returnedView).isEqualTo(viewDto);

		Mockito.verify(matchMapper, Mockito.times(1)).matchEntityToMatchViewDto(match);
		Mockito.verify(matchMapper, Mockito.never()).matchEntityToMatchViewDtoIgnoreResult(Mockito.any());
	}

	@Test
	void getMatchResultsNotIncluded_allGood_returnsDto() {
		var match = MatchTestData.getRandomMatches().stream().toList().getFirst();
		var viewDto = MatchTestData.getMatchesView().stream().toList().getFirst();

		Mockito.when(matchService.getMatch(match.getGuid())).thenReturn(match);
		Mockito.when(matchMapper.matchEntityToMatchViewDtoIgnoreResult(match)).thenReturn(viewDto);

		var returnedView = matchFacade.getMatch(match.getGuid(), false);

		assertThat(returnedView).isEqualTo(viewDto);

		Mockito.verify(matchMapper, Mockito.times(1)).matchEntityToMatchViewDtoIgnoreResult(match);
		Mockito.verify(matchMapper, Mockito.never()).matchEntityToMatchViewDto(Mockito.any());
	}

}