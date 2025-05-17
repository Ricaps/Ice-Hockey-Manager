package cz.fi.muni.pa165.service.userservice.api;

import cz.fi.muni.pa165.dto.userservice.UserCreateRequestDto;
import cz.fi.muni.pa165.dto.userservice.UserUpdateDto;
import cz.fi.muni.pa165.dto.userservice.UserViewDto;

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
