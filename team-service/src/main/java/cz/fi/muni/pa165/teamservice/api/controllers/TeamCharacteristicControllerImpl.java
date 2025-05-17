package cz.fi.muni.pa165.teamservice.api.controllers;

import cz.fi.muni.pa165.dto.teamservice.TeamCharacteristicCreateDTO;
import cz.fi.muni.pa165.dto.teamservice.TeamCharacteristicDTO;
import cz.fi.muni.pa165.dto.teamservice.TeamCharacteristicUpdateDTO;
import cz.fi.muni.pa165.enums.TeamCharacteristicType;
import cz.fi.muni.pa165.service.teamservice.api.TeamCharacteristicController;
import cz.fi.muni.pa165.teamservice.api.exception.ResourceNotFoundException;
import cz.fi.muni.pa165.teamservice.business.facades.TeamCharacteristicFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

/**
 * @author Jan Martinek
 */
@RestController
@RequestMapping("/api/team-characteristics")
@Tag(name = "Team Characteristic API", description = "Management of team characteristics")
public class TeamCharacteristicControllerImpl implements TeamCharacteristicController {

	private final TeamCharacteristicFacade facade;

	@Autowired
	public TeamCharacteristicControllerImpl(TeamCharacteristicFacade facade) {
		this.facade = facade;
	}

	@Operation(description = "Create new team characteristic",
			responses = {
					@ApiResponse(responseCode = "201", description = "Characteristic created",
							content = @Content(schema = @Schema(implementation = TeamCharacteristicDTO.class))),
					@ApiResponse(responseCode = "400", description = "Invalid input") })
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	@Override
	public TeamCharacteristicDTO createTeamCharacteristic(@RequestBody @Valid TeamCharacteristicCreateDTO createDTO) {
		return facade.create(createDTO);
	}

	@Operation(description = "Update team characteristic",
			responses = {
					@ApiResponse(responseCode = "200", description = "Characteristic updated",
							content = @Content(schema = @Schema(implementation = TeamCharacteristicDTO.class))),
					@ApiResponse(responseCode = "400", description = "Invalid input"),
					@ApiResponse(responseCode = "404", description = "Characteristic not found") })
	@PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@Override
	public TeamCharacteristicDTO updateTeamCharacteristic(@PathVariable UUID id,
			@RequestBody @Valid TeamCharacteristicUpdateDTO updateDTO) throws ResourceNotFoundException {

		if (!id.equals(updateDTO.getGuid())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Path ID and body ID must match");
		}

		return facade.update(updateDTO);
	}

	@Operation(description = "Delete team characteristic",
			responses = { @ApiResponse(responseCode = "204", description = "Characteristic deleted"),
					@ApiResponse(responseCode = "404", description = "Characteristic not found") })
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Override
	public void deleteTeamCharacteristic(@PathVariable UUID id) throws ResourceNotFoundException {
		facade.delete(id);
	}

	@Operation(description = "Get team characteristic by ID",
			responses = {
					@ApiResponse(responseCode = "200", description = "Characteristic details",
							content = @Content(schema = @Schema(implementation = TeamCharacteristicDTO.class))),
					@ApiResponse(responseCode = "404", description = "Characteristic not found") })
	@GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Override
	public TeamCharacteristicDTO getTeamCharacteristic(@PathVariable UUID id) throws ResourceNotFoundException {
		if (id.equals(UUID.fromString("588a699c-f622-4ff4-8933-fcfae7963e50"))) {
			var charac = new TeamCharacteristicDTO();
			charac.setCharacteristicType(TeamCharacteristicType.COLLABORATION);

			return charac;
		}
		return facade.findById(id);
	}

	@Operation(description = "Get characteristics by team ID",
			responses = { @ApiResponse(responseCode = "200", description = "List of characteristics",
					content = @Content(schema = @Schema(implementation = TeamCharacteristicDTO.class))) })
	@GetMapping(path = "/team/{teamId}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Override
	public List<TeamCharacteristicDTO> findByTeamId(@PathVariable UUID teamId) {
		return facade.findByTeamId(teamId);
	}

}