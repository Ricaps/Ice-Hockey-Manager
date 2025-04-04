package cz.fi.muni.pa165.worldlistservice.business.facades;

import cz.fi.muni.pa165.dto.worldlistservice.championship.create.ChampionshipCreateDto;
import cz.fi.muni.pa165.dto.worldlistservice.championship.detail.ChampionshipDetailDto;
import cz.fi.muni.pa165.dto.worldlistservice.championship.detail.ChampionshipTeamDto;
import cz.fi.muni.pa165.dto.worldlistservice.championship.list.ChampionshipListDto;
import cz.fi.muni.pa165.dto.worldlistservice.championship.update.ChampionshipUpdateDto;
import cz.fi.muni.pa165.dto.worldlistservice.championshippregion.ChampionshipRegionDto;
import cz.fi.muni.pa165.worldlistservice.api.exception.NotFoundException;
import cz.fi.muni.pa165.worldlistservice.api.exception.ResourceInUseException;
import cz.fi.muni.pa165.worldlistservice.business.mappers.ChampionshipMapper;
import cz.fi.muni.pa165.worldlistservice.business.services.interfaces.ChampionshipRegionService;
import cz.fi.muni.pa165.worldlistservice.business.services.interfaces.ChampionshipService;
import cz.fi.muni.pa165.worldlistservice.business.services.interfaces.TeamService;
import cz.fi.muni.pa165.worldlistservice.persistence.entities.ChampionshipEntity;
import cz.fi.muni.pa165.worldlistservice.persistence.entities.ChampionshipRegionEntity;
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
public class ChampionshipFacadeTest {

	private final UUID testChampionshipId = UUID.randomUUID();

	private final UUID testRegionId = UUID.randomUUID();

	private final UUID testTeamId = UUID.randomUUID();

	private final ChampionshipCreateDto createModel = ChampionshipCreateDto.builder()
		.name("Test Championship")
		.championshipRegionId(testRegionId)
		.championshipTeamsIds(Set.of(testTeamId))
		.build();

	private final ChampionshipUpdateDto updateModel = ChampionshipUpdateDto.builder()
		.id(testChampionshipId)
		.name("Test Championship")
		.championshipRegionId(testRegionId)
		.championshipTeamsIds(Set.of(testTeamId))
		.build();

	private final ChampionshipDetailDto detailModel = ChampionshipDetailDto.builder()
		.id(testChampionshipId)
		.name("Test Championship")
		.championshipRegion(ChampionshipRegionDto.builder().id(testRegionId).build())
		.championshipTeams(Set.of(ChampionshipTeamDto.builder().id(testTeamId).build()))
		.build();

	private final ChampionshipListDto listModel = ChampionshipListDto.builder()
		.id(testChampionshipId)
		.name("Test Championship")
		.build();

	@Mock
	private ChampionshipService service;

	@Mock
	private ChampionshipRegionService championshipRegionService;

	@Mock
	private TeamService teamService;

	@Mock
	private ChampionshipMapper mapper;

	@InjectMocks
	private ChampionshipFacadeImpl facade;

	@Test
	public void create_validModel_createsChampionship() throws NotFoundException {
		// Arrange
		ChampionshipRegionEntity regionEntity = ChampionshipRegionEntity.builder().id(testRegionId).build();
		TeamEntity teamEntity = TeamEntity.builder().id(testTeamId).build();

		when(championshipRegionService.findById(testRegionId)).thenReturn(Optional.of(regionEntity));
		when(teamService.findById(testTeamId)).thenReturn(Optional.of(teamEntity));
		when(mapper.toEntityFromCreateModel(createModel)).thenReturn(new ChampionshipEntity());
		when(service.create(any(ChampionshipEntity.class))).thenReturn(new ChampionshipEntity());
		when(mapper.toDetailModel(any(ChampionshipEntity.class))).thenReturn(detailModel);

		// Act
		ChampionshipDetailDto result = facade.create(createModel);

		// Assert
		assertNotNull(result);
		assertEquals(detailModel.getId(), result.getId());
		assertEquals(detailModel.getName(), result.getName());
		assertEquals(detailModel.getChampionshipRegion().getId(), result.getChampionshipRegion().getId());
		assertEquals(detailModel.getChampionshipTeams(), result.getChampionshipTeams());
		verify(championshipRegionService).findById(testRegionId);
		verify(teamService).findById(testTeamId);
		verify(mapper).toEntityFromCreateModel(createModel);
		verify(service).create(any(ChampionshipEntity.class));
		verify(mapper).toDetailModel(any(ChampionshipEntity.class));
	}

	@Test
	public void create_regionNotFound_throwsNotFoundException() {
		// Arrange
		when(championshipRegionService.findById(testRegionId)).thenReturn(Optional.empty());

		// Act & Assert
		assertThrows(NotFoundException.class, () -> facade.create(createModel));
		verify(championshipRegionService).findById(testRegionId);
	}

	@Test
	public void create_teamNotFound_throwsNotFoundException() {
		// Arrange
		ChampionshipRegionEntity regionEntity = ChampionshipRegionEntity.builder().id(testRegionId).build();
		when(championshipRegionService.findById(testRegionId)).thenReturn(Optional.of(regionEntity));
		when(teamService.findById(testTeamId)).thenReturn(Optional.empty());

		// Act & Assert
		assertThrows(NotFoundException.class, () -> facade.create(createModel));
		verify(championshipRegionService).findById(testRegionId);
		verify(teamService).findById(testTeamId);
	}

