package cz.fi.muni.pa165.worldlistservice.unit.business.facades;

import cz.fi.muni.pa165.dto.worldlistservice.player.create.PlayerCreateDto;
import cz.fi.muni.pa165.dto.worldlistservice.player.detail.PlayerDetailDto;
import cz.fi.muni.pa165.dto.worldlistservice.player.detail.PlayerTeamDto;
import cz.fi.muni.pa165.dto.worldlistservice.player.list.PlayerListDto;
import cz.fi.muni.pa165.dto.worldlistservice.player.update.PlayerUpdateDto;
import cz.fi.muni.pa165.worldlistservice.api.exception.NotFoundException;
import cz.fi.muni.pa165.worldlistservice.api.exception.ResourceInUseException;
import cz.fi.muni.pa165.worldlistservice.business.facades.PlayerFacadeImpl;
import cz.fi.muni.pa165.worldlistservice.business.mappers.PlayerMapper;
import cz.fi.muni.pa165.worldlistservice.business.services.interfaces.PlayerCharacteristicService;
import cz.fi.muni.pa165.worldlistservice.business.services.interfaces.PlayerService;
import cz.fi.muni.pa165.worldlistservice.business.services.interfaces.TeamService;
import cz.fi.muni.pa165.worldlistservice.persistence.entities.PlayerCharacteristicEntity;
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
public class PlayerFacadeTest {

	private final UUID testPlayerId = UUID.randomUUID();

	private final UUID testTeamId = UUID.randomUUID();

	private final UUID testCharacteristicId = UUID.randomUUID();

	private final PlayerCreateDto createModel = PlayerCreateDto.builder()
		.firstName("Player First")
		.lastName("Player Last")
		.teamId(testTeamId)
		.playerCharacteristicsIds(Set.of(testCharacteristicId))
		.build();

	private final PlayerUpdateDto updateModel = PlayerUpdateDto.builder()
		.id(testPlayerId)
		.firstName("Player First")
		.lastName("Player Last")
		.teamId(testTeamId)
		.playerCharacteristicsIds(Set.of(testCharacteristicId))
		.build();

	private final PlayerDetailDto detailModel = PlayerDetailDto.builder()
		.id(testPlayerId)
		.firstName("Player First")
		.lastName("Player Last")
		.overallRating(85)
		.team(new PlayerTeamDto(testTeamId, "Test Team", null))
		.playerCharacteristics(new HashSet<>())
		.build();

	private final PlayerListDto listModel = PlayerListDto.builder()
		.id(testPlayerId)
		.firstName("Player First")
		.lastName("Player Last")
		.overallRating(85)
		.build();

	@Mock
	private PlayerService playerService;

	@Mock
	private TeamService teamService;

	@Mock
	private PlayerCharacteristicService playerCharacteristicService;

	@Mock
	private PlayerMapper mapper;

	@InjectMocks
	private PlayerFacadeImpl facade;

	@Test
	public void create_validModel_createsPlayer() throws NotFoundException {
		// Arrange
		TeamEntity teamEntity = TeamEntity.builder().id(testTeamId).build();
		PlayerCharacteristicEntity characteristicEntity = PlayerCharacteristicEntity.builder()
			.id(testCharacteristicId)
			.build();

		when(teamService.findById(testTeamId)).thenReturn(Optional.of(teamEntity));
		when(playerCharacteristicService.findById(testCharacteristicId)).thenReturn(Optional.of(characteristicEntity));
		when(mapper.toEntityFromCreateModel(createModel)).thenReturn(new PlayerEntity());
		when(playerService.updateRating(any(PlayerEntity.class))).thenReturn(new PlayerEntity());
		when(playerService.create(any(PlayerEntity.class))).thenReturn(new PlayerEntity());
		when(mapper.toDetailModel(any(PlayerEntity.class))).thenReturn(detailModel);

		// Act
		PlayerDetailDto result = facade.create(createModel);

		// Assert
		assertNotNull(result);
		assertEquals(detailModel.getId(), result.getId());
		assertEquals(detailModel.getFirstName(), result.getFirstName());
		assertEquals(detailModel.getLastName(), result.getLastName());
		assertEquals(detailModel.getOverallRating(), result.getOverallRating());
		assertEquals(detailModel.getTeam().getId(), result.getTeam().getId());
		verify(teamService).findById(testTeamId);
		verify(playerCharacteristicService).findById(testCharacteristicId);
		verify(mapper).toEntityFromCreateModel(createModel);
		verify(playerService).create(any(PlayerEntity.class));
		verify(mapper).toDetailModel(any(PlayerEntity.class));
	}

	@Test
	public void create_teamNotFound_throwsNotFoundException() {
		// Arrange
		when(teamService.findById(testTeamId)).thenReturn(Optional.empty());

		// Act & Assert
		assertThrows(NotFoundException.class, () -> facade.create(createModel));
		verify(teamService).findById(testTeamId);
	}

	@Test
	public void create_characteristicNotFound_throwsNotFoundException() {
		// Arrange
		TeamEntity teamEntity = TeamEntity.builder().id(testTeamId).build();
		when(teamService.findById(testTeamId)).thenReturn(Optional.of(teamEntity));
		when(playerCharacteristicService.findById(testCharacteristicId)).thenReturn(Optional.empty());

		// Act & Assert
		assertThrows(NotFoundException.class, () -> facade.create(createModel));
		verify(teamService).findById(testTeamId);
		verify(playerCharacteristicService).findById(testCharacteristicId);
	}

