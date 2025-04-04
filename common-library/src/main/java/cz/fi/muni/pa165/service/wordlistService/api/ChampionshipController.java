package cz.fi.muni.pa165.service.wordlistService.api;

import cz.fi.muni.pa165.dto.worldlistservice.championship.create.ChampionshipCreateDto;
import cz.fi.muni.pa165.dto.worldlistservice.championship.detail.ChampionshipDetailDto;
import cz.fi.muni.pa165.dto.worldlistservice.championship.list.ChampionshipListDto;
import cz.fi.muni.pa165.dto.worldlistservice.championship.update.ChampionshipUpdateDto;
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

public interface ChampionshipController {

	@Operation(summary = "Get championship by id", description = "Returns a championship by specified id")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successful Response",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = ChampionshipDetailDto.class))),
			@ApiResponse(responseCode = "404", description = "Championship not found",
					content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))), })
	@GetMapping("{id}")
	@ResponseBody
	ResponseEntity<ChampionshipDetailDto> getChampionshipById(@PathVariable UUID id);

	@Operation(summary = "Get all championships with pagination",
			description = "Returns a paginated list of all championships")
	@ApiResponses(
			value = {
					@ApiResponse(responseCode = "200", description = "Successful Response",
							content = @Content(mediaType = "application/json",
									schema = @Schema(implementation = Page.class))),
					@ApiResponse(responseCode = "404", description = "No championships found",
							content = @Content(mediaType = "text/plain",
									schema = @Schema(implementation = String.class))) })
	@GetMapping("")
	ResponseEntity<Page<ChampionshipListDto>> getAllChampionships(
			@Parameter(hidden = true) @ParameterObject @PageableDefault(sort = { "name" }) Pageable pageable);

	@Operation(summary = "Create a championships", description = "Creates an championships with the provided data")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Championship created successfully",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = ChampionshipDetailDto.class))),
			@ApiResponse(responseCode = "404", description = "Related entities have not been found",
					content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))),
			@ApiResponse(responseCode = "409", description = "Championship with specified id already exists",
					content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))), })
	@PostMapping("")
	ResponseEntity<ChampionshipDetailDto> createChampionship(
			@Valid @RequestBody ChampionshipCreateDto championshipToCreate);

	@Operation(summary = "Update a championships",
			description = "Updates an existing championships with the provided data")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Championship updated successfully",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = ChampionshipUpdateDto.class))),
			@ApiResponse(responseCode = "404", description = "Championship or related entities have not been found",
					content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))), })
	@PutMapping("")
	ResponseEntity<ChampionshipDetailDto> updateChampionship(
			@Valid @RequestBody ChampionshipUpdateDto updatedChampionship);

	@Operation(summary = "Delete a championship", description = "Deletes a championship by the specified ID")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Championship deleted successfully"),
			@ApiResponse(responseCode = "404", description = "Championship not found",
					content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))),
			@ApiResponse(responseCode = "409",
					description = "Championship has related entities which have to be deleted before deletion",
					content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))) })
	@DeleteMapping("{id}")
	ResponseEntity<Void> deleteChampionship(@PathVariable UUID id);

}
