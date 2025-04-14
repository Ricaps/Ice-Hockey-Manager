package cz.fi.muni.pa165.worldlistservice.unit.api;

import cz.fi.muni.pa165.dto.worldlistservice.championshipregion.ChampionshipRegionDto;
import cz.fi.muni.pa165.dto.worldlistservice.championshipregion.create.ChampionshipRegionCreateDto;
import cz.fi.muni.pa165.worldlistservice.api.controllers.ChampionshipRegionControllerImpl;
import cz.fi.muni.pa165.worldlistservice.api.exception.NotFoundException;
import cz.fi.muni.pa165.worldlistservice.api.exception.ResourceInUseException;
import cz.fi.muni.pa165.worldlistservice.business.facades.interfaces.ChampionshipRegionFacade;
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
public class ChampionshipRegionControllerTest {

	private final UUID testRegionId = UUID.randomUUID();

	private final ChampionshipRegionDto championshipRegionDto = ChampionshipRegionDto.builder()
		.id(testRegionId)
		.name("Test Region")
		.build();

	private final ChampionshipRegionCreateDto championshipRegionCreateDto = ChampionshipRegionCreateDto.builder()
		.name("Test Region")
		.build();

	@Mock
	private ChampionshipRegionFacade championshipRegionFacade;

	@InjectMocks
	private ChampionshipRegionControllerImpl championshipRegionController;

	@Test
	public void getChampionshipRegionById_regionExists_returnsChampionshipRegion() {
		// Arrange
		when(championshipRegionFacade.findById(testRegionId)).thenReturn(Optional.of(championshipRegionDto));

		// Act
		ResponseEntity<ChampionshipRegionDto> response = championshipRegionController
			.getChampionshipRegionById(testRegionId);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(championshipRegionDto, response.getBody());
		verify(championshipRegionFacade).findById(testRegionId);
	}

	@Test
	public void getChampionshipRegionById_regionDoesNotExist_returnsNotFound() {
		// Arrange
		when(championshipRegionFacade.findById(testRegionId)).thenReturn(Optional.empty());

		// Act
		ResponseEntity<ChampionshipRegionDto> response = championshipRegionController
			.getChampionshipRegionById(testRegionId);

		// Assert
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertNull(response.getBody());
		verify(championshipRegionFacade).findById(testRegionId);
	}

	@Test
	public void getAllChampionshipRegions_validRequest_returnsPageOfChampionshipRegions() {
		// Arrange
		Pageable pageable = mock(Pageable.class);
		Page<ChampionshipRegionDto> championshipRegionPage = new PageImpl<>(List.of(championshipRegionDto));
		when(championshipRegionFacade.findAll(pageable)).thenReturn(championshipRegionPage);

		// Act
		ResponseEntity<Page<ChampionshipRegionDto>> response = championshipRegionController
			.getAllChampionshipRegions(pageable);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(championshipRegionPage, response.getBody());
		verify(championshipRegionFacade).findAll(pageable);
	}

	@Test
	public void getAllChampionshipRegions_noRegionsFound_returnsNotFound() {
		// Arrange
		Pageable pageable = mock(Pageable.class);
		Page<ChampionshipRegionDto> championshipRegionPage = new PageImpl<>(List.of());
		when(championshipRegionFacade.findAll(pageable)).thenReturn(championshipRegionPage);

		// Act
		ResponseEntity<Page<ChampionshipRegionDto>> response = championshipRegionController
			.getAllChampionshipRegions(pageable);

		// Assert
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertNull(response.getBody());
		verify(championshipRegionFacade).findAll(pageable);
	}

	@Test
	public void createChampionshipRegion_validRequest_returnsChampionshipRegion() {
		// Arrange
		when(championshipRegionFacade.create(championshipRegionCreateDto)).thenReturn(championshipRegionDto);

		// Act
		ResponseEntity<ChampionshipRegionDto> response = championshipRegionController
			.createChampionshipRegion(championshipRegionCreateDto);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(championshipRegionDto, response.getBody());
		verify(championshipRegionFacade).create(championshipRegionCreateDto);
	}

	@Test
	public void updateChampionshipRegion_validRequest_returnsChampionshipRegion() {
		// Arrange
		when(championshipRegionFacade.update(championshipRegionDto)).thenReturn(championshipRegionDto);

		// Act
		ResponseEntity<ChampionshipRegionDto> response = championshipRegionController
			.updateChampionshipRegion(championshipRegionDto);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(championshipRegionDto, response.getBody());
		verify(championshipRegionFacade).update(championshipRegionDto);
	}

	@Test
	public void updateChampionshipRegion_regionNotFound_throwsNotFoundException() {
		// Arrange
		doThrow(new NotFoundException("", UUID.randomUUID())).when(championshipRegionFacade)
			.update(championshipRegionDto);

		// Act & Assert
		assertThrows(NotFoundException.class,
				() -> championshipRegionController.updateChampionshipRegion(championshipRegionDto));
		verify(championshipRegionFacade).update(championshipRegionDto);
	}

	@Test
	public void deleteChampionshipRegion_validId_returnsOk() {
		// Act
		ResponseEntity<Void> response = championshipRegionController.deleteChampionshipRegion(testRegionId);

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode());
		verify(championshipRegionFacade).delete(testRegionId);
	}

	@Test
	public void deleteChampionshipRegion_regionNotFound_throwsNotFoundException() {
		// Arrange
		doThrow(new NotFoundException("", UUID.randomUUID())).when(championshipRegionFacade).delete(testRegionId);

		// Act & Assert
		assertThrows(NotFoundException.class,
				() -> championshipRegionController.deleteChampionshipRegion(testRegionId));
		verify(championshipRegionFacade).delete(testRegionId);
	}

	@Test
	public void deleteChampionshipRegion_regionInUse_throwsResourceInUseException() {
		// Arrange
		doThrow(new ResourceInUseException("", UUID.randomUUID())).when(championshipRegionFacade).delete(testRegionId);

		// Act & Assert
		assertThrows(ResourceInUseException.class,
				() -> championshipRegionController.deleteChampionshipRegion(testRegionId));
		verify(championshipRegionFacade).delete(testRegionId);
	}

}
