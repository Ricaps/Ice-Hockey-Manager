package cz.fi.muni.pa165.userservice.business.facades;

import cz.fi.muni.pa165.dto.userService.ChangePasswordRequestDto;
import cz.fi.muni.pa165.dto.userService.UserCreateDto;
import cz.fi.muni.pa165.dto.userService.UserViewDto;
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

	public UserViewDto updateUser(UserViewDto userViewDto) {
		final User user = userService.updateUser(userMapper.userViewDtoToUser(userViewDto));
		return userMapper.userToUserViewDto(user);
	}

	public UserViewDto changePassword(ChangePasswordRequestDto changePasswordRequestDto) {
		final User user = userService.changePassword(changePasswordRequestDto.getUserId(),
				changePasswordRequestDto.getOldPassword(), changePasswordRequestDto.getNewPassword());

		return userMapper.userToUserViewDto(user);
	}

	public UserViewDto addRoleToUser(UUID userId, UUID roleId) {
		final User user = userService.addRoleToUser(userId, roleId);
		return userMapper.userToUserViewDto(user);
	}

	public UserViewDto deleteRoleFromUser(UUID userId, UUID roleId) {
		final User user = userService.deleteRoleFromUser(userId, roleId);
		return userMapper.userToUserViewDto(user);
	}

	public UserViewDto resetPassword(UUID userId, String newPassword) {
		final User user = userService.resetPassword(userId, newPassword);
		return userMapper.userToUserViewDto(user);
	}

	public List<UserViewDto> getAllUsers() {
		final List<User> users = userService.getAllUsers();
		return users.stream().map(userMapper::userToUserViewDto).toList();
	}

}
