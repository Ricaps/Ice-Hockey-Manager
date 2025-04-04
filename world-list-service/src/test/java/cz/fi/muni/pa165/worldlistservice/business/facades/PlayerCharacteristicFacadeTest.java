package cz.fi.muni.pa165.worldlistservice.business.facades;

import cz.fi.muni.pa165.dto.worldlistservice.playercharacteristic.PlayerCharacteristicDto;
import cz.fi.muni.pa165.dto.worldlistservice.playercharacteristic.create.PlayerCharacteristicCreateDto;
import cz.fi.muni.pa165.enums.PlayerCharacteristicType;
import cz.fi.muni.pa165.worldlistservice.api.exception.ResourceInUseException;
import cz.fi.muni.pa165.worldlistservice.api.exception.ValueIsMissingException;
import cz.fi.muni.pa165.worldlistservice.business.mappers.PlayerCharacteristicMapper;
import cz.fi.muni.pa165.worldlistservice.business.services.interfaces.PlayerCharacteristicService;
import cz.fi.muni.pa165.worldlistservice.persistence.entities.PlayerCharacteristicEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PlayerCharacteristicFacadeTest {

	private final UUID testId = UUID.randomUUID();

	private final PlayerCharacteristicDto model = PlayerCharacteristicDto.builder()
		.id(testId)
		.type(PlayerCharacteristicType.SPEED)
		.value(10)
		.build();

	private final PlayerCharacteristicCreateDto createModel = PlayerCharacteristicCreateDto.builder()
		.type(PlayerCharacteristicType.SPEED)
		.value(10)
		.build();

	private final PlayerCharacteristicEntity entity = PlayerCharacteristicEntity.builder()
		.id(testId)
		.type(PlayerCharacteristicType.SPEED)
		.value(10)
		.build();

	@Mock
	private PlayerCharacteristicService service;

	@Mock
	private PlayerCharacteristicMapper mapper;

	@InjectMocks
	private PlayerCharacteristicFacadeImpl facade;

	@Test
	public void findById_entityExists_returnsModel() {
		// Arrange
		when(service.findById(testId)).thenReturn(Optional.of(entity));
		when(mapper.toDetailModel(entity)).thenReturn(model);

		// Act
		Optional<PlayerCharacteristicDto> result = facade.findById(testId);

		// Assert
		assertTrue(result.isPresent());
		assertEquals(model, result.get());
		verify(service).findById(testId);
		verify(mapper).toDetailModel(entity);
	}

	@Test
	public void findById_entityDoesNotExist_returnsEmptyOptional() {
		// Arrange
		when(service.findById(testId)).thenReturn(Optional.empty());

		// Act
		Optional<PlayerCharacteristicDto> result = facade.findById(testId);

		// Assert
		assertFalse(result.isPresent());
		verify(service).findById(testId);
		verifyNoInteractions(mapper);
	}

	@Test
	public void create_validModel_createsEntity() {
		// Arrange
		when(mapper.toEntityFromCreateModel(createModel)).thenReturn(entity);
		when(service.create(entity)).thenReturn(entity);
		when(mapper.toDetailModel(entity)).thenReturn(model);

		// Act
		PlayerCharacteristicDto result = facade.create(createModel);

		// Assert
		assertNotNull(result);
		assertEquals(model, result);
		verify(service).create(entity);
		verify(mapper).toEntityFromCreateModel(createModel);
		verify(mapper).toDetailModel(entity);
	}

	@Test
	public void create_nullModel_throwsValueIsMissingException() {
		// Arrange
		when(mapper.toEntityFromCreateModel(null)).thenReturn(null);
		when(service.create(null)).thenThrow(new ValueIsMissingException(""));

		// Act & Assert
		assertThrows(ValueIsMissingException.class, () -> facade.create(null));

		// Verify
		verify(mapper).toEntityFromCreateModel(null);
		verify(service).create(null);
		verify(mapper, never()).toDetailModel(any());
	}

	@Test
	public void update_validModel_updatesEntity() {
		// Arrange
		when(mapper.toEntityFromUpdateModel(model)).thenReturn(entity);
		when(service.update(entity)).thenReturn(entity);
		when(mapper.toDetailModel(entity)).thenReturn(model);

		// Act
		PlayerCharacteristicDto result = facade.update(model);

		// Assert
		assertNotNull(result);
		assertEquals(model, result);
		verify(service).update(entity);
		verify(mapper).toEntityFromUpdateModel(model);
		verify(mapper).toDetailModel(entity);
	}

	@Test
	public void update_nullModel_throwsValueIsMissingException() {
		// Arrange
		when(mapper.toEntityFromUpdateModel(null)).thenReturn(null);
		when(service.update(null)).thenThrow(new ValueIsMissingException(""));

		// Act & Assert
		assertThrows(ValueIsMissingException.class, () -> facade.update(null));

		// Verify
		verify(mapper).toEntityFromUpdateModel(null);
		verify(service).update(null);
		verify(mapper, never()).toDetailModel(any());
	}

	@Test
	public void delete_entityNotInUse_deletesSuccessfully() {
		// Arrange
		when(service.isEntityUsed(testId)).thenReturn(false);
		doNothing().when(service).delete(testId);

		// Act & Assert
		assertDoesNotThrow(() -> facade.delete(testId));

		// Verify
		verify(service).isEntityUsed(testId);
		verify(service).delete(testId);
	}

	@Test
	public void delete_entityInUse_throwsResourceInUseException() {
		// Arrange
		when(service.isEntityUsed(testId)).thenReturn(true);

		// Act & Assert
		assertThrows(ResourceInUseException.class, () -> facade.delete(testId));

		// Verify
		verify(service).isEntityUsed(testId);
	}

	@Test
	public void findAll_validPageable_returnsPageModel() {
		// Arrange
		Pageable pageable = mock(Pageable.class);
		Page<PlayerCharacteristicEntity> entityPage = new PageImpl<>(List.of(entity));
		Page<PlayerCharacteristicDto> modelPage = new PageImpl<>(List.of(model));

		when(service.findAll(pageable)).thenReturn(entityPage);
		when(mapper.toPageModel(entityPage)).thenReturn(modelPage);

		// Act
		Page<PlayerCharacteristicDto> result = facade.findAll(pageable);

		// Assert
		assertNotNull(result);
		assertEquals(1, result.getContent().size());
		assertEquals(model, result.getContent().getFirst());
		verify(service).findAll(pageable);
		verify(mapper).toPageModel(entityPage);
	}

}
