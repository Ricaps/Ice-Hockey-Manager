package cz.fi.muni.pa165.service.wordlistService.api;

import cz.fi.muni.pa165.dto.worldlistservice.team.create.TeamCreateDto;
import cz.fi.muni.pa165.dto.worldlistservice.team.detail.TeamDetailDto;
import cz.fi.muni.pa165.dto.worldlistservice.team.list.TeamListDto;
import cz.fi.muni.pa165.dto.worldlistservice.team.update.TeamUpdateDto;
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

public interface TeamController {

	@Operation(summary = "Get team by id", description = "Returns a team by specified id")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successful Response",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = TeamDetailDto.class))),
			@ApiResponse(responseCode = "404", description = "Team not found",
					content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))) })
	@GetMapping("{id}")
	@ResponseBody
	ResponseEntity<TeamDetailDto> getTeamById(@PathVariable UUID id);

	@Operation(summary = "Get all teams with pagination", description = "Returns a paginated list of all teams")
	@ApiResponses(
			value = {
					@ApiResponse(responseCode = "200", description = "Successful Response",
							content = @Content(mediaType = "application/json",
									schema = @Schema(implementation = Page.class))),
					@ApiResponse(responseCode = "404", description = "No teams found",
							content = @Content(mediaType = "text/plain",
									schema = @Schema(implementation = String.class))) })
	@GetMapping("")
	ResponseEntity<Page<TeamListDto>> getAllTeams(
			@Parameter(hidden = true) @ParameterObject @PageableDefault(sort = { "name" }) Pageable pageable);

	@Operation(summary = "Create a team", description = "Create a team with the provided data")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Team created successfully",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = TeamDetailDto.class))),
			@ApiResponse(responseCode = "404", description = "Related entities have not been found",
					content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))),
			@ApiResponse(responseCode = "409", description = "Team with specified id already exists",
					content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))), })
	@PostMapping("")
	ResponseEntity<TeamDetailDto> createTeam(@Valid @RequestBody TeamCreateDto teamToCreate);

	@Operation(summary = "Update a team", description = "Updates an existing team with the provided data")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Team updated successfully",
					content = @Content(mediaType = "application/json",
							schema = @Schema(implementation = TeamUpdateDto.class))),
			@ApiResponse(responseCode = "404", description = "Team or related entities have not been found",
					content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))), })
	@PutMapping("")
	ResponseEntity<TeamDetailDto> updateTeam(@Valid @RequestBody TeamUpdateDto updatedTeam);

	@Operation(summary = "Delete a team", description = "Deletes a team by the specified ID")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Team deleted successfully"),
			@ApiResponse(responseCode = "404", description = "Team not found",
					content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))),
			@ApiResponse(responseCode = "409",
					description = "Team has related entities which have to be deleted before deletion",
					content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))) })
	@DeleteMapping("{id}")
	ResponseEntity<Void> deleteTeam(@PathVariable UUID id);

}
