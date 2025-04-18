package cz.fi.muni.pa165.userservice.api.controllers;

import cz.fi.muni.pa165.dto.userService.ChangePasswordRequestDto;
import cz.fi.muni.pa165.dto.userService.UserCreateDto;
import cz.fi.muni.pa165.dto.userService.UserViewDto;
import cz.fi.muni.pa165.service.userService.api.UserController;
import cz.fi.muni.pa165.userservice.business.facades.UserFacade;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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

	@Autowired
	public UserControllerImpl(UserFacade userFacade) {
		this.userFacade = userFacade;
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
							schema = @Schema(implementation = UserCreateDto.class))))
	@PostMapping(path = "/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	public UserViewDto registerUser(@RequestBody @Valid UserCreateDto userCreateDto) {
		return userFacade.registerUser(userCreateDto);
	}

	@Override
	@Operation(description = "Updates user's password of user specified by it's ID", responses = {
			@ApiResponse(responseCode = "200",
					description = "User password was successfully updated, returning updated model",
					content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
							schema = @Schema(implementation = UserViewDto.class))),
			@ApiResponse(responseCode = "404", description = "Desired user does not exist",
					content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
							schema = @Schema(implementation = String.class))),
			@ApiResponse(responseCode = "400", description = "Request body does not meet validation requirements",
					content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
							schema = @Schema(implementation = String.class))) },
			requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
					description = "New and old password values", required = true,
					content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
							schema = @Schema(implementation = ChangePasswordRequestDto.class))))
	@PutMapping(path = "/change-password", consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	public UserViewDto changeUserPassword(@RequestBody @Valid ChangePasswordRequestDto changePasswordRequestDto) {
		return userFacade.changePassword(changePasswordRequestDto);
	}

	@Override
	@Operation(summary = "Resets a user's password",
			description = "Resets the password for a user identified by userId.",
			responses = {
					@ApiResponse(responseCode = "200", description = "User view with updated details.",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
									schema = @Schema(implementation = UserViewDto.class))),
					@ApiResponse(responseCode = "404", description = "User not found",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
									schema = @Schema(implementation = String.class))),
					@ApiResponse(responseCode = "400", description = "Password does not meet validation requirements",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
									schema = @Schema(implementation = String.class))) },
			requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "New password value",
					required = true,
					content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
							schema = @Schema(implementation = String.class))))
	@PutMapping(path = "/{userId}/password/reset", produces = MediaType.APPLICATION_JSON_VALUE)
	public UserViewDto resetUserPassword(
			@Parameter(description = "UUID of the user whose password is being reset",
					required = true) @PathVariable UUID userId,

			@Parameter(description = "New password for the user", required = true) @RequestBody String password) {
		return userFacade.resetPassword(userId, password);
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
							schema = @Schema(implementation = UserViewDto.class))))
	@PutMapping(path = "/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	public UserViewDto updateUser(@RequestBody @Valid UserViewDto userViewDto) {
		return userFacade.updateUser(userViewDto);
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
	@Operation(description = "Add role to user",
			responses = {
					@ApiResponse(responseCode = "200", description = "Role was successfully added.",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
									schema = @Schema(implementation = UserViewDto.class))),
					@ApiResponse(responseCode = "404", description = "Desired user or role does not exist",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
									schema = @Schema(implementation = String.class))) },
			parameters = {
					@Parameter(name = "userId", description = "Id of user to which the role should be added.",
							required = true),
					@Parameter(name = "roleId", description = "Id of role that should be added to user.",
							required = true) })
	@PutMapping(path = "/{userId}/role/{roleId}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	public UserViewDto addRoleToUser(@PathVariable UUID userId, @PathVariable UUID roleId) {
		return userFacade.addRoleToUser(userId, roleId);
	}

	@Override
	@Operation(description = "Delete role from user",
			responses = {
					@ApiResponse(responseCode = "200", description = "Role was successfully deleted.",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
									schema = @Schema(implementation = UserViewDto.class))),
					@ApiResponse(responseCode = "404", description = "Desired user or role does not exist",
							content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
									schema = @Schema(implementation = String.class))) },
			parameters = {
					@Parameter(name = "userId", description = "Id of user from which the role should be deleted.",
							required = true),
					@Parameter(name = "roleId", description = "Id of role that should be deleted from user.",
							required = true) })
	@DeleteMapping(path = "/{userId}/role/{roleId}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	public UserViewDto deleteRoleFromUser(@PathVariable UUID userId, @PathVariable UUID roleId) {
		return userFacade.deleteRoleFromUser(userId, roleId);
	}

}
