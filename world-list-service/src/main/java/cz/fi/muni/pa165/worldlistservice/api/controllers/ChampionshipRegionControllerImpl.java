package cz.fi.muni.pa165.worldlistservice.api.controllers;

import cz.fi.muni.pa165.dto.worldlistservice.championship.detail.ChampionshipDetailDto;
import cz.fi.muni.pa165.dto.worldlistservice.championshipregion.ChampionshipRegionDto;
import cz.fi.muni.pa165.dto.worldlistservice.championshipregion.create.ChampionshipRegionCreateDto;
import cz.fi.muni.pa165.service.wordlistservice.api.ChampionshipRegionController;
import cz.fi.muni.pa165.worldlistservice.business.facades.interfaces.ChampionshipRegionFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/championship-regions/")
@Tag(name = "Championship regions")
public class ChampionshipRegionControllerImpl implements ChampionshipRegionController {

	private final ChampionshipRegionFacade championshipRegionFacade;

	@Autowired
	ChampionshipRegionControllerImpl(ChampionshipRegionFacade championshipRegionFacade) {
		this.championshipRegionFacade = championshipRegionFacade;
	}

	@Operation(summary = "Get championship regions by id",
			description = "Returns a championship region by specified id")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successful Response",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = ChampionshipDetailDto.class))),
			@ApiResponse(responseCode = "404", description = "Championship region not found",
					content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))) })
	@GetMapping("{id}")
	@ResponseBody
	@Override
	public ResponseEntity<ChampionshipRegionDto> getChampionshipRegionById(@PathVariable UUID id) {
		var championshipRegionOption = championshipRegionFacade.findById(id);

		return championshipRegionOption.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}

	@Operation(summary = "Get all championship regions with pagination",
			description = "Returns a paginated list of all championship regions")
	@ApiResponses(
			value = {
					@ApiResponse(responseCode = "200", description = "Successful Response",
							content = @Content(mediaType = "application/json",
									schema = @Schema(implementation = Page.class))),
					@ApiResponse(responseCode = "404", description = "No championship regions found",
							content = @Content(mediaType = "text/plain",
									schema = @Schema(implementation = String.class))) })
	@GetMapping("")
	@Override
	public ResponseEntity<Page<ChampionshipRegionDto>> getAllChampionshipRegions(
			@Parameter(hidden = true) @ParameterObject @PageableDefault(sort = { "name" }) Pageable pageable) {
		var championshipRegionPage = championshipRegionFacade.findAll(pageable);

		return championshipRegionPage.hasContent() ? ResponseEntity.ok(championshipRegionPage)
				: ResponseEntity.notFound().build();
	}

	@Operation(summary = "Create a championship region",
			description = "Create an championship region with the provided data")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Championship region created successfully",
			content = @Content(mediaType = "application/json",
					schema = @Schema(implementation = ChampionshipRegionDto.class))), })
	@PostMapping("")
	@Override
	public ResponseEntity<ChampionshipRegionDto> createChampionshipRegion(
			@Valid @RequestBody ChampionshipRegionCreateDto championshipRegionToCreate) {
		return ResponseEntity.ok(championshipRegionFacade.create(championshipRegionToCreate));
	}

	@Operation(summary = "Update a championship region",
			description = "Updates an existing championship region with the provided data")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Championship region updated successfully",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = ChampionshipRegionDto.class))),
			@ApiResponse(responseCode = "404", description = "Championship region not found",
					content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))) })
	@PutMapping("")
	@Override
	public ResponseEntity<ChampionshipRegionDto> updateChampionshipRegion(
			@Valid @RequestBody ChampionshipRegionDto updatedChampionship) {
		return ResponseEntity.ok(championshipRegionFacade.update(updatedChampionship));
	}

	@Operation(summary = "Delete a championship region",
			description = "Deletes a championship region by the specified ID")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Championship region deleted successfully"),
			@ApiResponse(responseCode = "404", description = "Championship region not found",
					content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))),
			@ApiResponse(responseCode = "409",
					description = "Championship region has related entities which have to be deleted before deletion",
					content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))) })
	@DeleteMapping("{id}")
	@Override
	public ResponseEntity<Void> deleteChampionshipRegion(@PathVariable UUID id) {
		championshipRegionFacade.delete(id);
		return ResponseEntity.ok().build();
	}

}
