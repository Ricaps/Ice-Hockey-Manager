package cz.fi.muni.pa165.service.wordlistservice.api;

import cz.fi.muni.pa165.dto.worldlistservice.playercharacteristic.PlayerCharacteristicDto;
import cz.fi.muni.pa165.dto.worldlistservice.playercharacteristic.create.PlayerCharacteristicCreateDto;
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

public interface PlayerCharacteristicController {

	@Operation(summary = "Get player characteristic by id",
			description = "Returns a player characteristic by specified id")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successful Response",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = PlayerCharacteristicDto.class))),
			@ApiResponse(responseCode = "404", description = "Player characteristic not found",
					content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))) })
	@GetMapping("{id}")
	@ResponseBody
	ResponseEntity<PlayerCharacteristicDto> getPlayerCharacteristicById(@PathVariable UUID id);

	@Operation(summary = "Get all player characteristics with pagination",
			description = "Returns a paginated list of all player characteristics")
	@ApiResponses(
			value = {
					@ApiResponse(responseCode = "200", description = "Successful Response",
							content = @Content(mediaType = "application/json",
									schema = @Schema(implementation = Page.class))),
					@ApiResponse(responseCode = "404", description = "No player characteristics found",
							content = @Content(mediaType = "text/plain",
									schema = @Schema(implementation = String.class))) })
	@GetMapping("")
	ResponseEntity<Page<PlayerCharacteristicDto>> getAllPlayerCharacteristics(
			@Parameter(hidden = true) @ParameterObject @PageableDefault(sort = { "id" }) Pageable pageable);

	@Operation(summary = "Create a player characteristic",
			description = "Create a player characteristic with the provided data")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Player characteristic created successfully",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = PlayerCharacteristicDto.class))),
			@ApiResponse(responseCode = "409", description = "Player characteristic with specified id already exists",
					content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))), })
	@PostMapping("")
	ResponseEntity<PlayerCharacteristicDto> createPlayerCharacteristic(
			@Valid @RequestBody PlayerCharacteristicCreateDto playerCharacteristicToCreate);

	@Operation(summary = "Update a player characteristic",
			description = "Updates an existing player characteristic with the provided data")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Player characteristic updated successfully",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = PlayerCharacteristicDto.class))),
			@ApiResponse(responseCode = "404", description = "Player characteristic not found",
					content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))) })
	@PutMapping("")
	ResponseEntity<PlayerCharacteristicDto> updatePlayerCharacteristic(
			@Valid @RequestBody PlayerCharacteristicDto updatedPlayer);

	@Operation(summary = "Delete a player characteristic",
			description = "Deletes a player characteristic by the specified ID")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Player characteristic deleted successfully"),
			@ApiResponse(responseCode = "404", description = "Player characteristic not found",
					content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))),
			@ApiResponse(responseCode = "409",
					description = "Player characteristic has related entities which have to be deleted before deletion",
					content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))) })
	@DeleteMapping("{id}")
	ResponseEntity<Void> deletePlayerCharacteristic(@PathVariable UUID id);

}
