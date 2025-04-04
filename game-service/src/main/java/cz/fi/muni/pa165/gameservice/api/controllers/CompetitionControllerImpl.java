package cz.fi.muni.pa165.gameservice.api.controllers;

import cz.fi.muni.pa165.dto.gameService.AssignTeamDto;
import cz.fi.muni.pa165.dto.gameService.CompetitionCreateDto;
import cz.fi.muni.pa165.dto.gameService.CompetitionViewDto;
import cz.fi.muni.pa165.gameservice.business.facades.CompetitionFacade;
import cz.fi.muni.pa165.service.gameService.api.CompetitionController;
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

import java.util.UUID;

@RestController
@RequestMapping("/v1/competition")
@Tag(name = "Competition API", description = "Operations related to competitions and assigning teams")
public class CompetitionControllerImpl implements CompetitionController {

	private final CompetitionFacade competitionFacade;

	@Autowired
	public CompetitionControllerImpl(CompetitionFacade competitionFacade) {
		this.competitionFacade = competitionFacade;
	}

	@Override
	@Operation(description = "Creates a new competition with given properties", responses = {
			@ApiResponse(responseCode = "201", description = "Competition View with newly created competition",
					content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
							schema = @Schema(implementation = CompetitionViewDto.class))),
			@ApiResponse(responseCode = "400", description = "Validation of input data failed", content = @Content), },
			requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
					description = "Data for competition creation", required = true,
					content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
							schema = @Schema(implementation = CompetitionCreateDto.class))))
	@PostMapping(path = "/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	public CompetitionViewDto addCompetition(@RequestBody @Valid CompetitionCreateDto body) {
		return competitionFacade.addCompetition(body);
	}

	@Override
	@Operation(description = "Returns desired competition by defined UUID", responses = {
			@ApiResponse(responseCode = "200", description = "Competition View with desired competition details",
					content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
							schema = @Schema(implementation = CompetitionViewDto.class))),
			@ApiResponse(responseCode = "404", description = "Desired competition doesn't exist", content = @Content),
			@ApiResponse(responseCode = "400", description = "Path parameter doesn't have form of UUID",
					content = @Content) },
			parameters = @Parameter(name = "uuid", description = "UUID of the desired Competition model",
					required = true))
	@GetMapping(path = "/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
	public CompetitionViewDto getCompetition(@PathVariable UUID uuid) {
		return competitionFacade.getCompetition(uuid);
	}

	@Override
	@Operation(description = "Updates whole competition of specified UUID", responses = {
			@ApiResponse(responseCode = "200",
					description = "Competition model was successfully updated, returning updated model",
					content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
							schema = @Schema(implementation = CompetitionViewDto.class))),
			@ApiResponse(responseCode = "404", description = "Desired competition doesn't exist", content = @Content),
			@ApiResponse(responseCode = "400", description = "Request body doesn't meet validation requirements",
					content = @Content) },
			parameters = @Parameter(name = "uuid", description = "UUID of the Competition you want to update",
					required = true),
			requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
					description = "New value of the updated competition", required = true,
					content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
							schema = @Schema(implementation = CompetitionCreateDto.class))))
	@PutMapping(path = "/{uuid}", consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	public CompetitionViewDto updateCompetition(@PathVariable UUID uuid,
			@RequestBody @Valid CompetitionCreateDto competitionCreateDto) {
		return competitionFacade.updateCompetition(uuid, competitionCreateDto);
	}

	@Override
	@Operation(
			description = "Assigns team to the given competition. Team cannot be assigned to the ongoing competition.",
			responses = {
					@ApiResponse(responseCode = "201",
							description = "Team was successfully assigned to the given competition.",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
					@ApiResponse(responseCode = "404", description = "You are not allowed to perform this action.",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
					@ApiResponse(responseCode = "400", description = "The request body is not valid",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
					@ApiResponse(responseCode = "409",
							description = "Team was already assigned to the given competition",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))

			})
	@PostMapping(path = "/{competitionUUID}/teams", consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	public void assignTeam(@PathVariable UUID competitionUUID, @RequestBody @Valid AssignTeamDto assignTeamDto) {
		competitionFacade.assignTeam(competitionUUID, assignTeamDto);
	}

}
