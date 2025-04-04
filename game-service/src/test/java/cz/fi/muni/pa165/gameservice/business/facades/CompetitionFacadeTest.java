package cz.fi.muni.pa165.gameservice.business.facades;

import cz.fi.muni.pa165.gameservice.api.exception.ActionForbidden;
import cz.fi.muni.pa165.gameservice.api.exception.ResourceNotFoundException;
import cz.fi.muni.pa165.gameservice.api.exception.ValueIsMissingException;
import cz.fi.muni.pa165.gameservice.business.mappers.CompetitionMapper;
import cz.fi.muni.pa165.gameservice.business.services.CompetitionService;
import cz.fi.muni.pa165.gameservice.business.services.TeamService;
import cz.fi.muni.pa165.gameservice.persistence.entities.CompetitionHasTeam;
import cz.fi.muni.pa165.gameservice.testdata.CompetitionTestData;
import cz.fi.muni.pa165.gameservice.testdata.TeamsTestData;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class CompetitionFacadeTest {

	@Mock
	CompetitionService competitionService;

	@Mock
	CompetitionMapper competitionMapper;

	@Mock
	TeamService teamService;

	@InjectMocks
	CompetitionFacade competitionFacade;

	@Test
	void assignTeam_competitionIsRunning_throwsException() {
		var competition = CompetitionTestData.getCompetitionEntity();
		var teamUId = UUID.randomUUID();
		var teamAssign = TeamsTestData.getAssignTeamDto(teamUId);

		Mockito.when(competitionService.getCompetition(competition.getGuid())).thenReturn(competition);
		Mockito.when(competitionService.isStarted(competition)).thenReturn(true);

		Assertions.assertThatThrownBy(() -> competitionFacade.assignTeam(competition.getGuid(), teamAssign))
			.isInstanceOf(ActionForbidden.class)
			.hasMessage("You cannot assign team to the competition, that is already running");

		Mockito.verify(teamService, Mockito.never()).assignTeamToCompetition(Mockito.any());
	}

	@Test
	void assignTeam_competitionNull_throwsException() {
		var teamUId = UUID.randomUUID();
		var teamAssign = TeamsTestData.getAssignTeamDto(teamUId);

		Assertions.assertThatThrownBy(() -> competitionFacade.assignTeam(null, teamAssign))
			.isInstanceOf(ValueIsMissingException.class)
			.hasMessage("Competition UUID cannot be null");

		Mockito.verify(teamService, Mockito.never()).assignTeamToCompetition(Mockito.any());
	}

	@Test
	void assignTeam_assignTeamNullNull_throwsException() {
		var competition = CompetitionTestData.getCompetitionEntity();

		Assertions.assertThatThrownBy(() -> competitionFacade.assignTeam(competition.getGuid(), null))
			.isInstanceOf(ValueIsMissingException.class)
			.hasMessage("Team UUID cannot be null");

		Mockito.verify(teamService, Mockito.never()).assignTeamToCompetition(Mockito.any());
	}

	@Test
	void assignTeam_competitionNotExist_throwsException() {
		var competition = CompetitionTestData.getCompetitionEntity();
		var teamUId = UUID.randomUUID();
		var teamAssign = TeamsTestData.getAssignTeamDto(teamUId);

		Mockito.when(competitionService.getCompetition(competition.getGuid()))
			.thenThrow(ResourceNotFoundException.class);

		Assertions.assertThatThrownBy(() -> competitionFacade.assignTeam(competition.getGuid(), teamAssign))
			.isInstanceOf(ResourceNotFoundException.class);

		Mockito.verify(teamService, Mockito.never()).assignTeamToCompetition(Mockito.any());
	}

	@Test
	void assignTeam_assignTeam_success() {
		var competition = CompetitionTestData.getCompetitionEntity();
		var teamUId = UUID.randomUUID();
		var teamAssign = TeamsTestData.getAssignTeamDto(teamUId);
		CompetitionHasTeam hasTeamEntity = CompetitionHasTeam.builder()
			.teamUid(teamUId)
			.competition(competition)
			.build();

		Mockito.when(competitionService.getCompetition(competition.getGuid())).thenReturn(competition);
		Mockito.when(competitionService.isStarted(competition)).thenReturn(false);
		Mockito.when(competitionMapper.competitionTeamsDtoToCompetitionHasTeam(teamAssign, competition))
			.thenReturn(hasTeamEntity);

		competitionFacade.assignTeam(competition.getGuid(), teamAssign);

		Mockito.verify(teamService, Mockito.times(1)).assignTeamToCompetition(hasTeamEntity);
	}

	@Test
	void addCompetition_nullCompetition_throwsException() {
		assertThatThrownBy(() -> competitionFacade.addCompetition(null))
			.hasMessage("Please provide non null competition")
			.isInstanceOf(ValueIsMissingException.class);

		Mockito.verify(competitionService, Mockito.never()).saveCompetition(Mockito.any());
		Mockito.verify(competitionMapper, Mockito.never()).competitionCreateDtoToCompetition(Mockito.any());
		Mockito.verify(competitionMapper, Mockito.never()).competitionToCompetitionViewDto(Mockito.any());
	}

	@Test
	void addCompetition_allGood_success() {
		var competitionForCreate = CompetitionTestData.getCompetitionCreateDto();
		var competitionEntity = CompetitionTestData.getCompetitionEntity();
		competitionEntity.setGuid(null);
		var newEntity = CompetitionTestData.getCompetitionEntity();
		var competitionView = CompetitionTestData.getCompetitionView();

		Mockito.when(competitionMapper.competitionCreateDtoToCompetition(competitionForCreate))
			.thenReturn(competitionEntity);

		Mockito.when(competitionService.saveCompetition(competitionEntity)).thenReturn(newEntity);

		Mockito.when(competitionMapper.competitionToCompetitionViewDto(newEntity)).thenReturn(competitionView);

		var returnedCompetition = competitionFacade.addCompetition(competitionForCreate);

		assertThat(returnedCompetition).isEqualTo(competitionView);
		Mockito.verify(competitionMapper, Mockito.times(1)).competitionCreateDtoToCompetition(competitionForCreate);
		Mockito.verify(competitionService, Mockito.times(1)).saveCompetition(competitionEntity);
		Mockito.verify(competitionMapper, Mockito.times(1)).competitionToCompetitionViewDto(newEntity);
	}

	@Test
	void getCompetition_nullUuid_throwsException() {
		assertThatThrownBy(() -> competitionFacade.getCompetition(null))
			.hasMessage("Please provide UUID of competition you want to fetch")
			.isInstanceOf(ValueIsMissingException.class);

		Mockito.verify(competitionService, Mockito.never()).getCompetition(Mockito.any());
		Mockito.verify(competitionMapper, Mockito.never()).competitionToCompetitionViewDto(Mockito.any());
	}

	@Test
	void getCompetition_allGood_success() {
		var competitionEntity = CompetitionTestData.getCompetitionEntity();
		var competitionView = CompetitionTestData.getCompetitionView();

		Mockito.when(competitionService.getCompetition(competitionEntity.getGuid())).thenReturn(competitionEntity);

		Mockito.when(competitionMapper.competitionToCompetitionViewDto(competitionEntity)).thenReturn(competitionView);

		var returnedCompetition = competitionFacade.getCompetition(competitionEntity.getGuid());

		assertThat(returnedCompetition).isEqualTo(competitionView);
		Mockito.verify(competitionService, Mockito.times(1)).getCompetition(competitionEntity.getGuid());
		Mockito.verify(competitionMapper, Mockito.times(1)).competitionToCompetitionViewDto(competitionEntity);
	}

	@Test
	void updateCompetition_nullCompetition_throwsException() {
		assertThatThrownBy(() -> competitionFacade.updateCompetition(UUID.randomUUID(), null))
			.hasMessage("Please provide data for competition update")
			.isInstanceOf(ValueIsMissingException.class);

		Mockito.verify(competitionService, Mockito.never()).updateCompetition(Mockito.any());
		Mockito.verify(competitionMapper, Mockito.never()).competitionCreateDtoToCompetition(Mockito.any());
		Mockito.verify(competitionMapper, Mockito.never()).competitionToCompetitionViewDto(Mockito.any());
	}

	@Test
	void updateCompetition_nullUUID_throwsException() {
		var competitionCreate = CompetitionTestData.getCompetitionCreateDto();

		assertThatThrownBy(() -> competitionFacade.updateCompetition(null, competitionCreate))
			.hasMessage("Please provide UUID of competition you want to update")
			.isInstanceOf(ValueIsMissingException.class);

		Mockito.verify(competitionService, Mockito.never()).updateCompetition(Mockito.any());
		Mockito.verify(competitionMapper, Mockito.never()).competitionCreateDtoToCompetition(Mockito.any());
		Mockito.verify(competitionMapper, Mockito.never()).competitionToCompetitionViewDto(Mockito.any());
	}

	@Test
	void updateCompetition_allGood_success() {
		var competitionForCreate = CompetitionTestData.getCompetitionCreateDto();
		var competitionEntity = CompetitionTestData.getCompetitionEntity();
		var updatedEntity = CompetitionTestData.getCompetitionEntity();
		var competitionView = CompetitionTestData.getCompetitionView();
		updatedEntity.setName("Updated");

		Mockito
			.when(competitionMapper.competitionCreateDtoToCompetition(competitionForCreate,
					competitionEntity.getGuid()))
			.thenReturn(competitionEntity);

		Mockito.when(competitionService.updateCompetition(competitionEntity)).thenReturn(updatedEntity);

		Mockito.when(competitionMapper.competitionToCompetitionViewDto(updatedEntity)).thenReturn(competitionView);

		var returnedCompetition = competitionFacade.updateCompetition(competitionEntity.getGuid(),
				competitionForCreate);

		assertThat(returnedCompetition).isEqualTo(competitionView);
		Mockito.verify(competitionMapper, Mockito.times(1))
			.competitionCreateDtoToCompetition(competitionForCreate, competitionEntity.getGuid());
		Mockito.verify(competitionService, Mockito.times(1)).updateCompetition(competitionEntity);
		Mockito.verify(competitionMapper, Mockito.times(1)).competitionToCompetitionViewDto(updatedEntity);
	}

}