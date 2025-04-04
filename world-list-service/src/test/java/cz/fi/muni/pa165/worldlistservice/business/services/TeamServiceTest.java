package cz.fi.muni.pa165.worldlistservice.business.services;

import cz.fi.muni.pa165.worldlistservice.api.exception.NotFoundException;
import cz.fi.muni.pa165.worldlistservice.api.exception.ValueIsMissingException;
import cz.fi.muni.pa165.worldlistservice.persistence.entities.TeamEntity;
import cz.fi.muni.pa165.worldlistservice.persistence.repositories.TeamRepository;
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
class TeamServiceTest {

	private final TeamEntity team = TeamEntity.builder()
		.id(UUID.randomUUID())
		.name("Dream Team")
		.teamPlayers(new HashSet<>())
		.build();

	@Mock
	private TeamRepository teamRepository;

	@InjectMocks
	private TeamServiceImpl teamService;

	@Test
	void create_teamIsProvided_teamIsCreated() {
		// Arrange
		when(teamRepository.save(team)).thenReturn(team);

		// Act
		TeamEntity result = teamService.create(team);

		// Assert
		assertNotNull(result);
		assertEquals("Dream Team", result.getName());
		verify(teamRepository).save(team);
	}

	@Test
	void create_teamIsNull_exceptionIsThrown() {
		// Act & Assert
		assertThrows(ValueIsMissingException.class, () -> teamService.create(null));
	}

	@Test
	void findById_teamExists_teamIsReturned() {
		// Arrange
		when(teamRepository.findById(team.getId())).thenReturn(Optional.of(team));

		// Act
		Optional<TeamEntity> result = teamService.findById(team.getId());

		// Assert
		assertTrue(result.isPresent());
		assertEquals("Dream Team", result.get().getName());
	}

	@Test
	void findById_teamIdIsNull_exceptionIsThrown() {
		// Act & Assert
		assertThrows(ValueIsMissingException.class, () -> teamService.findById(null));
	}

	@Test
	void findById_teamDoesNotExist_emptyOptionalIsReturned() {
		// Arrange
		when(teamRepository.findById(team.getId())).thenReturn(Optional.empty());

		// Act
		Optional<TeamEntity> result = teamService.findById(team.getId());

		// Assert
		assertFalse(result.isPresent());
	}

	@Test
	void findAll_teamsExist_pageOfTeamsIsReturned() {
		// Arrange
		Pageable pageable = PageRequest.of(0, 10);
		Page<TeamEntity> page = new PageImpl<>(List.of(team));
		when(teamRepository.findAll(pageable)).thenReturn(page);

		// Act
		Page<TeamEntity> result = teamService.findAll(pageable);

		// Assert
		assertEquals(1, result.getTotalElements());
	}

	@Test
	void findAll_noTeamsExist_emptyPageIsReturned() {
		// Arrange
		Pageable pageable = PageRequest.of(0, 10);
		Page<TeamEntity> page = new PageImpl<>(Collections.emptyList());
		when(teamRepository.findAll(pageable)).thenReturn(page);

		// Act
		Page<TeamEntity> result = teamService.findAll(pageable);

		// Assert
		assertEquals(0, result.getTotalElements());
	}

	@Test
	void update_teamExists_teamIsUpdated() {
		// Arrange
		when(teamRepository.existsById(team.getId())).thenReturn(true);
		when(teamRepository.save(team)).thenReturn(team);

		// Act
		TeamEntity result = teamService.update(team);

		// Assert
		assertNotNull(result);
		assertEquals("Dream Team", result.getName());
	}

	@Test
	void update_teamDoesNotExist_exceptionIsThrown() {
		// Arrange
		when(teamRepository.existsById(team.getId())).thenReturn(false);

		// Act & Assert
		assertThrows(NotFoundException.class, () -> teamService.update(team));
	}

	@Test
	void update_teamIsNull_exceptionIsThrown() {
		// Act & Assert
		assertThrows(ValueIsMissingException.class, () -> teamService.update(null));
	}

	@Test
	void delete_teamExists_teamIsDeleted() {
		// Arrange
		when(teamRepository.existsById(team.getId())).thenReturn(true);
		doNothing().when(teamRepository).deleteById(team.getId());

		// Act & Assert
		assertDoesNotThrow(() -> teamService.delete(team.getId()));
		verify(teamRepository).deleteById(team.getId());
	}

	@Test
	void delete_teamDoesNotExist_exceptionIsThrown() {
		// Arrange
		when(teamRepository.existsById(team.getId())).thenReturn(false);

		// Act & Assert
		assertThrows(NotFoundException.class, () -> teamService.delete(team.getId()));
	}

	@Test
	void delete_teamIdIsNull_exceptionIsThrown() {
		// Act & Assert
		assertThrows(ValueIsMissingException.class, () -> teamService.delete(null));
	}

}
