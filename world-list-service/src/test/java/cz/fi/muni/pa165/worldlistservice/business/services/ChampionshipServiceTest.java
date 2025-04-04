package cz.fi.muni.pa165.worldlistservice.business.services;

import cz.fi.muni.pa165.enums.ChampionshipRegionType;
import cz.fi.muni.pa165.worldlistservice.api.exception.NotFoundException;
import cz.fi.muni.pa165.worldlistservice.api.exception.ValueIsMissingException;
import cz.fi.muni.pa165.worldlistservice.persistence.entities.ChampionshipEntity;
import cz.fi.muni.pa165.worldlistservice.persistence.entities.ChampionshipRegionEntity;
import cz.fi.muni.pa165.worldlistservice.persistence.repositories.ChampionshipRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChampionshipServiceTest {

	private final ChampionshipEntity championship = ChampionshipEntity.builder()
		.id(UUID.randomUUID())
		.name("World Cup")
		.championshipRegion(new ChampionshipRegionEntity(UUID.randomUUID(), "Europe", ChampionshipRegionType.REGIONAL,
				new HashSet<>()))
		.build();

	@Mock
	private ChampionshipRepository championshipRepository;

	@InjectMocks
	private ChampionshipServiceImpl championshipService;

	@Test
	void createChampionship_entityIsValid_entitySavedAndReturned() {
		// Arrange
		when(championshipRepository.save(championship)).thenReturn(championship);

		// Act
		ChampionshipEntity result = championshipService.create(championship);

		// Assert
		assertNotNull(result);
		assertEquals("World Cup", result.getName());
		verify(championshipRepository).save(championship);
	}

	@Test
	void createChampionship_entityIsNull_throwsValueIsMissingException() {
		// Act & Assert
		assertThrows(ValueIsMissingException.class, () -> championshipService.create(null));
	}

	@Test
	void findById_championshipExists_returnsEntity() {
		// Arrange
		when(championshipRepository.findById(championship.getId())).thenReturn(Optional.of(championship));

		// Act
		Optional<ChampionshipEntity> result = championshipService.findById(championship.getId());

		// Assert
		assertTrue(result.isPresent());
		assertEquals("World Cup", result.get().getName());
	}

	@Test
	void findById_idIsNull_throwsValueIsMissingException() {
		// Act & Assert
		assertThrows(ValueIsMissingException.class, () -> championshipService.findById(null));
	}

	@Test
	void findById_championshipDoesNotExist_returnsEmptyOptional() {
		// Arrange
		when(championshipRepository.findById(championship.getId())).thenReturn(Optional.empty());

		// Act
		Optional<ChampionshipEntity> result = championshipService.findById(championship.getId());

		// Assert
		assertFalse(result.isPresent());
	}

	@Test
	void findAll_entitiesExist_returnsPageOfEntities() {
		// Arrange
		Pageable pageable = PageRequest.of(0, 10);
		Page<ChampionshipEntity> page = new PageImpl<>(List.of(championship));
		when(championshipRepository.findAll(pageable)).thenReturn(page);

		// Act
		Page<ChampionshipEntity> result = championshipService.findAll(pageable);

		// Assert
		assertEquals(1, result.getTotalElements());
	}

	@Test
	void findAll_noEntitiesExist_returnsEmptyPage() {
		// Arrange
		Pageable pageable = PageRequest.of(0, 10);
		Page<ChampionshipEntity> page = new PageImpl<>(Collections.emptyList());
		when(championshipRepository.findAll(pageable)).thenReturn(page);

		// Act
		Page<ChampionshipEntity> result = championshipService.findAll(pageable);

		// Assert
		assertEquals(0, result.getTotalElements());
	}

	@Test
	void updateChampionship_championshipExists_updatesAndReturnsEntity() {
		// Arrange
		when(championshipRepository.existsById(championship.getId())).thenReturn(true);
		when(championshipRepository.save(championship)).thenReturn(championship);

		// Act
		ChampionshipEntity result = championshipService.update(championship);

		// Assert
		assertNotNull(result);
		assertEquals("World Cup", result.getName());
	}

	@Test
	void updateChampionship_championshipDoesNotExist_throwsNotFoundException() {
		// Arrange
		when(championshipRepository.existsById(championship.getId())).thenReturn(false);

		// Act & Assert
		assertThrows(NotFoundException.class, () -> championshipService.update(championship));
	}

	@Test
	void deleteChampionship_championshipExists_deletesEntity() {
		// Arrange
		when(championshipRepository.existsById(championship.getId())).thenReturn(true);
		doNothing().when(championshipRepository).deleteById(championship.getId());

		// Act & Assert
		assertDoesNotThrow(() -> championshipService.delete(championship.getId()));
		verify(championshipRepository).deleteById(championship.getId());
	}

	@Test
	void deleteChampionship_championshipDoesNotExist_throwsNotFoundException() {
		// Arrange
		when(championshipRepository.existsById(championship.getId())).thenReturn(false);

		// Act & Assert
		assertThrows(NotFoundException.class, () -> championshipService.delete(championship.getId()));
	}

}