	@Test
	public void findById_entityExists_returnsDetailModel() {
		// Arrange
		ChampionshipEntity entity = ChampionshipEntity.builder()
			.id(testChampionshipId)
			.name("Test Championship")
			.championshipRegion(ChampionshipRegionEntity.builder().id(testRegionId).name("Region").build())
			.championshipTeams(
					new HashSet<>(Collections.singletonList(TeamEntity.builder().id(testTeamId).name("Team").build())))
			.build();

		when(service.findById(testChampionshipId)).thenReturn(Optional.of(entity));
		when(mapper.toDetailModel(entity)).thenReturn(detailModel);

		// Act
		Optional<ChampionshipDetailDto> result = facade.findById(testChampionshipId);

		// Assert
		assertTrue(result.isPresent());
		assertEquals(detailModel, result.get());
		verify(service).findById(testChampionshipId);
		verify(mapper).toDetailModel(entity);
	}

	@Test
	public void findById_entityDoesNotExist_returnsEmptyOptional() {
		// Arrange
		when(service.findById(testChampionshipId)).thenReturn(Optional.empty());

		// Act
		Optional<ChampionshipDetailDto> result = facade.findById(testChampionshipId);

		// Assert
		assertFalse(result.isPresent());
		verify(service).findById(testChampionshipId);
		verifyNoInteractions(mapper);
	}

	@Test
	public void update_validModel_updatesChampionship() throws NotFoundException {
		// Arrange
		ChampionshipRegionEntity regionEntity = ChampionshipRegionEntity.builder().id(testRegionId).build();
		TeamEntity teamEntity = TeamEntity.builder().id(testTeamId).build();

		when(championshipRegionService.findById(testRegionId)).thenReturn(Optional.of(regionEntity));
		when(teamService.findById(testTeamId)).thenReturn(Optional.of(teamEntity));
		when(mapper.toEntityFromUpdateModel(updateModel)).thenReturn(new ChampionshipEntity());
		when(service.update(any(ChampionshipEntity.class))).thenReturn(new ChampionshipEntity());
		when(mapper.toDetailModel(any(ChampionshipEntity.class))).thenReturn(detailModel);

		// Act
		ChampionshipDetailDto result = facade.update(updateModel);

		// Assert
		assertNotNull(result);
		assertEquals(detailModel.getId(), result.getId());
		assertEquals(detailModel.getName(), result.getName());
		assertEquals(detailModel.getChampionshipRegion().getId(), result.getChampionshipRegion().getId());
		assertEquals(detailModel.getChampionshipTeams(), result.getChampionshipTeams());
		verify(championshipRegionService).findById(testRegionId);
		verify(teamService).findById(testTeamId);
		verify(mapper).toEntityFromUpdateModel(updateModel);
		verify(service).update(any(ChampionshipEntity.class));
		verify(mapper).toDetailModel(any(ChampionshipEntity.class));
	}

	@Test
	public void update_regionNotFound_throwsNotFoundException() {
		// Arrange
		when(championshipRegionService.findById(testRegionId)).thenReturn(Optional.empty());

		// Act & Assert
		assertThrows(NotFoundException.class, () -> facade.update(updateModel));
		verify(championshipRegionService).findById(testRegionId);
	}

	@Test
	public void update_teamNotFound_throwsNotFoundException() {
		// Arrange
		ChampionshipRegionEntity regionEntity = ChampionshipRegionEntity.builder().id(testRegionId).build();
		when(championshipRegionService.findById(testRegionId)).thenReturn(Optional.of(regionEntity));
		when(teamService.findById(testTeamId)).thenReturn(Optional.empty());

		// Act & Assert
		assertThrows(NotFoundException.class, () -> facade.update(updateModel));
		verify(championshipRegionService).findById(testRegionId);
		verify(teamService).findById(testTeamId);
	}

	@Test
	public void delete_entityNotInUse_deletesSuccessfully() {
		// Arrange
		when(service.isEntityUsed(testChampionshipId)).thenReturn(false);
		doNothing().when(service).delete(testChampionshipId);

		// Act & Assert
		assertDoesNotThrow(() -> facade.delete(testChampionshipId));

		// Verify
		verify(service).isEntityUsed(testChampionshipId);
		verify(service).delete(testChampionshipId);
	}

	@Test
	public void delete_entityInUse_throwsResourceInUseException() {
		// Arrange
		when(service.isEntityUsed(testChampionshipId)).thenReturn(true);

		// Act & Assert
		assertThrows(ResourceInUseException.class, () -> facade.delete(testChampionshipId));

		// Verify
		verify(service).isEntityUsed(testChampionshipId);
	}

	@Test
	public void findAll_validPageable_returnsPageModel() {
		// Arrange
		Pageable pageable = mock(Pageable.class);
		Page<ChampionshipEntity> entityPage = new PageImpl<>(
				List.of(new ChampionshipEntity(testChampionshipId, "Test Championship", null, null)));
		Page<ChampionshipListDto> modelPage = new PageImpl<>(List.of(listModel));

		when(service.findAll(pageable)).thenReturn(entityPage);
		when(mapper.toPageModel(entityPage)).thenReturn(modelPage);

		// Act
		Page<ChampionshipListDto> result = facade.findAll(pageable);

		// Assert
		assertNotNull(result);
		assertEquals(1, result.getContent().size());
		assertEquals(listModel, result.getContent().getFirst());
		verify(service).findAll(pageable);
		verify(mapper).toPageModel(entityPage);
	}

}
