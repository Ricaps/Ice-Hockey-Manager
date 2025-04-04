package cz.fi.muni.pa165.gameservice.api.controllers;

import cz.fi.muni.pa165.dto.gameService.MatchCreateDto;
import cz.fi.muni.pa165.dto.gameService.MatchViewDto;
import cz.fi.muni.pa165.gameservice.business.facades.MatchFacade;
import cz.fi.muni.pa165.service.gameService.api.MatchController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/matches")
@Tag(name = "Matches API", description = "Operations related to matches")
public class MatchControllerImpl implements MatchController {

	private final MatchFacade matchFacade;

	public MatchControllerImpl(MatchFacade matchFacade) {
		this.matchFacade = matchFacade;
	}

	@Operation(
			description = "Generates matches for the given competition. Competition must have assigned team members and matches cannot be already generated",
			responses = {
					@ApiResponse(responseCode = "201",
							description = "Matches were successfully generated. Generated competitions are returned.",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
									array = @ArraySchema(schema = @Schema(implementation = MatchViewDto.class)))),
					@ApiResponse(responseCode = "400", description = "The request body is not valid",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)), })
	@PostMapping(path = "/competition/{competitionUUID}")
	@ResponseStatus(HttpStatus.CREATED)
	public List<MatchViewDto> generateMatches(@PathVariable UUID competitionUUID) {
		return matchFacade.generateMatches(competitionUUID);
	}

	@Operation(description = "Returns all matches for the given competition with results.", responses = {
			@ApiResponse(responseCode = "200", description = "Array of matches with result.",
					content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
							array = @ArraySchema(schema = @Schema(implementation = MatchViewDto.class)))),
			@ApiResponse(responseCode = "404", description = "Desired competition doesn't exist", content = @Content),
			@ApiResponse(responseCode = "400", description = "Competition UUID is not valid UUID",
					content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)), })
	@GetMapping(path = "/competition/{competitionUUID}")
	public List<MatchViewDto> getMatchesForCompetition(@PathVariable UUID competitionUUID,
			@Parameter(name = "results", description = "Decides if response contains also result of the match.",
					in = ParameterIn.QUERY) @RequestParam(name = "results",
							defaultValue = "false") boolean includeResults) {
		return matchFacade.getMatchesOfCompetition(competitionUUID, includeResults);
	}

	@Operation(description = "Returns desired match defined by it's UUID",
			responses = {
					@ApiResponse(responseCode = "200", description = "Match view with or without results.",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
									schema = @Schema(implementation = MatchViewDto.class))),
					@ApiResponse(responseCode = "404", description = "Desired match doesn't exist", content = @Content),
					@ApiResponse(responseCode = "400", description = "Match UUID is not valid UUID",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)), })
	@GetMapping(path = "/{matchUUID}")
	public MatchViewDto getSingleMatch(@PathVariable UUID matchUUID,
			@Parameter(name = "results", description = "Decides if response contains also result of the match.",
					in = ParameterIn.QUERY) @RequestParam(name = "results",
							defaultValue = "false") boolean includeResults) {
		return matchFacade.getMatch(matchUUID, includeResults);
	}

	@Operation(
			description = "Creates new match. Only friendly matches can be created. The match cannot be part of any competition. Matches for competitions are generated.",
			responses = {
					@ApiResponse(responseCode = "201",
							description = "Match was successfully created. New match is returned in the response body.",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
									array = @ArraySchema(schema = @Schema(implementation = MatchViewDto.class)))),
					@ApiResponse(responseCode = "400", description = "The request body is not valid.",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
					@ApiResponse(responseCode = "404",
							description = "Provided dependent resource probably doesn't exist.", content = @Content) })
	@PostMapping(path = "/")
	@ResponseStatus(HttpStatus.CREATED)
	public MatchViewDto createMatch(@RequestBody @Valid MatchCreateDto newMatch) {
		return matchFacade.createMatch(newMatch);
	}

}
