package cz.fi.muni.pa165.service.userService.api;

import cz.fi.muni.pa165.dto.userService.ChangePasswordRequestDto;
import cz.fi.muni.pa165.dto.userService.UserCreateDto;
import cz.fi.muni.pa165.dto.userService.UserLoginDto;
import cz.fi.muni.pa165.dto.userService.UserViewDto;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

public interface UserController {

	UserViewDto getUserById(UUID id);

	UserViewDto getUserByEmail(String email);

	UserViewDto getUserByUsername(String username);

	UserViewDto registerUser(UserCreateDto userCreateDto);

	UserViewDto changeUserPassword(ChangePasswordRequestDto changePasswordRequestDto);

	UserViewDto resetUserPassword(UUID userId, String password);

	UserViewDto updateUser(UserViewDto userViewDto);

	UserViewDto deactivateUser(UUID id);

	UserViewDto activateUser(UUID id);

	List<UserViewDto> getAllUsers();

	UserViewDto addRoleToUser(UUID userId, UUID roleId);

	UserViewDto deleteRoleFromUser(UUID userId, UUID roleId);

}
