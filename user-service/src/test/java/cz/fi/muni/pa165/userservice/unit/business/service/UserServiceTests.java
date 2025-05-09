package cz.fi.muni.pa165.userservice.unit.business.service;

import cz.fi.muni.pa165.userservice.api.exception.BlankValueException;
import cz.fi.muni.pa165.userservice.api.exception.UnauthorizedException;
import cz.fi.muni.pa165.userservice.business.services.UserService;
import cz.fi.muni.pa165.userservice.persistence.entities.User;
import cz.fi.muni.pa165.userservice.persistence.repositories.UserRepository;
import cz.fi.muni.pa165.userservice.util.AuthUtil;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

	@Mock
	private UserRepository userRepository;

	@Mock
	private AuthUtil authUtil;

	@InjectMocks
	private UserService userService;

	private UUID userId;

	@BeforeEach
	void setUp() {
		userId = UUID.randomUUID();
	}

	@Test
	void getUserById_whenUserIdIsNull_shouldThrowBlankValueException() {
		// Act & Assert
		assertThrows(BlankValueException.class, () -> userService.getUserById(null));
	}

	@Test
	void getUserById_whenUserDoesNotExist_shouldThrowEntityNotFoundException() {
		// Act & Assert
		assertThrows(EntityNotFoundException.class, () -> userService.getUserById(userId));
	}

	@Test
	void getUserById_whenUserExists_shouldReturnUser() {
		// Arrange
		User user = getUser();
		Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));

		// Act
		User returnedUser = userService.getUserById(userId);

		// Assert
		assertNotNull(returnedUser);
		assertEquals(user, returnedUser);
	}

	@Test
	void getUserByEmail_whenUserExists_shouldReturnUser() {
		// Arrange
		User user = getUser();
		Mockito.when(userRepository.findByMail(user.getMail())).thenReturn(Optional.of(user));

		// Act
		User returnedUser = userService.getUserByMail(user.getMail());

		// Assert
		assertNotNull(returnedUser);
		assertEquals(user, returnedUser);
	}

	@Test
	void getUserByEmail_whenUserNotFound_shouldThrowEntityNotFoundException() {
		// Arrange
		User user = getUser();
		Mockito.when(userRepository.findByMail(Mockito.any(String.class))).thenReturn(Optional.empty());

		// Act & Assert
		assertThrows(EntityNotFoundException.class, () -> userService.getUserByMail(user.getMail()));
	}

	@Test
	void getUserByUsername_whenUserExists_shouldReturnUser() {
		// Arrange
		User user = getUser();
		Mockito.when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

		// Act
		User returnedUser = userService.getUserByUsername(user.getUsername());

		// Assert
		assertNotNull(returnedUser);
		assertEquals(user, returnedUser);
	}

	@Test
	void getUserByUsername_whenUserNotFound_shouldThrowEntityNotFoundException() {
		// Arrange
		User user = getUser();
		Mockito.when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());

		// Act & Assert
		assertThrows(EntityNotFoundException.class, () -> userService.getUserByUsername(user.getUsername()));
	}

	@Test
	void registerUser_whenUserIsNull_shouldThrowBlankValueException() {
		// Act & Assert
		assertThrows(BlankValueException.class, () -> userService.registerUser(null));
	}

	@Test
	void registerUser_whenUserGuidIsNotNull_shouldThrowIllegalArgumentException() {
		// Arrange
		User user = getUser();

		// Act & Assert
		assertThrows(IllegalArgumentException.class, () -> userService.registerUser(user));
	}

	@Test
	void registerUser_whenValidUser_shouldRegisterSuccessfully() {
		// Arrange
		User userToRegister = getUser();
		userToRegister.setGuid(null);
		Mockito.when(userRepository.save(any(User.class))).thenReturn(userToRegister);

		// Act
		User registeredUser = userService.registerUser(userToRegister);

		// Assert
		assertNotNull(registeredUser);
		Mockito.verify(userRepository, times(1)).save(any(User.class));
	}

	@Test
	void updateUser_whenUserDoesNotExist_shouldThrowEntityNotFoundException() {
		// Arrange
		User userToUpdate = getUser();
		Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

		// Act & Assert
		assertThrows(EntityNotFoundException.class, () -> userService.updateUser(userToUpdate));
	}

	@Test
	void updateUser_whenUserGuidIsNull_shouldThrowBlankValueException() {
		// Arrange
		User userToUpdate = getUser();
		userToUpdate.setGuid(null);

		// Act & Assert
		assertThrows(BlankValueException.class, () -> userService.updateUser(userToUpdate));
	}

	@Test
	void updateUser_whenUserIsNull_shouldThrowBlankValueException() {
		// Act & Assert
		assertThrows(BlankValueException.class, () -> userService.updateUser(null));
	}

	@Test
	void updateUser_whenUserExists_shouldUpdateUser() {
		// Arrange
		User userToUpdate = getUser();
		Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(userToUpdate));
		Mockito.when(userRepository.save(any(User.class))).thenReturn(userToUpdate);

		// Act
		User updatedUser = userService.updateUser(userToUpdate);

		// Assert
		assertNotNull(updatedUser);
		Mockito.verify(userRepository, times(1)).save(any(User.class));
	}

	@Test
	void deactivateUser_whenIsNull_shouldThrowBlankValueException() {
		// Act & Assert
		assertThrows(BlankValueException.class, () -> userService.deactivateUser(null));
	}

	@Test
	void deactivateUser_whenUserDoesNotExist_shouldThrowEntityNotFoundException() {
		// Arrange
		Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

		// Act & Assert
		assertThrows(EntityNotFoundException.class, () -> userService.deactivateUser(userId));
	}

	@Test
	void deactivateUser_whenUserExists_shouldDeactivateUser() {
		// Arrange
		User userToDeactivate = getUser();
		Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(userToDeactivate));
		Mockito.when(userRepository.save(any(User.class))).thenReturn(userToDeactivate);

		// Act
		User deactivatedUser = userService.deactivateUser(userId);

		// Assert
		assertNotNull(deactivatedUser);
		Mockito.verify(userRepository, times(1)).save(any(User.class));
	}

	@Test
	void activateUser_whenIsNull_shouldThrowBlankValueException() {
		// Act & Assert
		assertThrows(BlankValueException.class, () -> userService.activateUser(null));
	}

	@Test
	void activateUser_whenUserDoesNotExist_shouldThrowEntityNotFoundException() {
		// Arrange
		Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

		// Act & Assert
		assertThrows(EntityNotFoundException.class, () -> userService.activateUser(userId));
	}

	@Test
	void activateUser_whenUserExists_shouldActivateUser() {
		// Arrange
		User userToActivate = getUser();
		Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(userToActivate));
		Mockito.when(userRepository.save(any(User.class))).thenReturn(userToActivate);

		// Act
		User activatedUser = userService.activateUser(userId);

		// Assert
		assertNotNull(activatedUser);
		Mockito.verify(userRepository, times(1)).save(any(User.class));
	}

	@Test
	void isUserAdmin_whenUserIsAdmin_shouldReturnTrue() {
		// Arrange
		Mockito.when(userRepository.findIsAdminByGuid(userId)).thenReturn(Optional.of(true));
		Mockito.when(userRepository.existsById(userId)).thenReturn(true);

		// Act
		Boolean isUserAdmin = userService.isUserAdmin(userId);

		// Assert
		Assertions.assertTrue(isUserAdmin);
		Mockito.verify(userRepository, times(1)).findIsAdminByGuid(userId);
	}

	@Test
	void isUserAdmin_whenUserIsNotAdmin_shouldReturnFalse() {
		// Arrange
		Mockito.when(userRepository.findIsAdminByGuid(userId)).thenReturn(Optional.of(false));
		Mockito.when(userRepository.existsById(userId)).thenReturn(true);

		// Act
		Boolean isUserAdmin = userService.isUserAdmin(userId);

		// Assert
		Assertions.assertFalse(isUserAdmin);
		Mockito.verify(userRepository, times(1)).findIsAdminByGuid(userId);
	}

	@Test
	void isUserAdmin_whenUserDoesNotExists_shouldThrowEntityNotFoundException() {
		// Arrange
		Mockito.when(userRepository.existsById(userId)).thenReturn(false);

		// Act
		assertThrows(EntityNotFoundException.class, () -> userService.isUserAdmin(userId));
	}

	@Test
	void setUserIsAdmin_whenUserExists_shouldCallRepositoryMethod() {
		// Arrange
		boolean shouldBeAdmin = false;
		Mockito.when(userRepository.existsById(userId)).thenReturn(true);
		Mockito.doNothing().when(userRepository).updateIsAdminByGuid(userId, shouldBeAdmin);
		Mockito.when(authUtil.isAuthenticatedUserAdmin()).thenReturn(true);

		// Act
		userService.setUserIsAdmin(userId, false);

		// Assert
		Mockito.verify(userRepository, times(1)).updateIsAdminByGuid(userId, shouldBeAdmin);
	}

	@Test
	void setUserIsAdmin_whenAuthenticatedUserIsNotAdmin_shouldThrowUnauthenticatedException() {
		// Arrange
		boolean shouldBeAdmin = false;
		Mockito.when(userRepository.existsById(userId)).thenReturn(true);
		Mockito.when(authUtil.isAuthenticatedUserAdmin()).thenReturn(false);

		// Act
		assertThrows(UnauthorizedException.class, () -> userService.setUserIsAdmin(userId, false));

		// Assert
		Mockito.verify(userRepository, times(0)).updateIsAdminByGuid(userId, shouldBeAdmin);
	}

	@Test
	void setUserIsAdmin_whenUserDoesNotExists_shouldThrowEntityNotFoundException() {
		// Arrange
		boolean shouldBeAdmin = false;
		Mockito.when(userRepository.existsById(userId)).thenReturn(false);

		// Act
		assertThrows(EntityNotFoundException.class, () -> userService.setUserIsAdmin(userId, shouldBeAdmin));

		// Assert
		Mockito.verify(userRepository, never())
			.updateIsAdminByGuid(Mockito.any(UUID.class), Mockito.any(Boolean.class));
	}

	@Test
	void getAllUsers_whenValid_shouldReturnAllUsers() {
		// Arrange
		User user = getUser();
		Mockito.when(userRepository.findAll()).thenReturn(List.of(user));

		// Act
		List<User> users = userService.getAllUsers();

		// Assert
		assertFalse(users.isEmpty());
		Mockito.verify(userRepository, times(1)).findAll();
	}

	private User getUser() {
		return User.builder()
			.guid(userId)
			.name("John")
			.surname("Unnamed")
			.mail("john@oasiudhoisdf.aa")
			.isActive(true)
			.username("asdfaf")
			.deletedAt(null)
			.isAdmin(false)
			.build();
	}

}
