package cz.fi.muni.pa165.userservice.unit.business.facades;

import cz.fi.muni.pa165.dto.userservice.UserCreateDto;
import cz.fi.muni.pa165.dto.userservice.UserUpdateDto;
import cz.fi.muni.pa165.dto.userservice.UserViewDto;
import cz.fi.muni.pa165.userservice.business.facades.UserFacade;
import cz.fi.muni.pa165.userservice.business.mappers.UserMapper;
import cz.fi.muni.pa165.userservice.business.services.UserService;
import cz.fi.muni.pa165.userservice.persistence.entities.User;
import org.junit.jupiter.api.Assertions;
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
	public void getUserById_whenUserFound_shouldReceiveCorrectDto() {
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
	public void getUserByMail_whenUserFound_shouldReceiveCorrectDto() {
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
	public void getUserByUsername_whenUserFound_shouldReceiveCorrectDto() {
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
	public void registerUser_whenValidDtoProvided_shouldReceiveCorrectDto() {
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
	public void deactivateUser_whenUserFound_shouldReceiveCorrectDto() {
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
	public void activateUser_whenUserFound_shouldReceiveCorrectDto() {
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
	public void updateUser_whenValidUser_shouldReceiveCorrectDto() {
		// Arrange
		UserUpdateDto userUpdateDto = new UserUpdateDto();
		User user = new User();
		UserViewDto updatedUserViewDto = new UserViewDto();
		Mockito.when(userMapper.userUpdateDtoToUser(userUpdateDto)).thenReturn(user);
		Mockito.when(userService.updateUser(user)).thenReturn(user);
		Mockito.when(userMapper.userToUserViewDto(user)).thenReturn(updatedUserViewDto);

		// Act
		UserViewDto result = userFacade.updateUser(userUpdateDto);

		// Assert
		assertEquals(updatedUserViewDto, result);
		Mockito.verify(userMapper, Mockito.times(1)).userUpdateDtoToUser(userUpdateDto);
		Mockito.verify(userService, Mockito.times(1)).updateUser(user);
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

	@Test
	public void setIsUserAdmin_whenValid_shouldCallService() {
		// Arrange
		UUID userId = UUID.randomUUID();
		boolean isUserAdmin = true;
		Mockito.doNothing().when(userService).setUserIsAdmin(userId, isUserAdmin);

		// Act
		userFacade.setIsUserAdmin(userId, isUserAdmin);

		// Assert
		Mockito.verify(userService, Mockito.times(1)).setUserIsAdmin(userId, isUserAdmin);
	}

	@Test
	public void isUserAdmin_whenValid_shouldCallService() {
		// Arrange
		UUID userId = UUID.randomUUID();
		Mockito.when(userService.isUserAdmin(userId)).thenReturn(true);

		// Act
		boolean isUserAdmin = userFacade.isUserAdmin(userId);

		// Assert
		Assertions.assertTrue(isUserAdmin);
		Mockito.verify(userService, Mockito.times(1)).isUserAdmin(userId);
	}

}
