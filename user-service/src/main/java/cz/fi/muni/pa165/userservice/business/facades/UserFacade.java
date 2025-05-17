package cz.fi.muni.pa165.userservice.business.facades;

import cz.fi.muni.pa165.dto.userservice.UserCreateDto;
import cz.fi.muni.pa165.dto.userservice.UserUpdateDto;
import cz.fi.muni.pa165.dto.userservice.UserViewDto;
import cz.fi.muni.pa165.userservice.business.mappers.UserMapper;
import cz.fi.muni.pa165.userservice.business.services.UserService;
import cz.fi.muni.pa165.userservice.persistence.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserFacade {

	private final UserService userService;

	private final UserMapper userMapper;

	@Autowired
	public UserFacade(UserService userService, UserMapper userMapper) {
		this.userService = userService;
		this.userMapper = userMapper;
	}

	public UserViewDto getUserById(UUID userId) {
		final User user = userService.getUserById(userId);
		return userMapper.userToUserViewDto(user);
	}

	public UserViewDto getUserByMail(String mail) {
		final User user = userService.getUserByMail(mail);
		return userMapper.userToUserViewDto(user);
	}

	public UserViewDto getUserByUsername(String username) {
		final User user = userService.getUserByUsername(username);
		return userMapper.userToUserViewDto(user);
	}

	public UserViewDto registerUser(UserCreateDto userCreateDto) {
		final User user = userService.registerUser(userMapper.userCreateDtoToUser(userCreateDto));
		return userMapper.userToUserViewDto(user);
	}

	public UserViewDto deactivateUser(UUID userId) {
		return userMapper.userToUserViewDto(userService.deactivateUser(userId));
	}

	public UserViewDto activateUser(UUID userId) {
		return userMapper.userToUserViewDto(userService.activateUser(userId));
	}

	public UserViewDto updateUser(UserUpdateDto userUpdateDto) {
		final User user = userService.updateUser(userMapper.userUpdateDtoToUser(userUpdateDto));
		return userMapper.userToUserViewDto(user);
	}

	public void setIsUserAdmin(UUID userId, boolean isAdmin) {
		userService.setUserIsAdmin(userId, isAdmin);
	}

	public boolean isUserAdmin(UUID userId) {
		return userService.isUserAdmin(userId);
	}

	public List<UserViewDto> getAllUsers() {
		final List<User> users = userService.getAllUsers();
		return users.stream().map(userMapper::userToUserViewDto).toList();
	}

}
