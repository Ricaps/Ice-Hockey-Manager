package cz.fi.muni.pa165.userservice.unit.business.service;

import cz.fi.muni.pa165.userservice.api.exception.BlankValueException;
import cz.fi.muni.pa165.userservice.business.services.UserService;
import cz.fi.muni.pa165.userservice.persistence.entities.Role;
import cz.fi.muni.pa165.userservice.persistence.entities.User;
import cz.fi.muni.pa165.userservice.persistence.entities.UserHasRole;
import cz.fi.muni.pa165.userservice.persistence.repositories.RoleRepository;
import cz.fi.muni.pa165.userservice.persistence.repositories.UserHasRoleRepository;
import cz.fi.muni.pa165.userservice.persistence.repositories.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

	@Mock
	private UserRepository userRepository;

	@Mock
	private RoleRepository roleRepository;

	@Mock
	private UserHasRoleRepository userHasRoleRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private EntityManager entityManager;

	@InjectMocks
	private UserService userService;

	private UUID userId;

	private UUID roleId;

	@BeforeEach
	void setUp() {
		userId = UUID.randomUUID();
		roleId = UUID.randomUUID();
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
	void registerUser_whenPasswordIsShort_shouldThrowValidationException() {
		// Arrange
		User userToRegister = getUser();
		userToRegister.setPasswordHash("short");

		// Act & Assert
		assertThrows(ValidationException.class, () -> userService.registerUser(userToRegister));
	}

	@Test
	void registerUser_whenPasswordDoesNotContainDigit_shouldThrowValidationException() {
		// Arrange
		User userToRegister = getUser();
		userToRegister.setPasswordHash("shortABC****");

		// Act & Assert
		assertThrows(ValidationException.class, () -> userService.registerUser(userToRegister));
	}

	@Test
	void registerUser_whenPasswordDoesNotContainUpperCaseLetter_shouldThrowValidationException() {
		// Arrange
		User userToRegister = getUser();
		userToRegister.setPasswordHash("paassword123*");

		// Act & Assert
		assertThrows(ValidationException.class, () -> userService.registerUser(userToRegister));
	}

	@Test
	void registerUser_whenPasswordDoesNotContainLowerCaseLetter_shouldThrowValidationException() {
		// Arrange
		User userToRegister = getUser();
		userToRegister.setPasswordHash("PASSWORD123*");

		// Act & Assert
		assertThrows(ValidationException.class, () -> userService.registerUser(userToRegister));
	}

	@Test
	void registerUser_whenPasswordDoesNotContainSpecialCharacter_shouldThrowValidationException() {
		// Arrange
		User userToRegister = getUser();
		userToRegister.setPasswordHash("password123");

		// Act & Assert
		assertThrows(ValidationException.class, () -> userService.registerUser(userToRegister));
	}

	@Test
	void registerUser_whenValidUser_shouldRegisterSuccessfully() {
		// Arrange
		User userToRegister = getUser();
		userToRegister.setGuid(null);
		Mockito.when(passwordEncoder.encode(userToRegister.getPasswordHash()))
			.thenReturn(userToRegister.getPasswordHash());
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
	void changePassword_whenOldPasswordDoesNotMatch_shouldThrowValidationException() {
		// Arrange
		User user = getUser();
		String oldPassword = user.getPasswordHash() + "AA";
		String newPassword = "newPassword123*";
		Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		Mockito.when(passwordEncoder.matches(oldPassword, user.getPasswordHash())).thenReturn(false);

		// Act & Assert
		assertThrows(ValidationException.class, () -> userService.changePassword(userId, oldPassword, newPassword));
	}

	@Test
	void changePassword_whenOldPasswordMatches_shouldChangePassword() {
		// Arrange
		User user = getUser();
		String oldPassword = user.getPasswordHash();
		String newPassword = "newPassword*123";
		Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		Mockito.when(passwordEncoder.matches(Mockito.any(String.class), Mockito.any(String.class))).thenReturn(true);
		Mockito.when(passwordEncoder.encode(newPassword)).thenReturn("encodedNewPassword");
		Mockito.when(userRepository.save(any(User.class))).thenReturn(user);

		// Act
		User updatedUser = userService.changePassword(userId, oldPassword, newPassword);

		// Assert
		assertNotNull(updatedUser);
		Mockito.verify(userRepository, times(1)).save(any(User.class));
	}

	@Test
	void changePassword_whenNewPasswordIsNotValid_shouldThrowValidationException() {
		// Arrange
		User user = getUser();
		String oldPassword = user.getPasswordHash();
		String newPassword = "newPassword*123";
		Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		Mockito.when(passwordEncoder.matches(oldPassword, user.getPasswordHash())).thenReturn(false);

		// Act & Assert
		assertThrows(ValidationException.class, () -> userService.changePassword(userId, oldPassword, newPassword));
	}

	@Test
	void addRoleToUser_whenUserOrRoleDoesNotExist_shouldThrowEntityNotFoundException() {
		// Act & Assert
		assertThrows(EntityNotFoundException.class, () -> userService.addRoleToUser(userId, roleId));
	}

	@Test
	void addRoleToUser_whenValid_shouldAddRoleToUser() {
		// Arrange
		User user = getUser();
		Role role = getRole();
		Mockito.when(userRepository.existsById(userId)).thenReturn(true);
		Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user));
		Mockito.when(roleRepository.existsById(roleId)).thenReturn(true);
		Mockito.when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
		Mockito.when(userHasRoleRepository.saveAndFlush(any(UserHasRole.class))).thenReturn(null);
		Mockito.doNothing().when(entityManager).flush();
		Mockito.doNothing().when(entityManager).refresh(any(Object.class));
		Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));

		// Act
		User updatedUser = userService.addRoleToUser(userId, roleId);

		// Assert
		assertNotNull(updatedUser);
		Mockito.verify(userHasRoleRepository, times(1)).saveAndFlush(any(UserHasRole.class));
	}

	@Test
	void deleteRoleFromUser_whenUserOrRoleDoesNotExist_shouldThrowEntityNotFoundException() {
		// Act & Assert
		assertThrows(EntityNotFoundException.class, () -> userService.deleteRoleFromUser(userId, roleId));
	}

	@Test
	void deleteRoleFromUser_whenValid_shouldDeleteRoleFromUser() {
		// Arrange
		User user = getUser();
		Role role = getRole();
		Mockito.when(userRepository.existsById(userId)).thenReturn(true);
		Mockito.when(roleRepository.existsById(roleId)).thenReturn(true);
		Mockito.doNothing().when(userHasRoleRepository).deleteByUserIdAndRoleId(userId, roleId);
		Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));

		// Act
		User updatedUser = userService.deleteRoleFromUser(userId, roleId);

		// Assert
		assertNotNull(updatedUser);
		Mockito.verify(userHasRoleRepository, times(1)).deleteByUserIdAndRoleId(userId, roleId);
	}

	@Test
	void resetPassword_whenUserDoesNotExist_shouldThrowEntityNotFoundException() {
		// Arrange
		String newPassword = "newPassword123*";
		Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

		// Act & Assert
		assertThrows(EntityNotFoundException.class, () -> userService.resetPassword(userId, newPassword));
	}

	@Test
	void resetPassword_whenValid_shouldResetPassword() {
		// Arrange
		String newPassword = "123LLLaaaa@";
		User user = getUser();
		Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		Mockito.when(passwordEncoder.encode(newPassword)).thenReturn("encodedNewPassword");
		Mockito.when(userRepository.save(any(User.class))).thenReturn(user);

		// Act
		User updatedUser = userService.resetPassword(userId, newPassword);

		// Assert
		assertNotNull(updatedUser);
		Mockito.verify(userRepository, times(1)).save(any(User.class));
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
			.passwordHash("Password*123")
			.mail("john@oasiudhoisdf.aa")
			.isActive(true)
			.username("asdfaf")
			.deletedAt(null)
			.build();
	}

	private Role getRole() {
		return Role.builder().guid(roleId).description("role").code("some code").name("name").build();
	}

}