	@Test
	public void findById_entityExists_returnsDetailModel() {
		// Arrange
		PlayerEntity entity = PlayerEntity.builder()
			.id(testPlayerId)
			.firstName("Player First")
			.lastName("Player Last")
			.overallRating(85)
			.team(new TeamEntity(testTeamId, "Test Team", null, null))
			.build();

		when(playerService.findById(testPlayerId)).thenReturn(Optional.of(entity));
		when(mapper.toDetailModel(entity)).thenReturn(detailModel);

		// Act
		Optional<PlayerDetailDto> result = facade.findById(testPlayerId);

		// Assert
		assertTrue(result.isPresent());
		assertEquals(detailModel, result.get());
		verify(playerService).findById(testPlayerId);
		verify(mapper).toDetailModel(entity);
	}

	@Test
	public void findById_entityDoesNotExist_returnsEmptyOptional() {
		// Arrange
		when(playerService.findById(testPlayerId)).thenReturn(Optional.empty());

		// Act
		Optional<PlayerDetailDto> result = facade.findById(testPlayerId);

		// Assert
		assertFalse(result.isPresent());
		verify(playerService).findById(testPlayerId);
		verifyNoInteractions(mapper);
	}

	@Test
	public void update_validModel_updatesPlayer() throws NotFoundException {
		// Arrange
		TeamEntity teamEntity = TeamEntity.builder().id(testTeamId).build();
		PlayerCharacteristicEntity characteristicEntity = PlayerCharacteristicEntity.builder()
			.id(testCharacteristicId)
			.build();

		when(teamService.findById(testTeamId)).thenReturn(Optional.of(teamEntity));
		when(playerCharacteristicService.findById(testCharacteristicId)).thenReturn(Optional.of(characteristicEntity));
		when(mapper.toEntityFromUpdateModel(updateModel)).thenReturn(new PlayerEntity());
		when(playerService.updateRating(any(PlayerEntity.class))).thenReturn(new PlayerEntity());
		when(playerService.update(any(PlayerEntity.class))).thenReturn(new PlayerEntity());
		when(mapper.toDetailModel(any(PlayerEntity.class))).thenReturn(detailModel);

		// Act
		PlayerDetailDto result = facade.update(updateModel);

		// Assert
		assertNotNull(result);
		assertEquals(detailModel.getId(), result.getId());
		assertEquals(detailModel.getFirstName(), result.getFirstName());
		assertEquals(detailModel.getLastName(), result.getLastName());
		assertEquals(detailModel.getOverallRating(), result.getOverallRating());
		assertEquals(detailModel.getTeam().getId(), result.getTeam().getId());
		verify(teamService).findById(testTeamId);
		verify(playerCharacteristicService).findById(testCharacteristicId);
		verify(mapper).toEntityFromUpdateModel(updateModel);
		verify(playerService).update(any(PlayerEntity.class));
		verify(mapper).toDetailModel(any(PlayerEntity.class));
	}

	@Test
	public void update_teamNotFound_throwsNotFoundException() {
		// Arrange
		when(teamService.findById(testTeamId)).thenReturn(Optional.empty());

		// Act & Assert
		assertThrows(NotFoundException.class, () -> facade.update(updateModel));
		verify(teamService).findById(testTeamId);
	}

	@Test
	public void update_characteristicNotFound_throwsNotFoundException() {
		// Arrange
		TeamEntity teamEntity = TeamEntity.builder().id(testTeamId).build();
		when(teamService.findById(testTeamId)).thenReturn(Optional.of(teamEntity));
		when(playerCharacteristicService.findById(testCharacteristicId)).thenReturn(Optional.empty());

		// Act & Assert
		assertThrows(NotFoundException.class, () -> facade.update(updateModel));
		verify(teamService).findById(testTeamId);
		verify(playerCharacteristicService).findById(testCharacteristicId);
	}

	@Test
	public void delete_entityNotInUse_deletesSuccessfully() {
		// Arrange
		when(playerService.isEntityUsed(testPlayerId)).thenReturn(false);
		doNothing().when(playerService).delete(testPlayerId);

		// Act & Assert
		assertDoesNotThrow(() -> facade.delete(testPlayerId));

		// Verify
		verify(playerService).isEntityUsed(testPlayerId);
		verify(playerService).delete(testPlayerId);
	}

	@Test
	public void delete_entityInUse_throwsResourceInUseException() {
		// Arrange
		when(playerService.isEntityUsed(testPlayerId)).thenReturn(true);

		// Act & Assert
		assertThrows(ResourceInUseException.class, () -> facade.delete(testPlayerId));

		// Verify
		verify(playerService).isEntityUsed(testPlayerId);
	}

	@Test
	public void findAll_validPageable_returnsPageModel() {
		// Arrange
		Pageable pageable = mock(Pageable.class);
		Page<PlayerEntity> entityPage = new PageImpl<>(List
			.of(new PlayerEntity(testPlayerId, "Player First", "Player Last", 85, 1000, false, null, new HashSet<>())));
		Page<PlayerListDto> modelPage = new PageImpl<>(List.of(listModel));

		when(playerService.findAll(pageable)).thenReturn(entityPage);
		when(mapper.toPageModel(entityPage)).thenReturn(modelPage);

		// Act
		Page<PlayerListDto> result = facade.findAll(pageable);

		// Assert
		assertNotNull(result);
		assertEquals(1, result.getContent().size());
		assertEquals(listModel, result.getContent().getFirst());
		verify(playerService).findAll(pageable);
		verify(mapper).toPageModel(entityPage);
	}

}
