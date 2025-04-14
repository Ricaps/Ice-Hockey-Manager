package cz.fi.muni.pa165.worldlistservice.unit.api;

import cz.fi.muni.pa165.dto.worldlistservice.team.create.TeamCreateDto;
import cz.fi.muni.pa165.dto.worldlistservice.team.detail.TeamChampionshipDto;
import cz.fi.muni.pa165.dto.worldlistservice.team.detail.TeamDetailDto;
import cz.fi.muni.pa165.dto.worldlistservice.team.list.TeamListDto;
import cz.fi.muni.pa165.dto.worldlistservice.team.update.TeamUpdateDto;
import cz.fi.muni.pa165.worldlistservice.api.controllers.TeamControllerImpl;
import cz.fi.muni.pa165.worldlistservice.api.exception.NotFoundException;
import cz.fi.muni.pa165.worldlistservice.api.exception.ResourceInUseException;
import cz.fi.muni.pa165.worldlistservice.business.facades.interfaces.TeamFacade;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TeamControllerTest {

	private final UUID testTeamId = UUID.randomUUID();

	private final TeamDetailDto teamDetailDto = TeamDetailDto.builder()
		.id(testTeamId)
		.name("Test Team")
		.championship(TeamChampionshipDto.builder().id(UUID.randomUUID()).name("Championship Team").build())
		.build();

	private final TeamCreateDto createModel = TeamCreateDto.builder()
		.name("Test Team")
		.championshipId(UUID.randomUUID())
		.teamPlayersIds(Set.of(UUID.randomUUID()))
		.build();

	private final TeamUpdateDto updateModel = TeamUpdateDto.builder()
		.id(testTeamId)
		.name("Test Team")
		.championshipId(UUID.randomUUID())
		.teamPlayersIds(Set.of(UUID.randomUUID()))
		.build();

	private final TeamListDto teamListDto = TeamListDto.builder().id(testTeamId).name("Test Team").build();

	@Mock
	private TeamFacade teamFacade;

	@InjectMocks
	private TeamControllerImpl teamController;

	@Test
	public void getTeamById_teamExists_returnsTeamDetail() {
		// Arrange
		when(teamFacade.findById(testTeamId)).thenReturn(Optional.of(teamDetailDto));

		// Act
		ResponseEntity<TeamDetailDto> response = teamController.getTeamById(testTeamId);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(teamDetailDto, response.getBody());
		verify(teamFacade).findById(testTeamId);
	}

	@Test
	public void getTeamById_teamDoesNotExist_returnsNotFound() {
		// Arrange
		when(teamFacade.findById(testTeamId)).thenReturn(Optional.empty());

		// Act
		ResponseEntity<TeamDetailDto> response = teamController.getTeamById(testTeamId);

		// Assert
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertNull(response.getBody());
		verify(teamFacade).findById(testTeamId);
	}

	@Test
	public void getAllTeams_validRequest_returnsPageOfTeams() {
		// Arrange
		Pageable pageable = mock(Pageable.class);
		Page<TeamListDto> teamsPage = new PageImpl<>(List.of(teamListDto));
		when(teamFacade.findAll(pageable)).thenReturn(teamsPage);

		// Act
		ResponseEntity<Page<TeamListDto>> response = teamController.getAllTeams(pageable);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(teamsPage, response.getBody());
		verify(teamFacade).findAll(pageable);
	}

	@Test
	public void getAllTeams_noTeamsFound_returnsNotFound() {
		// Arrange
		Pageable pageable = mock(Pageable.class);
		Page<TeamListDto> teamsPage = new PageImpl<>(List.of());
		when(teamFacade.findAll(pageable)).thenReturn(teamsPage);

		// Act
		ResponseEntity<Page<TeamListDto>> response = teamController.getAllTeams(pageable);

		// Assert
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertNull(response.getBody());
		verify(teamFacade).findAll(pageable);
	}

	@Test
	public void createTeam_validRequest_returnsTeamDetail() {
		// Arrange
		when(teamFacade.create(createModel)).thenReturn(teamDetailDto);

		// Act
		ResponseEntity<TeamDetailDto> response = teamController.createTeam(createModel);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(teamDetailDto, response.getBody());
		verify(teamFacade).create(createModel);
	}

	@Test
	public void updateTeam_validRequest_returnsTeamDetail() {
		// Arrange
		when(teamFacade.update(updateModel)).thenReturn(teamDetailDto);

		// Act
		ResponseEntity<TeamDetailDto> response = teamController.updateTeam(updateModel);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(teamDetailDto, response.getBody());
		verify(teamFacade).update(updateModel);
	}

	@Test
	public void updateTeam_notFound_throwsNotFoundException() {
		// Arrange
		doThrow(new NotFoundException("", UUID.randomUUID())).when(teamFacade).update(updateModel);

		// Act & Assert
		assertThrows(NotFoundException.class, () -> teamController.updateTeam(updateModel));
		verify(teamFacade).update(updateModel);
	}

	@Test
	public void deleteTeam_validId_returnsOk() {
		// Act
		ResponseEntity<Void> response = teamController.deleteTeam(testTeamId);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		verify(teamFacade).delete(testTeamId);
	}

	@Test
	public void deleteTeam_teamNotFound_throwsNotFoundException() {
		// Arrange
		doThrow(new NotFoundException("", UUID.randomUUID())).when(teamFacade).delete(testTeamId);

		// Act & Assert
		assertThrows(NotFoundException.class, () -> teamController.deleteTeam(testTeamId));
		verify(teamFacade).delete(testTeamId);
	}

	@Test
	public void deleteTeam_teamInUse_throwsResourceInUseException() {
		// Arrange
		doThrow(new ResourceInUseException("", UUID.randomUUID())).when(teamFacade).delete(testTeamId);

		// Act & Assert
		assertThrows(ResourceInUseException.class, () -> teamController.deleteTeam(testTeamId));
		verify(teamFacade).delete(testTeamId);
	}

}
