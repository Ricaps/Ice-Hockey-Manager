package cz.fi.muni.pa165.service.wordlistService.api;

import cz.fi.muni.pa165.dto.worldlistservice.championship.detail.ChampionshipDetailDto;
import cz.fi.muni.pa165.dto.worldlistservice.championshipregion.ChampionshipRegionDto;
import cz.fi.muni.pa165.dto.worldlistservice.championshipregion.create.ChampionshipRegionCreateDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

public interface ChampionshipRegionController {

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
	ResponseEntity<ChampionshipRegionDto> getChampionshipRegionById(@PathVariable UUID id);

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
	ResponseEntity<Page<ChampionshipRegionDto>> getAllChampionshipRegions(
			@Parameter(hidden = true) @ParameterObject @PageableDefault(sort = { "name" }) Pageable pageable);

	@Operation(summary = "Create a championship region",
			description = "Create an championship region with the provided data")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Championship region created successfully",
			content = @Content(mediaType = "application/json",
					schema = @Schema(implementation = ChampionshipRegionDto.class))), })
	@PostMapping("")
	ResponseEntity<ChampionshipRegionDto> createChampionshipRegion(
			@Valid @RequestBody ChampionshipRegionCreateDto championshipRegionToCreate);

	@Operation(summary = "Update a championship region",
			description = "Updates an existing championship region with the provided data")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Championship region updated successfully",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = ChampionshipRegionDto.class))),
			@ApiResponse(responseCode = "404", description = "Championship region not found",
					content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))) })
	@PutMapping("")
	ResponseEntity<ChampionshipRegionDto> updateChampionshipRegion(
			@Valid @RequestBody ChampionshipRegionDto updatedChampionship);

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
	ResponseEntity<Void> deleteChampionshipRegion(@PathVariable UUID id);

}
