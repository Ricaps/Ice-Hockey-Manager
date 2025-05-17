package cz.fi.muni.pa165.worldlistservice.api.controllers;

import cz.fi.muni.pa165.dto.worldlistservice.playercharacteristic.PlayerCharacteristicDto;
import cz.fi.muni.pa165.dto.worldlistservice.playercharacteristic.create.PlayerCharacteristicCreateDto;
import cz.fi.muni.pa165.service.wordlistservice.api.PlayerCharacteristicController;
import cz.fi.muni.pa165.worldlistservice.business.facades.interfaces.PlayerCharacteristicFacade;
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
@RequestMapping("/v1/player-characteristics/")
@Tag(name = "Player characteristics")
public class PlayerCharacteristicControllerImpl implements PlayerCharacteristicController {

	private final PlayerCharacteristicFacade playerCharacteristicFacade;

	@Autowired
	PlayerCharacteristicControllerImpl(PlayerCharacteristicFacade playerCharacteristicFacade) {
		this.playerCharacteristicFacade = playerCharacteristicFacade;
	}

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
	@Override
	public ResponseEntity<PlayerCharacteristicDto> getPlayerCharacteristicById(@PathVariable UUID id) {
		var playerCharacteristicOption = playerCharacteristicFacade.findById(id);

		return playerCharacteristicOption.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}

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
	@Override
	public ResponseEntity<Page<PlayerCharacteristicDto>> getAllPlayerCharacteristics(
			@Parameter(hidden = true) @ParameterObject @PageableDefault(sort = { "id" }) Pageable pageable) {
		Page<PlayerCharacteristicDto> playerCharacteristicsPage = playerCharacteristicFacade.findAll(pageable);

		return playerCharacteristicsPage.hasContent() ? ResponseEntity.ok(playerCharacteristicsPage)
				: ResponseEntity.notFound().build();
	}

	@Operation(summary = "Create a player characteristic",
			description = "Create a player characteristic with the provided data")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Player characteristic created successfully",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = PlayerCharacteristicDto.class))),
			@ApiResponse(responseCode = "409", description = "Player characteristic with specified id already exists",
					content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))), })
	@PostMapping("")
	@Override
	public ResponseEntity<PlayerCharacteristicDto> createPlayerCharacteristic(
			@Valid @RequestBody PlayerCharacteristicCreateDto playerCharacteristicToCreate) {
		return ResponseEntity.ok(playerCharacteristicFacade.create(playerCharacteristicToCreate));
	}

	@Operation(summary = "Update a player characteristic",
			description = "Updates an existing player characteristic with the provided data")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Player characteristic updated successfully",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = PlayerCharacteristicDto.class))),
			@ApiResponse(responseCode = "404", description = "Player characteristic not found",
					content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))) })
	@PutMapping("")
	@Override
	public ResponseEntity<PlayerCharacteristicDto> updatePlayerCharacteristic(
			@Valid @RequestBody PlayerCharacteristicDto updatedPlayer) {
		return ResponseEntity.ok(playerCharacteristicFacade.update(updatedPlayer));
	}

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
	@Override
	public ResponseEntity<Void> deletePlayerCharacteristic(@PathVariable UUID id) {
		playerCharacteristicFacade.delete(id);
		return ResponseEntity.ok().build();
	}

}
