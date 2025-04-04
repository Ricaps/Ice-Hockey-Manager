package cz.fi.muni.pa165.worldlistservice.business.services;

import cz.fi.muni.pa165.worldlistservice.api.exception.NotFoundException;
import cz.fi.muni.pa165.worldlistservice.api.exception.ValueIsMissingException;
import cz.fi.muni.pa165.worldlistservice.persistence.entities.PlayerEntity;
import cz.fi.muni.pa165.worldlistservice.persistence.repositories.PlayerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerServiceTest {

	private final PlayerEntity player = PlayerEntity.builder()
		.id(UUID.randomUUID())
		.firstName("John")
		.lastName("Doe")
		.overallRating(85)
		.build();

	@Mock
	private PlayerRepository playerRepository;

	@InjectMocks
	private PlayerServiceImpl playerService;

	@Test
	void createPlayer_validPlayer_playerCreated() {
		// Arrange
		when(playerRepository.save(player)).thenReturn(player);

		// Act
		PlayerEntity result = playerService.create(player);

		// Assert
		assertNotNull(result);
		assertEquals("John", result.getFirstName());
		verify(playerRepository).save(player);
	}

	@Test
	void createPlayer_nullPlayer_throwsValueIsMissingException() {
		// Act & Assert
		assertThrows(ValueIsMissingException.class, () -> playerService.create(null));
	}

	@Test
	void findById_validId_playerReturned() {
		// Arrange
		when(playerRepository.findById(player.getId())).thenReturn(Optional.of(player));

		// Act
		Optional<PlayerEntity> result = playerService.findById(player.getId());

		// Assert
		assertTrue(result.isPresent());
		assertEquals("John", result.get().getFirstName());
	}

	@Test
	void findById_nullId_throwsValueIsMissingException() {
		// Act & Assert
		assertThrows(ValueIsMissingException.class, () -> playerService.findById(null));
	}

	@Test
	void findById_nonExistentId_noPlayerFound() {
		// Arrange
		when(playerRepository.findById(player.getId())).thenReturn(Optional.empty());

		// Act
		Optional<PlayerEntity> result = playerService.findById(player.getId());

		// Assert
		assertFalse(result.isPresent());
	}

	@Test
	void findAll_playersExist_playersReturned() {
		// Arrange
		Pageable pageable = PageRequest.of(0, 10);
		Page<PlayerEntity> page = new PageImpl<>(List.of(player));
		when(playerRepository.findAll(pageable)).thenReturn(page);

		// Act
		Page<PlayerEntity> result = playerService.findAll(pageable);

		// Assert
		assertEquals(1, result.getTotalElements());
	}

	@Test
	void findAll_noPlayers_emptyPageReturned() {
		// Arrange
		Pageable pageable = PageRequest.of(0, 10);
		Page<PlayerEntity> page = new PageImpl<>(Collections.emptyList());
		when(playerRepository.findAll(pageable)).thenReturn(page);

		// Act
		Page<PlayerEntity> result = playerService.findAll(pageable);

		// Assert
		assertEquals(0, result.getTotalElements());
	}

	@Test
	void updatePlayer_existingPlayer_playerUpdated() {
		// Arrange
		when(playerRepository.existsById(player.getId())).thenReturn(true);
		when(playerRepository.save(player)).thenReturn(player);

		// Act
		PlayerEntity result = playerService.update(player);

		// Assert
		assertNotNull(result);
		assertEquals("John", result.getFirstName());
	}

	@Test
	void updatePlayer_nonExistentPlayer_throwsNotFoundException() {
		// Arrange
		when(playerRepository.existsById(player.getId())).thenReturn(false);

		// Act & Assert
		assertThrows(NotFoundException.class, () -> playerService.update(player));
	}

	@Test
	void updatePlayer_nullPlayer_throwsValueIsMissingException() {
		// Act & Assert
		assertThrows(ValueIsMissingException.class, () -> playerService.update(null));
	}

	@Test
	void deletePlayer_existingPlayer_playerDeleted() {
		// Arrange
		when(playerRepository.existsById(player.getId())).thenReturn(true);
		doNothing().when(playerRepository).deleteById(player.getId());

		// Act & Assert
		assertDoesNotThrow(() -> playerService.delete(player.getId()));
		verify(playerRepository).deleteById(player.getId());
	}

	@Test
	void deletePlayer_nonExistentPlayer_throwsNotFoundException() {
		// Arrange
		when(playerRepository.existsById(player.getId())).thenReturn(false);

		// Act & Assert
		assertThrows(NotFoundException.class, () -> playerService.delete(player.getId()));
	}

	@Test
	void deletePlayer_nullId_throwsValueIsMissingException() {
		// Act & Assert
		assertThrows(ValueIsMissingException.class, () -> playerService.delete(null));
	}

}
