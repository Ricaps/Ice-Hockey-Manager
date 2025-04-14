package cz.fi.muni.pa165.worldlistservice.unit.business.services;

import cz.fi.muni.pa165.enums.PlayerCharacteristicType;
import cz.fi.muni.pa165.worldlistservice.api.exception.NotFoundException;
import cz.fi.muni.pa165.worldlistservice.api.exception.ValueIsMissingException;
import cz.fi.muni.pa165.worldlistservice.business.services.PlayerCharacteristicServiceImpl;
import cz.fi.muni.pa165.worldlistservice.persistence.entities.PlayerCharacteristicEntity;
import cz.fi.muni.pa165.worldlistservice.persistence.repositories.PlayerCharacteristicRepository;
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
class PlayerCharacteristicServiceTest {

	private final PlayerCharacteristicEntity characteristic = PlayerCharacteristicEntity.builder()
		.id(UUID.randomUUID())
		.type(PlayerCharacteristicType.SPEED)
		.value(90)
		.build();

	@Mock
	private PlayerCharacteristicRepository playerCharacteristicRepository;

	@InjectMocks
	private PlayerCharacteristicServiceImpl playerCharacteristicService;

	@Test
	void createPlayerCharacteristic_validCharacteristic_characteristicCreated() {
		// Arrange
		when(playerCharacteristicRepository.save(characteristic)).thenReturn(characteristic);

		// Act
		PlayerCharacteristicEntity result = playerCharacteristicService.create(characteristic);

		// Assert
		assertNotNull(result);
		assertEquals(PlayerCharacteristicType.SPEED, result.getType());
		verify(playerCharacteristicRepository).save(characteristic);
	}

	@Test
	void createPlayerCharacteristic_nullValue_valueIsMissingExceptionThrown() {
		// Act & Assert
		assertThrows(ValueIsMissingException.class, () -> playerCharacteristicService.create(null));
	}

	@Test
	void findById_existingId_characteristicFound() {
		// Arrange
		when(playerCharacteristicRepository.findById(characteristic.getId())).thenReturn(Optional.of(characteristic));

		// Act
		Optional<PlayerCharacteristicEntity> result = playerCharacteristicService.findById(characteristic.getId());

		// Assert
		assertTrue(result.isPresent());
		assertEquals(PlayerCharacteristicType.SPEED, result.get().getType());
	}

	@Test
	void findById_nullId_valueIsMissingExceptionThrown() {
		// Act & Assert
		assertThrows(ValueIsMissingException.class, () -> playerCharacteristicService.findById(null));
	}

	@Test
	void findById_nonExistingId_characteristicNotFound() {
		// Arrange
		when(playerCharacteristicRepository.findById(characteristic.getId())).thenReturn(Optional.empty());

		// Act
		Optional<PlayerCharacteristicEntity> result = playerCharacteristicService.findById(characteristic.getId());

		// Assert
		assertFalse(result.isPresent());
	}

	@Test
	void findAll_withData_characteristicsReturned() {
		// Arrange
		Pageable pageable = PageRequest.of(0, 10);
		Page<PlayerCharacteristicEntity> page = new PageImpl<>(List.of(characteristic));
		when(playerCharacteristicRepository.findAll(pageable)).thenReturn(page);

		// Act
		Page<PlayerCharacteristicEntity> result = playerCharacteristicService.findAll(pageable);

		// Assert
		assertEquals(1, result.getTotalElements());
	}

	@Test
	void findAll_noData_noCharacteristicsReturned() {
		// Arrange
		Pageable pageable = PageRequest.of(0, 10);
		Page<PlayerCharacteristicEntity> page = new PageImpl<>(Collections.emptyList());
		when(playerCharacteristicRepository.findAll(pageable)).thenReturn(page);

		// Act
		Page<PlayerCharacteristicEntity> result = playerCharacteristicService.findAll(pageable);

		// Assert
		assertEquals(0, result.getTotalElements());
	}

	@Test
	void updatePlayerCharacteristic_existingCharacteristic_characteristicUpdated() {
		// Arrange
		when(playerCharacteristicRepository.existsById(characteristic.getId())).thenReturn(true);
		when(playerCharacteristicRepository.save(characteristic)).thenReturn(characteristic);

		// Act
		PlayerCharacteristicEntity result = playerCharacteristicService.update(characteristic);

		// Assert
		assertNotNull(result);
		assertEquals(PlayerCharacteristicType.SPEED, result.getType());
	}

	@Test
	void updatePlayerCharacteristic_nonExistingCharacteristic_notFoundExceptionThrown() {
		// Arrange
		when(playerCharacteristicRepository.existsById(characteristic.getId())).thenReturn(false);

		// Act & Assert
		assertThrows(NotFoundException.class, () -> playerCharacteristicService.update(characteristic));
	}

	@Test
	void updatePlayerCharacteristic_nullValue_valueIsMissingExceptionThrown() {
		// Act & Assert
		assertThrows(ValueIsMissingException.class, () -> playerCharacteristicService.update(null));
	}

	@Test
	void deletePlayerCharacteristic_existingId_characteristicDeleted() {
		// Arrange
		when(playerCharacteristicRepository.existsById(characteristic.getId())).thenReturn(true);
		doNothing().when(playerCharacteristicRepository).deleteById(characteristic.getId());

		// Act & Assert
		assertDoesNotThrow(() -> playerCharacteristicService.delete(characteristic.getId()));
		verify(playerCharacteristicRepository).deleteById(characteristic.getId());
	}

	@Test
	void deletePlayerCharacteristic_nonExistingId_notFoundExceptionThrown() {
		// Arrange
		when(playerCharacteristicRepository.existsById(characteristic.getId())).thenReturn(false);

		// Act & Assert
		assertThrows(NotFoundException.class, () -> playerCharacteristicService.delete(characteristic.getId()));
	}

	@Test
	void deletePlayerCharacteristic_nullId_valueIsMissingExceptionThrown() {
		// Act & Assert
		assertThrows(ValueIsMissingException.class, () -> playerCharacteristicService.delete(null));
	}

}
