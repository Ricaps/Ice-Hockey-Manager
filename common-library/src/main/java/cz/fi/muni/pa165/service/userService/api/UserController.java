package cz.fi.muni.pa165.service.userService.api;

import cz.fi.muni.pa165.dto.userService.UserCreateRequestDto;
import cz.fi.muni.pa165.dto.userService.UserUpdateDto;
import cz.fi.muni.pa165.dto.userService.UserViewDto;

import java.util.List;
import java.util.UUID;

public interface UserController {

	UserViewDto getUserById(UUID id);

	UserViewDto getUserByEmail(String email);

	UserViewDto getUserByUsername(String username);

	UserViewDto registerUser(UserCreateRequestDto userCreateRequestDto);

	UserViewDto updateUser(UserUpdateDto userUpdateDto);

	UserViewDto deactivateUser(UUID id);

	UserViewDto activateUser(UUID id);

	List<UserViewDto> getAllUsers();

	boolean isUserAdmin(UUID id);

	void setIsUserAdmin(UUID id, boolean isAdmin);

}
