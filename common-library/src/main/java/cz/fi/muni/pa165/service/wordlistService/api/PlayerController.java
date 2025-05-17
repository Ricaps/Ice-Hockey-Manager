package cz.fi.muni.pa165.service.wordlistservice.api;

import cz.fi.muni.pa165.dto.worldlistservice.player.create.PlayerCreateDto;
import cz.fi.muni.pa165.dto.worldlistservice.player.detail.PlayerDetailDto;
import cz.fi.muni.pa165.dto.worldlistservice.player.list.PlayerListDto;
import cz.fi.muni.pa165.dto.worldlistservice.player.update.PlayerUpdateDto;
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

public interface PlayerController {

	@Operation(summary = "Get player by id", description = "Returns a player by specified id")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successful Response",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = PlayerDetailDto.class))),
			@ApiResponse(responseCode = "404", description = "Player not found",
					content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))) })
	@GetMapping("{id}")
	@ResponseBody
	ResponseEntity<PlayerDetailDto> getPlayerById(@PathVariable UUID id);

	@Operation(summary = "Get all players with pagination", description = "Returns a paginated list of all players")
	@ApiResponses(
			value = {
					@ApiResponse(responseCode = "200", description = "Successful Response",
							content = @Content(mediaType = "application/json",
									schema = @Schema(implementation = Page.class))),
					@ApiResponse(responseCode = "404", description = "No players found",
							content = @Content(mediaType = "text/plain",
									schema = @Schema(implementation = String.class))) })
	@GetMapping("")
	ResponseEntity<Page<PlayerListDto>> getAllPlayers(
			@Parameter(hidden = true) @ParameterObject @PageableDefault(sort = { "lastName" }) Pageable pageable);

	@Operation(summary = "Create a player", description = "Create a player with the provided data")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Player created successfully",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = PlayerDetailDto.class))),
			@ApiResponse(responseCode = "404", description = "Player or related entities have not been found",
					content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))),
			@ApiResponse(responseCode = "409", description = "Player with specified id already exists",
					content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))), })
	@PostMapping("")
	ResponseEntity<PlayerDetailDto> createPlayer(@Valid @RequestBody PlayerCreateDto playerToCreate);

	@Operation(summary = "Update a player", description = "Updates an existing player with the provided data")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Player updated successfully",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = PlayerUpdateDto.class))),
			@ApiResponse(responseCode = "404", description = "Player or related entities have not been found",
					content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))), })
	@PutMapping("")
	ResponseEntity<PlayerDetailDto> updatePlayer(@Valid @RequestBody PlayerUpdateDto updatedPlayer);

	@Operation(summary = "Delete a player", description = "Deletes a player by the specified ID")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Player deleted successfully"),
			@ApiResponse(responseCode = "404", description = "Player not found",
					content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))),
			@ApiResponse(responseCode = "409",
					description = "Player has related entities which have to be deleted before deletion",
					content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))) })
	@DeleteMapping("{id}")
	ResponseEntity<Void> deletePlayer(@PathVariable UUID id);

}
