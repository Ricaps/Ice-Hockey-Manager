package cz.fi.muni.pa165.userservice.api.controllers;

import cz.fi.muni.pa165.dto.userService.RoleViewDto;
import cz.fi.muni.pa165.service.userService.api.RoleController;
import cz.fi.muni.pa165.userservice.business.facades.RoleFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/role")
@Tag(name = "Role API", description = "Operations related to roles.")
public class RoleControllerImpl implements RoleController {

	private final RoleFacade roleFacade;

	@Autowired
	public RoleControllerImpl(RoleFacade roleFacade) {
		this.roleFacade = roleFacade;
	}

	@Override
	@Operation(description = "Returns desired role by its id.",
			responses = {
					@ApiResponse(responseCode = "200", description = "Role view with desired details.",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
									schema = @Schema(implementation = RoleViewDto.class))),
					@ApiResponse(responseCode = "404", description = "Desired role does not exist",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
									schema = @Schema(implementation = String.class))),
					@ApiResponse(responseCode = "400", description = "Path parameter does not have a form of UUID",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
									schema = @Schema(implementation = String.class))) },
			parameters = @Parameter(name = "id", description = "UUID of desired role", required = true))
	@GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public RoleViewDto getRoleById(@PathVariable UUID id) {
		return roleFacade.getRoleById(id);
	}

	@Override
	@Operation(description = "Returns all available roles.",
			responses = { @ApiResponse(responseCode = "200", description = "List of all roles.",
					content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
							array = @ArraySchema(schema = @Schema(implementation = RoleViewDto.class)))) })
	@GetMapping(path = "/", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<RoleViewDto> getAllRoles() {
		return roleFacade.getAllRoles();
	}

	@Override
	@Operation(description = "Creates a new role with given properties",
			responses = {
					@ApiResponse(responseCode = "201", description = "Role View with newly created role data",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
									schema = @Schema(implementation = RoleViewDto.class))),
					@ApiResponse(responseCode = "400", description = "Validation of input data failed",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
									schema = @Schema(implementation = String.class))),
					@ApiResponse(responseCode = "409", description = "Entity already exists",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
									schema = @Schema(implementation = String.class))), },
			requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Data for role creation",
					required = true,
					content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
							schema = @Schema(implementation = RoleViewDto.class))))
	@PostMapping(path = "/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	public RoleViewDto createRole(@RequestBody @Valid RoleViewDto roleViewDto) {
		return roleFacade.createRole(roleViewDto);
	}

	@Override
	@Operation(description = "Updates role by it's ID", responses = {
			@ApiResponse(responseCode = "200", description = "Role was successfully updated, returning updated model",
					content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
							schema = @Schema(implementation = RoleViewDto.class))),
			@ApiResponse(responseCode = "404", description = "Desired role does not exist",
					content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
							schema = @Schema(implementation = String.class))),
			@ApiResponse(responseCode = "400", description = "Request body does not meet validation requirements",
					content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
							schema = @Schema(implementation = String.class))) },
			requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Updated role values",
					required = true,
					content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
							schema = @Schema(implementation = RoleViewDto.class))))
	@PutMapping(path = "/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	public RoleViewDto updateRole(@RequestBody @Valid RoleViewDto roleViewDto) {
		return roleFacade.updateRole(roleViewDto);
	}

	@Override
	@Operation(description = "Deletes role by it's ID",
			responses = {
					@ApiResponse(responseCode = "200", description = "Role was successfully deleted.",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
									schema = @Schema(implementation = Boolean.class))),
					@ApiResponse(responseCode = "404", description = "Desired role does not exist",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
									schema = @Schema(implementation = String.class))) },
			parameters = @Parameter(name = "id", description = "Id of role that should be deleted.", required = true))
	@DeleteMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	public void deleteRole(@PathVariable UUID id) {
		roleFacade.deleteRole(id);
	}

}
