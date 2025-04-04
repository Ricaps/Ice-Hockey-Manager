package cz.fi.muni.pa165.worldlistservice.api.controllers;

import cz.fi.muni.pa165.dto.worldlistservice.championship.create.ChampionshipCreateDto;
import cz.fi.muni.pa165.dto.worldlistservice.championship.detail.ChampionshipDetailDto;
import cz.fi.muni.pa165.dto.worldlistservice.championship.list.ChampionshipListDto;
import cz.fi.muni.pa165.dto.worldlistservice.championship.update.ChampionshipUpdateDto;
import cz.fi.muni.pa165.service.wordlistService.api.ChampionshipController;
import cz.fi.muni.pa165.worldlistservice.business.facades.interfaces.ChampionshipFacade;
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
@RequestMapping("/v1/championships/")
@Tag(name = "Championships")
public class ChampionshipControllerImpl implements ChampionshipController {

	private final ChampionshipFacade championshipFacade;

	@Autowired
	ChampionshipControllerImpl(ChampionshipFacade championshipFacade) {
		this.championshipFacade = championshipFacade;
	}

	@Operation(summary = "Get championship by id", description = "Returns a championship by specified id")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successful Response",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = ChampionshipDetailDto.class))),
			@ApiResponse(responseCode = "404", description = "Championship not found",
					content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))), })
	@GetMapping("{id}")
	@ResponseBody
	@Override
	public ResponseEntity<ChampionshipDetailDto> getChampionshipById(@PathVariable UUID id) {
		var championshipOption = championshipFacade.findById(id);

		return championshipOption.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}

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
	@Override
	public ResponseEntity<Page<ChampionshipListDto>> getAllChampionships(
			@Parameter(hidden = true) @ParameterObject @PageableDefault(sort = { "name" }) Pageable pageable) {
		Page<ChampionshipListDto> championshipPage = championshipFacade.findAll(pageable);

		return championshipPage.hasContent() ? ResponseEntity.ok(championshipPage) : ResponseEntity.notFound().build();
	}

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
	@Override
	public ResponseEntity<ChampionshipDetailDto> createChampionship(
			@Valid @RequestBody ChampionshipCreateDto championshipToCreate) {
		return ResponseEntity.ok(championshipFacade.create(championshipToCreate));
	}

	@Operation(summary = "Update a championships",
			description = "Updates an existing championships with the provided data")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Championship updated successfully",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = ChampionshipUpdateDto.class))),
			@ApiResponse(responseCode = "404", description = "Championship or related entities have not been found",
					content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))), })
	@PutMapping("")
	@Override
	public ResponseEntity<ChampionshipDetailDto> updateChampionship(
			@Valid @RequestBody ChampionshipUpdateDto updatedChampionship) {
		return ResponseEntity.ok(championshipFacade.update(updatedChampionship));
	}

	@Operation(summary = "Delete a championship", description = "Deletes a championship by the specified ID")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Championship deleted successfully"),
			@ApiResponse(responseCode = "404", description = "Championship not found",
					content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))),
			@ApiResponse(responseCode = "409",
					description = "Championship has related entities which have to be deleted before deletion",
					content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))) })
	@DeleteMapping("{id}")
	@Override
	public ResponseEntity<Void> deleteChampionship(@PathVariable UUID id) {
		championshipFacade.delete(id);
		return ResponseEntity.ok().build();
	}

}
