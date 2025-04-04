package cz.fi.muni.pa165.worldlistservice.api.controllers;

import cz.fi.muni.pa165.dto.worldlistservice.championship.create.ChampionshipCreateDto;
import cz.fi.muni.pa165.dto.worldlistservice.championship.detail.ChampionshipDetailDto;
import cz.fi.muni.pa165.dto.worldlistservice.championship.list.ChampionshipListDto;
import cz.fi.muni.pa165.dto.worldlistservice.championship.update.ChampionshipUpdateDto;
import cz.fi.muni.pa165.worldlistservice.api.exception.NotFoundException;
import cz.fi.muni.pa165.worldlistservice.api.exception.ResourceInUseException;
import cz.fi.muni.pa165.worldlistservice.business.facades.interfaces.ChampionshipFacade;
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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChampionshipControllerTest {

	private final UUID testChampionshipId = UUID.randomUUID();

	private final ChampionshipDetailDto championshipDetailDto = ChampionshipDetailDto.builder()
		.id(testChampionshipId)
		.name("Test Championship")
		.championshipRegion(null)
		.championshipTeams(new HashSet<>())
		.build();

	private final ChampionshipCreateDto createModel = ChampionshipCreateDto.builder()
		.name("Test Championship")
		.championshipRegionId(UUID.randomUUID())
		.championshipTeamsIds(Set.of(UUID.randomUUID()))
		.build();

	private final ChampionshipUpdateDto updateModel = ChampionshipUpdateDto.builder()
		.id(testChampionshipId)
		.name("Test Championship")
		.championshipRegionId(UUID.randomUUID())
		.championshipTeamsIds(Set.of(UUID.randomUUID()))
		.build();

	private final ChampionshipListDto listModel = ChampionshipListDto.builder()
		.id(testChampionshipId)
		.name("Test Championship")
		.build();

	@Mock
	private ChampionshipFacade championshipFacade;

	@InjectMocks
	private ChampionshipControllerImpl championshipController;

	@Test
	public void getChampionshipById_championshipExists_returnsChampionshipDetail() {
		// Arrange
		when(championshipFacade.findById(testChampionshipId)).thenReturn(Optional.of(championshipDetailDto));

		// Act
		ResponseEntity<ChampionshipDetailDto> response = championshipController.getChampionshipById(testChampionshipId);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(championshipDetailDto, response.getBody());
		verify(championshipFacade).findById(testChampionshipId);
	}

	@Test
	public void getChampionshipById_championshipDoesNotExist_returnsNotFound() {
		// Arrange
		when(championshipFacade.findById(testChampionshipId)).thenReturn(Optional.empty());

		// Act
		ResponseEntity<ChampionshipDetailDto> response = championshipController.getChampionshipById(testChampionshipId);

		// Assert
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertNull(response.getBody());
		verify(championshipFacade).findById(testChampionshipId);
	}

	@Test
	public void getAllChampionships_validRequest_returnsPageOfChampionships() {
		// Arrange
		Pageable pageable = mock(Pageable.class);
		Page<ChampionshipListDto> championshipsPage = new PageImpl<>(List.of(listModel));
		when(championshipFacade.findAll(pageable)).thenReturn(championshipsPage);

		// Act
		ResponseEntity<Page<ChampionshipListDto>> response = championshipController.getAllChampionships(pageable);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(championshipsPage, response.getBody());
		verify(championshipFacade).findAll(pageable);
	}

	@Test
	public void getAllChampionships_noChampionshipsFound_returnsNotFound() {
		// Arrange
		Pageable pageable = mock(Pageable.class);
		Page<ChampionshipListDto> championshipsPage = new PageImpl<>(List.of());
		when(championshipFacade.findAll(pageable)).thenReturn(championshipsPage);

		// Act
		ResponseEntity<Page<ChampionshipListDto>> response = championshipController.getAllChampionships(pageable);

		// Assert
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertNull(response.getBody());
		verify(championshipFacade).findAll(pageable);
	}

	@Test
	public void createChampionship_validRequest_returnsChampionshipDetail() {
		// Arrange
		when(championshipFacade.create(createModel)).thenReturn(championshipDetailDto);

		// Act
		ResponseEntity<ChampionshipDetailDto> response = championshipController.createChampionship(createModel);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(championshipDetailDto, response.getBody());
		verify(championshipFacade).create(createModel);
	}

	@Test
	public void updateChampionship_validRequest_returnsChampionshipDetail() {
		// Arrange
		when(championshipFacade.update(updateModel)).thenReturn(championshipDetailDto);

		// Act
		ResponseEntity<ChampionshipDetailDto> response = championshipController.updateChampionship(updateModel);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(championshipDetailDto, response.getBody());
		verify(championshipFacade).update(updateModel);
	}

	@Test
	public void deleteChampionship_validId_returnsOk() {
		// Act
		ResponseEntity<Void> response = championshipController.deleteChampionship(testChampionshipId);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		verify(championshipFacade).delete(testChampionshipId);
	}

	@Test
	public void deleteChampionship_notFound_throwsNotFoundException() {
		// Arrange
		doThrow(new NotFoundException("", UUID.randomUUID())).when(championshipFacade).delete(testChampionshipId);

		// Act & Assert
		assertThrows(NotFoundException.class, () -> championshipController.deleteChampionship(testChampionshipId));
		verify(championshipFacade).delete(testChampionshipId);
	}

	@Test
	public void deleteChampionship_inUse_throwsResourceInUseException() {
		// Arrange
		doThrow(new ResourceInUseException("", UUID.randomUUID())).when(championshipFacade).delete(testChampionshipId);

		// Act & Assert
		assertThrows(ResourceInUseException.class, () -> championshipController.deleteChampionship(testChampionshipId));
		verify(championshipFacade).delete(testChampionshipId);
	}

}
