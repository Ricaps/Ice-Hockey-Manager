package cz.fi.muni.pa165.worldlistservice.unit.api;

import cz.fi.muni.pa165.dto.worldlistservice.player.create.PlayerCreateDto;
import cz.fi.muni.pa165.dto.worldlistservice.player.detail.PlayerDetailDto;
import cz.fi.muni.pa165.dto.worldlistservice.player.list.PlayerListDto;
import cz.fi.muni.pa165.dto.worldlistservice.player.update.PlayerUpdateDto;
import cz.fi.muni.pa165.worldlistservice.api.controllers.PlayerControllerImpl;
import cz.fi.muni.pa165.worldlistservice.api.exception.NotFoundException;
import cz.fi.muni.pa165.worldlistservice.api.exception.ResourceInUseException;
import cz.fi.muni.pa165.worldlistservice.business.facades.interfaces.PlayerFacade;
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
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PlayerControllerTest {

	private final UUID testPlayerId = UUID.randomUUID();

	private final PlayerDetailDto playerDetailDto = PlayerDetailDto.builder()
		.id(testPlayerId)
		.firstName("Player First")
		.lastName("Player Last")
		.overallRating(85)
		.build();

	private final PlayerCreateDto createModel = PlayerCreateDto.builder()
		.firstName("Player First")
		.lastName("Player Last")
		.overallRating(85)
		.teamId(UUID.randomUUID())
		.playerCharacteristicsIds(Set.of(UUID.randomUUID()))
		.build();

	private final PlayerUpdateDto updateModel = PlayerUpdateDto.builder()
		.id(testPlayerId)
		.firstName("Player First")
		.lastName("Player Last")
		.overallRating(85)
		.teamId(UUID.randomUUID())
		.playerCharacteristicsIds(Set.of(UUID.randomUUID()))
		.build();

	private final PlayerListDto playerListDto = PlayerListDto.builder()
		.id(testPlayerId)
		.firstName("Player First")
		.lastName("Player Last")
		.overallRating(85)
		.build();

	@Mock
	private PlayerFacade playerFacade;

	@InjectMocks
	private PlayerControllerImpl playerController;

	@Test
	public void getPlayerById_playerExists_returnsPlayerDetail() {
		// Arrange
		when(playerFacade.findById(testPlayerId)).thenReturn(Optional.of(playerDetailDto));

		// Act
		ResponseEntity<PlayerDetailDto> response = playerController.getPlayerById(testPlayerId);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(playerDetailDto, response.getBody());
		verify(playerFacade).findById(testPlayerId);
	}

	@Test
	public void getPlayerById_playerDoesNotExist_returnsNotFound() {
		// Arrange
		when(playerFacade.findById(testPlayerId)).thenReturn(Optional.empty());

		// Act
		ResponseEntity<PlayerDetailDto> response = playerController.getPlayerById(testPlayerId);

		// Assert
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertNull(response.getBody());
		verify(playerFacade).findById(testPlayerId);
	}

	@Test
	public void getAllPlayers_validRequest_returnsPageOfPlayers() {
		// Arrange
		Pageable pageable = mock(Pageable.class);
		Page<PlayerListDto> playersPage = new PageImpl<>(List.of(playerListDto));
		when(playerFacade.findAll(pageable)).thenReturn(playersPage);

		// Act
		ResponseEntity<Page<PlayerListDto>> response = playerController.getAllPlayers(pageable);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(playersPage, response.getBody());
		verify(playerFacade).findAll(pageable);
	}

	@Test
	public void getAllPlayers_noPlayersFound_returnsNotFound() {
		// Arrange
		Pageable pageable = mock(Pageable.class);
		Page<PlayerListDto> playersPage = new PageImpl<>(List.of());
		when(playerFacade.findAll(pageable)).thenReturn(playersPage);

		// Act
		ResponseEntity<Page<PlayerListDto>> response = playerController.getAllPlayers(pageable);

		// Assert
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertNull(response.getBody());
		verify(playerFacade).findAll(pageable);
	}

	@Test
	public void createPlayer_validRequest_returnsPlayerDetail() {
		// Arrange
		when(playerFacade.create(createModel)).thenReturn(playerDetailDto);

		// Act
		ResponseEntity<PlayerDetailDto> response = playerController.createPlayer(createModel);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(playerDetailDto, response.getBody());
		verify(playerFacade).create(createModel);
	}

	@Test
	public void createPlayer_invalidRequest_returnsBadRequest() {
		// Arrange
		doThrow(new IllegalArgumentException("Invalid input")).when(playerFacade).create(createModel);

		// Act & Assert
		assertThrows(IllegalArgumentException.class, () -> playerController.createPlayer(createModel));
		verify(playerFacade).create(createModel);
	}

	@Test
	public void updatePlayer_validRequest_returnsPlayerDetail() {
		// Arrange
		when(playerFacade.update(updateModel)).thenReturn(playerDetailDto);

		// Act
		ResponseEntity<PlayerDetailDto> response = playerController.updatePlayer(updateModel);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(playerDetailDto, response.getBody());
		verify(playerFacade).update(updateModel);
	}

	@Test
	public void updatePlayer_notFound_throwsNotFoundException() {
		// Arrange
		doThrow(new NotFoundException("", UUID.randomUUID())).when(playerFacade).update(updateModel);

		// Act & Assert
		assertThrows(NotFoundException.class, () -> playerController.updatePlayer(updateModel));
		verify(playerFacade).update(updateModel);
	}

	@Test
	public void deletePlayer_validId_returnsOk() {
		// Act
		ResponseEntity<Void> response = playerController.deletePlayer(testPlayerId);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		verify(playerFacade).delete(testPlayerId);
	}

	@Test
	public void deletePlayer_playerNotFound_throwsNotFoundException() {
		// Arrange
		doThrow(new NotFoundException("", UUID.randomUUID())).when(playerFacade).delete(testPlayerId);

		// Act & Assert
		assertThrows(NotFoundException.class, () -> playerController.deletePlayer(testPlayerId));
		verify(playerFacade).delete(testPlayerId);
	}

	@Test
	public void deletePlayer_playerInUse_throwsResourceInUseException() {
		// Arrange
		doThrow(new ResourceInUseException("", UUID.randomUUID())).when(playerFacade).delete(testPlayerId);

		// Act & Assert
		assertThrows(ResourceInUseException.class, () -> playerController.deletePlayer(testPlayerId));
		verify(playerFacade).delete(testPlayerId);
	}

}