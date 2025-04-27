package cz.fi.muni.pa165.teamservice.api.controllers;

import cz.fi.muni.pa165.dto.teamService.FictiveTeamCreateDTO;
import cz.fi.muni.pa165.dto.teamService.FictiveTeamDTO;
import cz.fi.muni.pa165.dto.teamService.FictiveTeamUpdateDTO;
import cz.fi.muni.pa165.teamservice.business.facades.FictiveTeamFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * @author Jan Martinek
 */
@RestController
@RequestMapping("/v1/fictive-team")
@Tag(name = "FictiveTeam API", description = "Operations related to fictiveTeam management")
public class FictiveTeamController {

	private final FictiveTeamFacade fictiveTeamFacade;

	@Autowired
	public FictiveTeamController(FictiveTeamFacade fictiveTeamFacade) {
		this.fictiveTeamFacade = fictiveTeamFacade;
	}

	@Operation(description = "Creates a new fictiveTeam with given properties", responses = {
			@ApiResponse(responseCode = "201", description = "FictiveTeam successfully created",
					content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
							schema = @Schema(implementation = FictiveTeamDTO.class))),
			@ApiResponse(responseCode = "400", description = "Validation of input data failed", content = @Content),
			@ApiResponse(responseCode = "409", description = "FictiveTeam with this ID already exists",
					content = @Content) },
			requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
					description = "Data for fictiveTeam creation", required = true,
					content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
							schema = @Schema(implementation = FictiveTeamCreateDTO.class))))
	@PostMapping(path = "/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	public FictiveTeamDTO createFictiveTeam(@RequestBody @Valid FictiveTeamCreateDTO body) {
		return fictiveTeamFacade.createFictiveTeam(body);
	}

	@Operation(description = "Returns fictiveTeam by specified UUID",
			responses = {
					@ApiResponse(responseCode = "200", description = "FictiveTeam details",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
									schema = @Schema(implementation = FictiveTeamDTO.class))),
					@ApiResponse(responseCode = "404", description = "FictiveTeam not found", content = @Content),
					@ApiResponse(responseCode = "400", description = "Invalid UUID format", content = @Content) },
			parameters = @Parameter(name = "uuid", description = "UUID of the fictiveTeam", required = true))
	@GetMapping(path = "/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
	public FictiveTeamDTO getFictiveTeam(@PathVariable UUID uuid) {
		return fictiveTeamFacade.findById(uuid);
	}

	@Operation(description = "Updates existing fictiveTeam",
			responses = {
					@ApiResponse(responseCode = "200", description = "Updated fictiveTeam details",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
									schema = @Schema(implementation = FictiveTeamDTO.class))),
					@ApiResponse(responseCode = "404", description = "FictiveTeam not found", content = @Content),
					@ApiResponse(responseCode = "400", description = "Validation failed", content = @Content) },
			parameters = @Parameter(name = "uuid", description = "UUID of the fictiveTeam to update", required = true),
			requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
					description = "Updated fictiveTeam data", required = true,
					content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
							schema = @Schema(implementation = FictiveTeamUpdateDTO.class))))
	@PutMapping(path = "/{uuid}", consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public FictiveTeamDTO updateFictiveTeam(@PathVariable UUID uuid, @RequestBody @Valid FictiveTeamUpdateDTO body) {
		return fictiveTeamFacade.updateFictiveTeam(body);
	}

	@Operation(description = "Deletes a fictiveTeam",
			responses = {
					@ApiResponse(responseCode = "204", description = "FictiveTeam successfully deleted",
							content = @Content),
					@ApiResponse(responseCode = "404", description = "FictiveTeam not found", content = @Content) },
			parameters = @Parameter(name = "uuid", description = "UUID of the fictiveTeam to delete", required = true))
	@DeleteMapping(path = "/{uuid}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteFictiveTeam(@PathVariable UUID uuid) {
		fictiveTeamFacade.deleteFictiveTeam(uuid);
	}

	@Operation(description = "Lists all fictiveTeams",
			responses = { @ApiResponse(responseCode = "200", description = "List of all fictiveTeams",
					content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
							schema = @Schema(implementation = FictiveTeamDTO[].class))) })
	@GetMapping(path = "/", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<FictiveTeamDTO> getAllFictiveTeams() {
		return fictiveTeamFacade.findAll();
	}

	@Operation(description = "Finds all fictiveTeams by owner UUID",
			responses = {
					@ApiResponse(responseCode = "200", description = "List of teams owned by the user",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
									schema = @Schema(implementation = FictiveTeamDTO[].class))),
					@ApiResponse(responseCode = "400", description = "Invalid UUID format", content = @Content) },
			parameters = @Parameter(name = "ownerId", description = "UUID of the team owner", required = true))
	@GetMapping(path = "/owner/{ownerId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<FictiveTeamDTO> getTeamsByOwner(@PathVariable UUID ownerId) {
		return fictiveTeamFacade.findByOwnerId(ownerId);
	}

}
