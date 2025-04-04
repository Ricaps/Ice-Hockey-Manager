package cz.fi.muni.pa165.userservice.business.facades;

import cz.fi.muni.pa165.dto.userService.ChangePasswordRequestDto;
import cz.fi.muni.pa165.dto.userService.UserCreateDto;
import cz.fi.muni.pa165.dto.userService.UserViewDto;
import cz.fi.muni.pa165.userservice.business.mappers.UserMapper;
import cz.fi.muni.pa165.userservice.business.services.UserService;
import cz.fi.muni.pa165.userservice.persistence.entities.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class UserFacadeTests {

	@Mock
	private UserService userService;

	@Mock
	private UserMapper userMapper;

	@InjectMocks
	private UserFacade userFacade;

	@Test
	public void testGetUserById() {
		// Arrange
		UUID userId = UUID.randomUUID();
		User user = new User();
		user.setGuid(userId);
		UserViewDto userViewDto = new UserViewDto();
		Mockito.when(userService.getUserById(userId)).thenReturn(user);
		Mockito.when(userMapper.userToUserViewDto(user)).thenReturn(userViewDto);

		// Act
		UserViewDto result = userFacade.getUserById(userId);

		// Assert
		assertEquals(userViewDto, result);
		Mockito.verify(userService, Mockito.times(1)).getUserById(userId);
		Mockito.verify(userMapper, Mockito.times(1)).userToUserViewDto(user);
	}

	@Test
	public void testGetUserByMail() {
		// Arrange
		String mail = "test@example.com";
		User user = new User();
		user.setMail(mail);
		UserViewDto userViewDto = new UserViewDto();
		Mockito.when(userService.getUserByMail(mail)).thenReturn(user);
		Mockito.when(userMapper.userToUserViewDto(user)).thenReturn(userViewDto);

		// Act
		UserViewDto result = userFacade.getUserByMail(mail);

		// Assert
		assertEquals(userViewDto, result);
		Mockito.verify(userService, Mockito.times(1)).getUserByMail(mail);
		Mockito.verify(userMapper, Mockito.times(1)).userToUserViewDto(user);
	}

	@Test
	public void testGetUserByUsername() {
		// Arrange
		String username = "testuser";
		User user = new User();
		user.setUsername(username);
		UserViewDto userViewDto = new UserViewDto();
		Mockito.when(userService.getUserByUsername(username)).thenReturn(user);
		Mockito.when(userMapper.userToUserViewDto(user)).thenReturn(userViewDto);

		// Act
		UserViewDto result = userFacade.getUserByUsername(username);

		// Assert
		assertEquals(userViewDto, result);
		Mockito.verify(userService, Mockito.times(1)).getUserByUsername(username);
		Mockito.verify(userMapper, Mockito.times(1)).userToUserViewDto(user);
	}

	@Test
	public void testRegisterUser() {
		// Arrange
		UserCreateDto userCreateDto = new UserCreateDto();
		User user = new User();
		UserViewDto userViewDto = new UserViewDto();
		Mockito.when(userMapper.userCreateDtoToUser(userCreateDto)).thenReturn(user);
		Mockito.when(userService.registerUser(user)).thenReturn(user);
		Mockito.when(userMapper.userToUserViewDto(user)).thenReturn(userViewDto);

		// Act
		UserViewDto result = userFacade.registerUser(userCreateDto);

		// Assert
		assertEquals(userViewDto, result);
		Mockito.verify(userMapper, Mockito.times(1)).userCreateDtoToUser(userCreateDto);
		Mockito.verify(userService, Mockito.times(1)).registerUser(user);
		Mockito.verify(userMapper, Mockito.times(1)).userToUserViewDto(user);
	}

	@Test
	public void testDeactivateUser() {
		// Arrange
		UUID userId = UUID.randomUUID();
		User user = new User();
		UserViewDto userViewDto = new UserViewDto();
		Mockito.when(userService.deactivateUser(userId)).thenReturn(user);
		Mockito.when(userMapper.userToUserViewDto(user)).thenReturn(userViewDto);

		// Act
		UserViewDto result = userFacade.deactivateUser(userId);

		// Assert
		assertEquals(userViewDto, result);
		Mockito.verify(userService, Mockito.times(1)).deactivateUser(userId);
		Mockito.verify(userMapper, Mockito.times(1)).userToUserViewDto(user);
	}

	@Test
	public void testActivateUser() {
		// Arrange
		UUID userId = UUID.randomUUID();
		User user = new User();
		UserViewDto userViewDto = new UserViewDto();
		Mockito.when(userService.activateUser(userId)).thenReturn(user);
		Mockito.when(userMapper.userToUserViewDto(user)).thenReturn(userViewDto);

		// Act
		UserViewDto result = userFacade.activateUser(userId);

		// Assert
		assertEquals(userViewDto, result);
		Mockito.verify(userService, Mockito.times(1)).activateUser(userId);
		Mockito.verify(userMapper, Mockito.times(1)).userToUserViewDto(user);
	}

	@Test
	public void testUpdateUser() {
		// Arrange
		UserViewDto userViewDto = new UserViewDto();
		User user = new User();
		UserViewDto updatedUserViewDto = new UserViewDto();
		Mockito.when(userMapper.userViewDtoToUser(userViewDto)).thenReturn(user);
		Mockito.when(userService.updateUser(user)).thenReturn(user);
		Mockito.when(userMapper.userToUserViewDto(user)).thenReturn(updatedUserViewDto);

		// Act
		UserViewDto result = userFacade.updateUser(userViewDto);

		// Assert
		assertEquals(updatedUserViewDto, result);
		Mockito.verify(userMapper, Mockito.times(1)).userViewDtoToUser(userViewDto);
		Mockito.verify(userService, Mockito.times(1)).updateUser(user);
		Mockito.verify(userMapper, Mockito.times(1)).userToUserViewDto(user);
	}

	@Test
	public void changePassword_whenValid_shouldReturnValidDto() {
		// Arrange
		ChangePasswordRequestDto changePasswordRequestDto = new ChangePasswordRequestDto();
		changePasswordRequestDto.setUserId(UUID.randomUUID());
		changePasswordRequestDto.setOldPassword("oldPassword");
		changePasswordRequestDto.setNewPassword("newPassword");
		User user = new User();
		UserViewDto userViewDto = new UserViewDto();
		Mockito
			.when(userService.changePassword(changePasswordRequestDto.getUserId(),
					changePasswordRequestDto.getOldPassword(), changePasswordRequestDto.getNewPassword()))
			.thenReturn(user);
		Mockito.when(userMapper.userToUserViewDto(user)).thenReturn(userViewDto);

		// Act
		UserViewDto result = userFacade.changePassword(changePasswordRequestDto);

		// Assert
		assertEquals(userViewDto, result);
		Mockito.verify(userService, Mockito.times(1))
			.changePassword(changePasswordRequestDto.getUserId(), changePasswordRequestDto.getOldPassword(),
					changePasswordRequestDto.getNewPassword());
		Mockito.verify(userMapper, Mockito.times(1)).userToUserViewDto(user);
	}

	@Test
	public void addRoleToUser_whenValid_shouldReturnValidDto() {
		// Arrange
		UUID userId = UUID.randomUUID();
		UUID roleId = UUID.randomUUID();
		User user = new User();
		UserViewDto userViewDto = new UserViewDto();
		Mockito.when(userService.addRoleToUser(userId, roleId)).thenReturn(user);
		Mockito.when(userMapper.userToUserViewDto(user)).thenReturn(userViewDto);

		// Act
		UserViewDto result = userFacade.addRoleToUser(userId, roleId);

		// Assert
		assertEquals(userViewDto, result);
		Mockito.verify(userService, Mockito.times(1)).addRoleToUser(userId, roleId);
		Mockito.verify(userMapper, Mockito.times(1)).userToUserViewDto(user);
	}

	@Test
	public void deleteRoleFromUser_whenValid_shouldReturnValidDto() {
		// Arrange
		UUID userId = UUID.randomUUID();
		UUID roleId = UUID.randomUUID();
		User user = new User();
		UserViewDto userViewDto = new UserViewDto();
		Mockito.when(userService.deleteRoleFromUser(userId, roleId)).thenReturn(user);
		Mockito.when(userMapper.userToUserViewDto(user)).thenReturn(userViewDto);

		// Act
		UserViewDto result = userFacade.deleteRoleFromUser(userId, roleId);

		// Assert
		assertEquals(userViewDto, result);
		Mockito.verify(userService, Mockito.times(1)).deleteRoleFromUser(userId, roleId);
		Mockito.verify(userMapper, Mockito.times(1)).userToUserViewDto(user);
	}

	@Test
	public void resetPassword_whenValid_shouldReturnValidDto() {
		// Arrange
		UUID userId = UUID.randomUUID();
		String newPassword = "newPassword";
		User user = new User();
		UserViewDto userViewDto = new UserViewDto();
		Mockito.when(userService.resetPassword(userId, newPassword)).thenReturn(user);
		Mockito.when(userMapper.userToUserViewDto(user)).thenReturn(userViewDto);

		// Act
		UserViewDto result = userFacade.resetPassword(userId, newPassword);

		// Assert
		assertEquals(userViewDto, result);
		Mockito.verify(userService, Mockito.times(1)).resetPassword(userId, newPassword);
		Mockito.verify(userMapper, Mockito.times(1)).userToUserViewDto(user);
	}

	@Test
	public void getAllUsers_whenValid_shouldReturnValidDto() {
		// Arrange
		User user = new User();
		UserViewDto userViewDto = new UserViewDto();
		Mockito.when(userService.getAllUsers()).thenReturn(Collections.singletonList(user));
		Mockito.when(userMapper.userToUserViewDto(user)).thenReturn(userViewDto);

		// Act
		List<UserViewDto> result = userFacade.getAllUsers();

		// Assert
		assertEquals(Collections.singletonList(userViewDto), result);
		Mockito.verify(userService, Mockito.times(1)).getAllUsers();
		Mockito.verify(userMapper, Mockito.times(1)).userToUserViewDto(user);
	}

}
