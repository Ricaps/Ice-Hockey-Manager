package cz.fi.muni.pa165.worldlistservice.api.controllers;

import cz.fi.muni.pa165.dto.worldlistservice.playercharacteristic.PlayerCharacteristicDto;
import cz.fi.muni.pa165.dto.worldlistservice.playercharacteristic.create.PlayerCharacteristicCreateDto;
import cz.fi.muni.pa165.enums.PlayerCharacteristicType;
import cz.fi.muni.pa165.worldlistservice.api.exception.NotFoundException;
import cz.fi.muni.pa165.worldlistservice.business.facades.interfaces.PlayerCharacteristicFacade;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PlayerCharacteristicControllerTest {

	private final UUID testCharacteristicId = UUID.randomUUID();

	private final PlayerCharacteristicDto playerCharacteristicDto = PlayerCharacteristicDto.builder()
		.id(testCharacteristicId)
		.type(PlayerCharacteristicType.SPEED)
		.build();

	private final PlayerCharacteristicCreateDto playerCharacteristicCreateDto = PlayerCharacteristicCreateDto.builder()
		.type(PlayerCharacteristicType.SPEED)
		.build();

	@Mock
	private PlayerCharacteristicFacade playerCharacteristicFacade;

	@InjectMocks
	private PlayerCharacteristicControllerImpl playerCharacteristicController;

	@Test
	public void getPlayerCharacteristicById_characteristicExists_returnsPlayerCharacteristic() {
		// Arrange
		when(playerCharacteristicFacade.findById(testCharacteristicId))
			.thenReturn(Optional.of(playerCharacteristicDto));

		// Act
		ResponseEntity<PlayerCharacteristicDto> response = playerCharacteristicController
			.getPlayerCharacteristicById(testCharacteristicId);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(playerCharacteristicDto, response.getBody());
		verify(playerCharacteristicFacade).findById(testCharacteristicId);
	}

	@Test
	public void getPlayerCharacteristicById_characteristicDoesNotExist_returnsNotFound() {
		// Arrange
		when(playerCharacteristicFacade.findById(testCharacteristicId)).thenReturn(Optional.empty());

		// Act
		ResponseEntity<PlayerCharacteristicDto> response = playerCharacteristicController
			.getPlayerCharacteristicById(testCharacteristicId);

		// Assert
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertNull(response.getBody());
		verify(playerCharacteristicFacade).findById(testCharacteristicId);
	}

	@Test
	public void getAllPlayerCharacteristics_validRequest_returnsPageOfPlayerCharacteristics() {
		// Arrange
		Pageable pageable = mock(Pageable.class);
		Page<PlayerCharacteristicDto> characteristicsPage = new PageImpl<>(List.of(playerCharacteristicDto));
		when(playerCharacteristicFacade.findAll(pageable)).thenReturn(characteristicsPage);

		// Act
		ResponseEntity<Page<PlayerCharacteristicDto>> response = playerCharacteristicController
			.getAllPlayerCharacteristics(pageable);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(characteristicsPage, response.getBody());
		verify(playerCharacteristicFacade).findAll(pageable);
	}

	@Test
	public void getAllPlayerCharacteristics_noCharacteristicsFound_returnsNotFound() {
		// Arrange
		Pageable pageable = mock(Pageable.class);
		Page<PlayerCharacteristicDto> characteristicsPage = new PageImpl<>(List.of());
		when(playerCharacteristicFacade.findAll(pageable)).thenReturn(characteristicsPage);

		// Act
		ResponseEntity<Page<PlayerCharacteristicDto>> response = playerCharacteristicController
			.getAllPlayerCharacteristics(pageable);

		// Assert
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertNull(response.getBody());
		verify(playerCharacteristicFacade).findAll(pageable);
	}

	@Test
	public void createPlayerCharacteristic_validRequest_returnsPlayerCharacteristic() {
		// Arrange
		when(playerCharacteristicFacade.create(playerCharacteristicCreateDto)).thenReturn(playerCharacteristicDto);

		// Act
		ResponseEntity<PlayerCharacteristicDto> response = playerCharacteristicController
			.createPlayerCharacteristic(playerCharacteristicCreateDto);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(playerCharacteristicDto, response.getBody());
		verify(playerCharacteristicFacade).create(playerCharacteristicCreateDto);
	}

	@Test
	public void updatePlayerCharacteristic_validRequest_returnsPlayerCharacteristic() {
		// Arrange
		when(playerCharacteristicFacade.update(playerCharacteristicDto)).thenReturn(playerCharacteristicDto);

		// Act
		ResponseEntity<PlayerCharacteristicDto> response = playerCharacteristicController
			.updatePlayerCharacteristic(playerCharacteristicDto);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(playerCharacteristicDto, response.getBody());
		verify(playerCharacteristicFacade).update(playerCharacteristicDto);
	}

	@Test
	public void deletePlayerCharacteristic_validId_returnsOk() {
		// Act
		ResponseEntity<Void> response = playerCharacteristicController.deletePlayerCharacteristic(testCharacteristicId);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		verify(playerCharacteristicFacade).delete(testCharacteristicId);
	}

	@Test
	public void deletePlayerCharacteristic_characteristicNotFound_throwsNotFoundException() {
		// Arrange
		doThrow(new NotFoundException("", UUID.randomUUID())).when(playerCharacteristicFacade)
			.delete(testCharacteristicId);

		// Act & Assert
		assertThrows(NotFoundException.class,
				() -> playerCharacteristicController.deletePlayerCharacteristic(testCharacteristicId));
		verify(playerCharacteristicFacade).delete(testCharacteristicId);
	}

}
