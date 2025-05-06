package cz.fi.muni.pa165.gameservice.api.controllers;

import cz.fi.muni.pa165.dto.gameservice.ArenaCreateDto;
import cz.fi.muni.pa165.dto.gameservice.ArenaViewDto;
import cz.fi.muni.pa165.gameservice.business.facades.ArenaFacade;
import cz.fi.muni.pa165.service.gameservice.api.ArenaController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/arena")
@Tag(name = "Arena API", description = "Operations related to arenas")
public class ArenaControllerImpl implements ArenaController {

	private final ArenaFacade arenaFacade;

	public ArenaControllerImpl(ArenaFacade arenaFacade) {
		this.arenaFacade = arenaFacade;
	}

	@Override
	@Operation(description = "Gets arena pageable",
			responses = { @ApiResponse(responseCode = "200", description = "All arenas wrapped into the pageable",
					content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)) })
	@GetMapping(path = "/", produces = MediaType.APPLICATION_JSON_VALUE)
	public Page<ArenaViewDto> findAll(
			@Parameter(hidden = true) @ParameterObject @PageableDefault(sort = { "arenaName" }) Pageable pageable) {
		return arenaFacade.findAllPageable(pageable);
	}

	@Override
	@Operation(description = "Creates a new arena with given properties", responses = {
			@ApiResponse(responseCode = "201", description = "Arena View with newly created competition",
					content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
							schema = @Schema(implementation = ArenaViewDto.class))),
			@ApiResponse(responseCode = "400", description = "Validation of input data failed", content = @Content), },
			requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Data for arena creation",
					required = true,
					content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
							schema = @Schema(implementation = ArenaCreateDto.class))))
	@PostMapping(path = "/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	public ArenaViewDto createArena(@RequestBody @Valid ArenaCreateDto arenaCreateDto) {
		return arenaFacade.createArena(arenaCreateDto);
	}

	@Override
	@Operation(description = "Updates whole arena of specified UUID",
			responses = {
					@ApiResponse(responseCode = "200",
							description = "Arena model was successfully updated, returning updated model",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
									schema = @Schema(implementation = ArenaViewDto.class))),
					@ApiResponse(responseCode = "404", description = "Desired arena doesn't exist", content = @Content),
					@ApiResponse(responseCode = "400",
							description = "Request body doesn't meet validation requirements", content = @Content) },
			parameters = @Parameter(name = "arenaUUID", description = "UUID of the Arena you want to update",
					required = true),
			requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
					description = "New value of the updated arena", required = true,
					content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
							schema = @Schema(implementation = ArenaCreateDto.class))))
	@PutMapping(path = "/{arenaUUID}", consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	public ArenaViewDto updateArena(@PathVariable UUID arenaUUID, @RequestBody @Valid ArenaCreateDto arenaCreateDto) {
		return arenaFacade.updateArena(arenaUUID, arenaCreateDto);
	}

	@Override
	@Operation(description = "Delete arena with specified UUID",
			responses = { @ApiResponse(responseCode = "204", description = "Arena model was successfully deleted"),
					@ApiResponse(responseCode = "404", description = "Desired arena doesn't exist", content = @Content),
					@ApiResponse(responseCode = "409", description = "Arena is already used, cannot be deleted.") },
			parameters = @Parameter(name = "arenaUUID", description = "UUID of the Arena you want to update",
					required = true),
			requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
					description = "New value of the updated arena", required = true,
					content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
							schema = @Schema(implementation = ArenaCreateDto.class))))
	@DeleteMapping(path = "/{arenaUUID}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteArena(@PathVariable UUID arenaUUID) {
		arenaFacade.deleteArena(arenaUUID);
	}

}
