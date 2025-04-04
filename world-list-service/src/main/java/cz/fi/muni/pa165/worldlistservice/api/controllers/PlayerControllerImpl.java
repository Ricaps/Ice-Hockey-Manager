package cz.fi.muni.pa165.worldlistservice.api.controllers;

import cz.fi.muni.pa165.dto.worldlistservice.player.create.PlayerCreateDto;
import cz.fi.muni.pa165.dto.worldlistservice.player.detail.PlayerDetailDto;
import cz.fi.muni.pa165.dto.worldlistservice.player.list.PlayerListDto;
import cz.fi.muni.pa165.dto.worldlistservice.player.update.PlayerUpdateDto;
import cz.fi.muni.pa165.service.wordlistService.api.PlayerController;
import cz.fi.muni.pa165.worldlistservice.business.facades.interfaces.PlayerFacade;
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
@RequestMapping("/v1/players/")
@Tag(name = "Players")
public class PlayerControllerImpl implements PlayerController {

	private final PlayerFacade playerFacade;

	@Autowired
	PlayerControllerImpl(PlayerFacade playerFacade) {
		this.playerFacade = playerFacade;
	}

	@Operation(summary = "Get player by id", description = "Returns a player by specified id")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successful Response",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = PlayerDetailDto.class))),
			@ApiResponse(responseCode = "404", description = "Player not found",
					content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))) })
	@GetMapping("{id}")
	@ResponseBody
	@Override
	public ResponseEntity<PlayerDetailDto> getPlayerById(@PathVariable UUID id) {
		var playerOption = playerFacade.findById(id);

		return playerOption.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}

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
	@Override
	public ResponseEntity<Page<PlayerListDto>> getAllPlayers(
			@Parameter(hidden = true) @ParameterObject @PageableDefault(sort = { "lastName" }) Pageable pageable) {
		Page<PlayerListDto> playersPage = playerFacade.findAll(pageable);

		return playersPage.hasContent() ? ResponseEntity.ok(playersPage) : ResponseEntity.notFound().build();
	}

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
	@Override
	public ResponseEntity<PlayerDetailDto> createPlayer(@Valid @RequestBody PlayerCreateDto playerToCreate) {
		return ResponseEntity.ok(playerFacade.create(playerToCreate));
	}

	@Operation(summary = "Update a player", description = "Updates an existing player with the provided data")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Player updated successfully",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = PlayerUpdateDto.class))),
			@ApiResponse(responseCode = "404", description = "Player or related entities have not been found",
					content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))), })
	@PutMapping("")
	@Override
	public ResponseEntity<PlayerDetailDto> updatePlayer(@Valid @RequestBody PlayerUpdateDto updatedPlayer) {
		return ResponseEntity.ok(playerFacade.update(updatedPlayer));
	}

	@Operation(summary = "Delete a player", description = "Deletes a player by the specified ID")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Player deleted successfully"),
			@ApiResponse(responseCode = "404", description = "Player not found",
					content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))),
			@ApiResponse(responseCode = "409",
					description = "Player has related entities which have to be deleted before deletion",
					content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))) })
	@DeleteMapping("{id}")
	@Override
	public ResponseEntity<Void> deletePlayer(@PathVariable UUID id) {
		playerFacade.delete(id);
		return ResponseEntity.ok().build();
	}

}
