package cz.fi.muni.pa165.userservice.api.controllers;

import cz.fi.muni.pa165.dto.userService.UserCreateDto;
import cz.fi.muni.pa165.dto.userService.UserCreateRequestDto;
import cz.fi.muni.pa165.dto.userService.UserUpdateDto;
import cz.fi.muni.pa165.dto.userService.UserViewDto;
import cz.fi.muni.pa165.service.userService.api.UserController;
import cz.fi.muni.pa165.userservice.business.facades.UserFacade;
import cz.fi.muni.pa165.userservice.util.AuthUtil;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.MediaType;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/user")
@Tag(name = "User API", description = "Operations related to user.")
public class UserControllerImpl implements UserController {

	private final UserFacade userFacade;

	private final AuthUtil authUtil;

	@Autowired
	public UserControllerImpl(UserFacade userFacade, AuthUtil authUtil) {
		this.userFacade = userFacade;
		this.authUtil = authUtil;
	}

	@Override
	@Operation(description = "Returns desired user by its id.",
			responses = {
					@ApiResponse(responseCode = "200", description = "User view with desired details.",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
									schema = @Schema(implementation = UserViewDto.class))),
					@ApiResponse(responseCode = "404", description = "Desired user does not exist",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
									schema = @Schema(implementation = String.class))),
					@ApiResponse(responseCode = "400", description = "Path parameter does not have a form of UUID",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
									schema = @Schema(implementation = String.class))) },
			parameters = @Parameter(name = "id", description = "UUID of desired user", required = true))
	@GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public UserViewDto getUserById(@PathVariable UUID id) {
		return userFacade.getUserById(id);
	}

	@Override
	@Operation(description = "Returns desired user by its email.",
			responses = {
					@ApiResponse(responseCode = "200", description = "User view with desired details.",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
									schema = @Schema(implementation = UserViewDto.class))),
					@ApiResponse(responseCode = "404", description = "Desired user does not exist",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
									schema = @Schema(implementation = String.class))),
					@ApiResponse(responseCode = "400", description = "Path parameter is not a valid email address.",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
									schema = @Schema(implementation = String.class))) },
			parameters = @Parameter(name = "email", description = "Email address of desired user", required = true))
	@GetMapping(path = "/by-email/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
	public UserViewDto getUserByEmail(@PathVariable String email) {
		return userFacade.getUserByMail(email);
	}

	@Override
	@Operation(description = "Returns desired user by its username.",
			responses = {
					@ApiResponse(responseCode = "200", description = "User view with desired details.",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
									schema = @Schema(implementation = UserViewDto.class))),
					@ApiResponse(responseCode = "404", description = "Desired user does not exist",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
									schema = @Schema(implementation = String.class))) },
			parameters = @Parameter(name = "username", description = "Username of desired user", required = true))
	@GetMapping(path = "/by-username/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
	public UserViewDto getUserByUsername(@PathVariable String username) {
		return userFacade.getUserByUsername(username);
	}

	@Override
	@Operation(description = "Creates a new user with given properties",
			responses = {
					@ApiResponse(responseCode = "201", description = "User View with newly created user data",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
									schema = @Schema(implementation = UserViewDto.class))),
					@ApiResponse(responseCode = "400", description = "Validation of input data failed",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
									schema = @Schema(implementation = String.class))),
					@ApiResponse(responseCode = "409", description = "Entity already exists",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
									schema = @Schema(implementation = String.class))), },
			requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Data for user creation",
					required = true,
					content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
							schema = @Schema(implementation = UserCreateRequestDto.class))))
	@PostMapping(path = "/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	public UserViewDto registerUser(@RequestBody @Valid UserCreateRequestDto userCreateRequestDto) {
		return userFacade.registerUser(UserCreateDto.createFromRequest(userCreateRequestDto, authUtil.getAuthMail()));
	}

	@Override
	@Operation(description = "Updates user by it's ID", responses = {
			@ApiResponse(responseCode = "200", description = "User was successfully updated, returning updated model",
					content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
							schema = @Schema(implementation = UserViewDto.class))),
			@ApiResponse(responseCode = "404", description = "Desired user does not exist",
					content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
							schema = @Schema(implementation = String.class))),
			@ApiResponse(responseCode = "400", description = "Request body does not meet validation requirements",
					content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
							schema = @Schema(implementation = String.class))) },
			requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Updated user values",
					required = true,
					content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
							schema = @Schema(implementation = UserUpdateDto.class))))
	@PutMapping(path = "/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	public UserViewDto updateUser(@RequestBody @Valid UserUpdateDto userUpdateDto) {
		return userFacade.updateUser(userUpdateDto);
	}

	@Override
	@Operation(description = "Deactivates user account.",
			responses = {
					@ApiResponse(responseCode = "200", description = "Account was successfully deactivated.",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
									schema = @Schema(implementation = UserViewDto.class))),
					@ApiResponse(responseCode = "404", description = "Desired user does not exist",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
									schema = @Schema(implementation = String.class))) },
			parameters = @Parameter(name = "id", description = "Id of user which should be deactivated.",
					required = true))
	@PutMapping(path = "/deactivate/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	public UserViewDto deactivateUser(@PathVariable UUID id) {
		return userFacade.deactivateUser(id);
	}

	@Override
	@Operation(description = "Activates user account.",
			responses = {
					@ApiResponse(responseCode = "200", description = "Account was successfully activated.",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
									schema = @Schema(implementation = UserViewDto.class))),
					@ApiResponse(responseCode = "404", description = "Desired user does not exist",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
									schema = @Schema(implementation = String.class))) },
			parameters = @Parameter(name = "id", description = "Id of user which should be activated.",
					required = true))
	@PutMapping(path = "/activate/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	public UserViewDto activateUser(@PathVariable UUID id) {
		return userFacade.activateUser(id);
	}

	@Override
	@Operation(description = "Returns all users.",
			responses = { @ApiResponse(responseCode = "200", description = "List of all users.",
					content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
							array = @ArraySchema(schema = @Schema(implementation = UserViewDto.class)))) })
	@GetMapping(path = "/", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<UserViewDto> getAllUsers() {
		return userFacade.getAllUsers();
	}

	@Override
	@Operation(description = "Return false/true base on fact if user is admin.",
			responses = {
					@ApiResponse(responseCode = "200", description = "Return false/true base on fact if user is admin.",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
									schema = @Schema(type = "boolean"))),
					@ApiResponse(responseCode = "404", description = "User was not found!") })
	@GetMapping(path = "/{id}/is-admin", produces = MediaType.APPLICATION_JSON_VALUE)
	public boolean isUserAdmin(@PathVariable UUID id) {
		return userFacade.isUserAdmin(id);
	}

	@Override
	@Operation(summary = "Update admin status of user", description = "Sets the 'isAdmin' flag for a specific user.",
			responses = { @ApiResponse(responseCode = "204", description = "Admin status updated successfully"),
					@ApiResponse(responseCode = "404", description = "User not found") },
			requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
					description = "True/false if user should be admin.", required = true,
					content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
							schema = @Schema(implementation = Boolean.class))))
	@PatchMapping(path = "/{id}/is-admin", consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void setIsUserAdmin(@PathVariable UUID id, @RequestBody boolean isAdmin) {
		userFacade.setIsUserAdmin(id, isAdmin);
	}

}
