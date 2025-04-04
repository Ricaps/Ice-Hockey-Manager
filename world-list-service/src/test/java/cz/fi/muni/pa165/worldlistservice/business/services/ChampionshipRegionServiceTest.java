package cz.fi.muni.pa165.worldlistservice.business.services;

import cz.fi.muni.pa165.enums.ChampionshipRegionType;
import cz.fi.muni.pa165.worldlistservice.api.exception.NotFoundException;
import cz.fi.muni.pa165.worldlistservice.api.exception.ValueIsMissingException;
import cz.fi.muni.pa165.worldlistservice.persistence.entities.ChampionshipRegionEntity;
import cz.fi.muni.pa165.worldlistservice.persistence.repositories.ChampionshipRegionRepository;
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
public class ChampionshipRegionServiceTest {

	private final ChampionshipRegionEntity entity = ChampionshipRegionEntity.builder()
		.id(UUID.randomUUID())
		.name("Championship Region")
		.type(ChampionshipRegionType.CONTINENTAL)
		.build();

	@Mock
	private ChampionshipRegionRepository repository;

	@InjectMocks
	private ChampionshipRegionServiceImpl service;

	@Test
	void create_validEntity_entitySaved() {
		// Arrange
		when(repository.save(entity)).thenReturn(entity);

		// Act
		ChampionshipRegionEntity savedEntity = service.create(entity);

		// Assert
		assertNotNull(savedEntity);
		assertEquals(entity.getName(), savedEntity.getName());
		verify(repository, times(1)).save(entity);
	}

	@Test
	void create_nullEntity_throwsValueIsMissingException() {
		// Arrange
		// Act & Assert
		assertThrows(ValueIsMissingException.class, () -> service.create(null));
	}

	@Test
	void findById_existingId_entityFound() {
		// Arrange
		when(repository.findById(entity.getId())).thenReturn(Optional.of(entity));

		// Act
		Optional<ChampionshipRegionEntity> foundEntity = service.findById(entity.getId());

		// Assert
		assertTrue(foundEntity.isPresent());
		assertEquals(entity.getId(), foundEntity.get().getId());
		verify(repository, times(1)).findById(entity.getId());
	}

	@Test
	void findById_nonExistingId_entityNotFound() {
		// Arrange
		when(repository.findById(any(UUID.class))).thenReturn(Optional.empty());

		// Act
		Optional<ChampionshipRegionEntity> foundEntity = service.findById(UUID.randomUUID());

		// Assert
		assertFalse(foundEntity.isPresent());
		verify(repository, times(1)).findById(any(UUID.class));
	}

	@Test
	void findById_nullId_throwsValueIsMissingException() {
		// Arrange
		// Act & Assert
		assertThrows(ValueIsMissingException.class, () -> service.findById(null));
	}

	@Test
	void findAll_nonEmptyRepository_entitiesReturned() {
		// Arrange
		Page<ChampionshipRegionEntity> page = new PageImpl<>(List.of(entity));
		when(repository.findAll(any(Pageable.class))).thenReturn(page);

		// Act
		Page<ChampionshipRegionEntity> result = service.findAll(Pageable.unpaged());

		// Assert
		assertNotNull(result);
		assertEquals(1, result.getContent().size());
		assertEquals(entity.getName(), result.getContent().getFirst().getName());
		verify(repository, times(1)).findAll(any(Pageable.class));
	}

	@Test
	void findAll_emptyRepository_noEntitiesReturned() {
		// Arrange
		Page<ChampionshipRegionEntity> page = new PageImpl<>(List.of());
		when(repository.findAll(any(Pageable.class))).thenReturn(page);

		// Act
		Page<ChampionshipRegionEntity> result = service.findAll(Pageable.unpaged());

		// Assert
		assertNotNull(result);
		assertTrue(result.isEmpty());
		verify(repository, times(1)).findAll(any(Pageable.class));
	}

	@Test
	void findAll_nullPageable_throwsValueIsMissingException() {
		// Arrange
		// Act & Assert
		assertThrows(ValueIsMissingException.class, () -> service.findAll(null));
	}

	@Test
	void update_existingEntity_entityUpdated() {
		// Arrange
		when(repository.existsById(entity.getId())).thenReturn(true);
		when(repository.save(entity)).thenReturn(entity);

		// Act
		ChampionshipRegionEntity updatedEntity = service.update(entity);

		// Assert
		assertNotNull(updatedEntity);
		assertEquals(entity.getName(), updatedEntity.getName());
		verify(repository, times(1)).save(entity);
	}

	@Test
	void update_nonExistingEntity_throwsNotFoundException() {
		// Arrange
		when(repository.existsById(entity.getId())).thenReturn(false);

		// Act & Assert
		assertThrows(NotFoundException.class, () -> service.update(entity));
		verify(repository, times(1)).existsById(entity.getId());
	}

	@Test
	void update_nullEntity_throwsValueIsMissingException() {
		// Arrange
		// Act & Assert
		assertThrows(ValueIsMissingException.class, () -> service.update(null));
	}

	@Test
	void update_modifiedEntity_entityUpdated() {
		// Arrange
		entity.setName("New Region Name");
		when(repository.existsById(entity.getId())).thenReturn(true);
		when(repository.save(entity)).thenReturn(entity);

		// Act
		ChampionshipRegionEntity updatedEntity = service.update(entity);

		// Assert
		assertNotNull(updatedEntity);
		assertEquals("New Region Name", updatedEntity.getName());
		verify(repository, times(1)).save(entity);
	}

	@Test
	void delete_existingId_entityDeleted() {
		// Arrange
		when(repository.existsById(entity.getId())).thenReturn(true);

		// Act
		service.delete(entity.getId());

		// Assert
		verify(repository, times(1)).deleteById(entity.getId());
	}

	@Test
	void delete_nonExistingId_throwsNotFoundException() {
		// Arrange
		when(repository.existsById(entity.getId())).thenReturn(false);

		// Act & Assert
		assertThrows(NotFoundException.class, () -> service.delete(entity.getId()));
		verify(repository, times(1)).existsById(entity.getId());
	}

	@Test
	void delete_nullId_throwsValueIsMissingException() {
		// Arrange
		// Act & Assert
		assertThrows(ValueIsMissingException.class, () -> service.delete(null));
	}

	@Test
	void delete_withAssociatedChampionships_entityDeleted() {
		// Arrange
		when(repository.existsById(entity.getId())).thenReturn(true);

		// Act
		service.delete(entity.getId());

		// Assert
		verify(repository, times(1)).deleteById(entity.getId());
	}

}
