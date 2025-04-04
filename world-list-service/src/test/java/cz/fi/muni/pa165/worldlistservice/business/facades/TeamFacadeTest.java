package cz.fi.muni.pa165.worldlistservice.business.facades;

import cz.fi.muni.pa165.dto.worldlistservice.team.create.TeamCreateDto;
import cz.fi.muni.pa165.dto.worldlistservice.team.detail.TeamChampionshipDto;
import cz.fi.muni.pa165.dto.worldlistservice.team.detail.TeamDetailDto;
import cz.fi.muni.pa165.dto.worldlistservice.team.detail.TeamPlayerDto;
import cz.fi.muni.pa165.dto.worldlistservice.team.list.TeamListDto;
import cz.fi.muni.pa165.dto.worldlistservice.team.update.TeamUpdateDto;
import cz.fi.muni.pa165.worldlistservice.api.exception.NotFoundException;
import cz.fi.muni.pa165.worldlistservice.api.exception.ResourceInUseException;
import cz.fi.muni.pa165.worldlistservice.business.mappers.TeamMapper;
import cz.fi.muni.pa165.worldlistservice.business.services.interfaces.ChampionshipService;
import cz.fi.muni.pa165.worldlistservice.business.services.interfaces.PlayerService;
import cz.fi.muni.pa165.worldlistservice.business.services.interfaces.TeamService;
import cz.fi.muni.pa165.worldlistservice.persistence.entities.ChampionshipEntity;
import cz.fi.muni.pa165.worldlistservice.persistence.entities.PlayerEntity;
import cz.fi.muni.pa165.worldlistservice.persistence.entities.TeamEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TeamFacadeTest {

	private final UUID testTeamId = UUID.randomUUID();

	private final UUID testChampionshipId = UUID.randomUUID();

	private final UUID testPlayerId = UUID.randomUUID();

	private final TeamCreateDto createModel = TeamCreateDto.builder()
		.name("Test Team")
		.championshipId(testChampionshipId)
		.teamPlayersIds(Set.of(testPlayerId))
		.build();

	private final TeamUpdateDto updateModel = TeamUpdateDto.builder()
		.id(testTeamId)
		.name("Test Team")
		.championshipId(testChampionshipId)
		.teamPlayersIds(Set.of(testPlayerId))
		.build();

	private final TeamDetailDto detailModel = TeamDetailDto.builder()
		.id(testTeamId)
		.name("Test Team")
		.championship(TeamChampionshipDto.builder().id(testChampionshipId).build())
		.teamPlayers(Set.of(new TeamPlayerDto(testPlayerId, "Player First", "Player Last", 85)))
		.build();

	private final TeamListDto listModel = TeamListDto.builder().id(testTeamId).name("Test Team").build();

	@Mock
	private TeamService teamService;

	@Mock
	private ChampionshipService championshipService;

	@Mock
	private PlayerService playerService;

	@Mock
	private TeamMapper mapper;

	@InjectMocks
	private TeamFacadeImpl facade;

	@Test
	public void create_validModel_createsTeam() throws NotFoundException {
		// Arrange
		ChampionshipEntity championshipEntity = ChampionshipEntity.builder().id(testChampionshipId).build();
		PlayerEntity playerEntity = PlayerEntity.builder()
			.id(testPlayerId)
			.firstName("Player First")
			.lastName("Player Last")
			.overallRating(85)
			.build();

		when(championshipService.findById(testChampionshipId)).thenReturn(Optional.of(championshipEntity));
		when(playerService.findById(testPlayerId)).thenReturn(Optional.of(playerEntity));
		when(mapper.toEntityFromCreateModel(createModel)).thenReturn(new TeamEntity());
		when(teamService.create(any(TeamEntity.class))).thenReturn(new TeamEntity());
		when(mapper.toDetailModel(any(TeamEntity.class))).thenReturn(detailModel);

		// Act
		TeamDetailDto result = facade.create(createModel);

		// Assert
		assertNotNull(result);
		assertEquals(detailModel.getId(), result.getId());
		assertEquals(detailModel.getName(), result.getName());
		assertEquals(detailModel.getChampionship().getId(), result.getChampionship().getId());
		assertEquals(detailModel.getTeamPlayers(), result.getTeamPlayers());
		verify(championshipService).findById(testChampionshipId);
		verify(playerService).findById(testPlayerId);
		verify(mapper).toEntityFromCreateModel(createModel);
		verify(teamService).create(any(TeamEntity.class));
		verify(mapper).toDetailModel(any(TeamEntity.class));
	}

	@Test
	public void create_championshipNotFound_throwsNotFoundException() {
		// Arrange
		when(championshipService.findById(testChampionshipId)).thenReturn(Optional.empty());

		// Act & Assert
		assertThrows(NotFoundException.class, () -> facade.create(createModel));
		verify(championshipService).findById(testChampionshipId);
	}

	@Test
	public void create_playerNotFound_throwsNotFoundException() {
		// Arrange
		ChampionshipEntity championshipEntity = ChampionshipEntity.builder().id(testChampionshipId).build();
		when(championshipService.findById(testChampionshipId)).thenReturn(Optional.of(championshipEntity));
		when(playerService.findById(testPlayerId)).thenReturn(Optional.empty());

		// Act & Assert
		assertThrows(NotFoundException.class, () -> facade.create(createModel));
		verify(championshipService).findById(testChampionshipId);
		verify(playerService).findById(testPlayerId);
	}

	@Test
	public void findById_entityExists_returnsDetailModel() {
		// Arrange
		TeamEntity entity = TeamEntity.builder()
			.id(testTeamId)
			.name("Test Team")
			.championship(ChampionshipEntity.builder().id(testChampionshipId).build())
			.teamPlayers(new HashSet<>(Collections.singletonList(
					new PlayerEntity(testPlayerId, "Player First", "Player Last", 80, 1000, null, Set.of()))))
			.build();

		when(teamService.findById(testTeamId)).thenReturn(Optional.of(entity));
		when(mapper.toDetailModel(entity)).thenReturn(detailModel);

		// Act
		Optional<TeamDetailDto> result = facade.findById(testTeamId);

		// Assert
		assertTrue(result.isPresent());
		assertEquals(detailModel, result.get());
		verify(teamService).findById(testTeamId);
		verify(mapper).toDetailModel(entity);
	}

	@Test
	public void findById_entityDoesNotExist_returnsEmptyOptional() {
		// Arrange
		when(teamService.findById(testTeamId)).thenReturn(Optional.empty());

		// Act
		Optional<TeamDetailDto> result = facade.findById(testTeamId);

		// Assert
		assertFalse(result.isPresent());
		verify(teamService).findById(testTeamId);
		verifyNoInteractions(mapper);
	}

	@Test
	public void update_validModel_updatesTeam() throws NotFoundException {
		// Arrange
		ChampionshipEntity championshipEntity = ChampionshipEntity.builder().id(testChampionshipId).build();
		PlayerEntity playerEntity = PlayerEntity.builder().id(testPlayerId).build();

		when(championshipService.findById(testChampionshipId)).thenReturn(Optional.of(championshipEntity));
		when(playerService.findById(testPlayerId)).thenReturn(Optional.of(playerEntity));
		when(mapper.toEntityFromUpdateModel(updateModel)).thenReturn(new TeamEntity());
		when(teamService.update(any(TeamEntity.class))).thenReturn(new TeamEntity());
		when(mapper.toDetailModel(any(TeamEntity.class))).thenReturn(detailModel);

		// Act
		TeamDetailDto result = facade.update(updateModel);

		// Assert
		assertNotNull(result);
		assertEquals(detailModel.getId(), result.getId());
		assertEquals(detailModel.getName(), result.getName());
		assertEquals(detailModel.getChampionship().getId(), result.getChampionship().getId());
		assertEquals(detailModel.getTeamPlayers(), result.getTeamPlayers());
		verify(championshipService).findById(testChampionshipId);
		verify(playerService).findById(testPlayerId);
		verify(mapper).toEntityFromUpdateModel(updateModel);
		verify(teamService).update(any(TeamEntity.class));
		verify(mapper).toDetailModel(any(TeamEntity.class));
	}

	@Test
	public void update_championshipNotFound_throwsNotFoundException() {
		// Arrange
		when(championshipService.findById(testChampionshipId)).thenReturn(Optional.empty());

		// Act & Assert
		assertThrows(NotFoundException.class, () -> facade.update(updateModel));
		verify(championshipService).findById(testChampionshipId);
	}

	@Test
	public void update_playerNotFound_throwsNotFoundException() {
		// Arrange
		ChampionshipEntity championshipEntity = ChampionshipEntity.builder().id(testChampionshipId).build();
		when(championshipService.findById(testChampionshipId)).thenReturn(Optional.of(championshipEntity));
		when(playerService.findById(testPlayerId)).thenReturn(Optional.empty());

		// Act & Assert
		assertThrows(NotFoundException.class, () -> facade.update(updateModel));
		verify(championshipService).findById(testChampionshipId);
		verify(playerService).findById(testPlayerId);
	}

	@Test
	public void delete_entityNotInUse_deletesSuccessfully() {
		// Arrange
		when(teamService.isEntityUsed(testTeamId)).thenReturn(false);
		doNothing().when(teamService).delete(testTeamId);

		// Act & Assert
		assertDoesNotThrow(() -> facade.delete(testTeamId));

		// Verify
		verify(teamService).isEntityUsed(testTeamId);
		verify(teamService).delete(testTeamId);
	}

	@Test
	public void delete_entityInUse_throwsResourceInUseException() {
		// Arrange
		when(teamService.isEntityUsed(testTeamId)).thenReturn(true);

		// Act & Assert
		assertThrows(ResourceInUseException.class, () -> facade.delete(testTeamId));

		// Verify
		verify(teamService).isEntityUsed(testTeamId);
	}

	@Test
	public void findAll_validPageable_returnsPageModel() {
		// Arrange
		Pageable pageable = mock(Pageable.class);
		Page<TeamEntity> entityPage = new PageImpl<>(List.of(new TeamEntity(testTeamId, "Test Team", null, null)));
		Page<TeamListDto> modelPage = new PageImpl<>(List.of(listModel));

		when(teamService.findAll(pageable)).thenReturn(entityPage);
		when(mapper.toPageModel(entityPage)).thenReturn(modelPage);

		// Act
		Page<TeamListDto> result = facade.findAll(pageable);

		// Assert
		assertNotNull(result);
		assertEquals(1, result.getContent().size());
		assertEquals(listModel, result.getContent().getFirst());
		verify(teamService).findAll(pageable);
		verify(mapper).toPageModel(entityPage);
	}

}
