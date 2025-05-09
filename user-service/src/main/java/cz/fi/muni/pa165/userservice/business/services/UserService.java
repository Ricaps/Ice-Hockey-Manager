package cz.fi.muni.pa165.userservice.business.services;

import cz.fi.muni.pa165.userservice.api.exception.UnauthorizedException;
import cz.fi.muni.pa165.userservice.api.exception.ValidationUtil;
import cz.fi.muni.pa165.userservice.persistence.entities.User;
import cz.fi.muni.pa165.userservice.persistence.repositories.UserRepository;
import cz.fi.muni.pa165.userservice.util.AuthUtil;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService extends EntityServiceBase<User> {

	private final UserRepository userRepository;

	private final AuthUtil authUtil;

	@Autowired
	public UserService(UserRepository userRepository, AuthUtil authUtil) {
		this.userRepository = userRepository;
		this.authUtil = authUtil;
	}

	@Transactional
	public User getUserById(UUID userId) {
		ValidationUtil.requireNotNull(userId, "You must provide a user id, provided id is NULL!");
		final var user = userRepository.findById(userId);

		return user.orElseThrow(() -> new EntityNotFoundException("User with id: %s was not found!".formatted(userId)));
	}

	@Transactional
	public User getUserByMail(String mail) {
		ValidationUtil.requireNotBlankString(mail, "You must provide a mail address!");

		var user = userRepository.findByMail(mail);
		return user.orElseThrow(
				() -> new EntityNotFoundException("User with email address: %s was not found!".formatted(mail)));
	}

	@Transactional
	public User getUserByUsername(String username) {
		ValidationUtil.requireNotBlankString(username, "You must provide a username!");

		var user = userRepository.findByUsername(username);
		return user.orElseThrow(
				() -> new EntityNotFoundException("User with username: %s was not found!".formatted(username)));
	}

	@Transactional
	public User registerUser(@Valid User user) {
		validateUser(user);
		ValidationUtil.requireValidEmailAddress(user.getMail(), "Invalid email address!");
		ValidationUtil.requireNull(user.getGuid(), "ID must be null when creating a new user!");

		user.setIsActive(true);
		user.setDeletedAt(null);
		user.setIsAdmin(false);

		return userRepository.save(user);
	}

	@Transactional
	public User updateUser(@Valid User updatedUser) {
		validateUser(updatedUser);

		User storedUser = getUserById(updatedUser.getGuid());
		updatedUser.setPayments(storedUser.getPayments());
		updatedUser.setIsAdmin(storedUser.getIsAdmin());
		updatedUser.setDeletedAt(storedUser.getDeletedAt());
		updatedUser.setIsActive(storedUser.getIsActive());
		updatedUser.setMail(storedUser.getMail());

		return userRepository.save(updatedUser);
	}

	@Transactional
	public User deactivateUser(UUID userId) {
		ValidationUtil.requireNotNull(userId, "You must provide a user id. Provided id is NULL!");
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new EntityNotFoundException("User with id: %s was not found!".formatted(userId)));

		user.setDeletedAt(LocalDateTime.now());
		user.setIsActive(false);

		return userRepository.save(user);
	}

	@Transactional
	public User activateUser(UUID userId) {
		ValidationUtil.requireNotNull(userId, "You must provide a user id. Provided id is NULL!");
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new EntityNotFoundException("User with id: %s was not found!".formatted(userId)));

		user.setDeletedAt(null);
		user.setIsActive(true);

		return userRepository.save(user);
	}

	@Transactional
	public void setUserIsAdmin(UUID userId, boolean isAdmin) {
		validateUserExists(userId);
		validateLoggedUserIsAdmin();

		userRepository.updateIsAdminByGuid(userId, isAdmin);
	}

	@Transactional
	public Boolean isUserAdmin(UUID userId) {
		validateUserExists(userId);
		Optional<Boolean> isAdmin = userRepository.findIsAdminByGuid(userId);

		return isAdmin.isPresent() && isAdmin.get();
	}

	@Transactional
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	private void validateUser(User user) {
		ValidationUtil.requireNotNull(user, "You must provide a valid user! Provided updatedUser is NULL!");

		Optional<User> existingUser = userRepository.findByUsername(user.getUsername());
		if (areEntitiesDuplicated(user, existingUser)) {
			throw new EntityExistsException("User with username: %s already exists!".formatted(user.getUsername()));
		}
	}

	private void validateUserExists(UUID userId) {
		ValidationUtil.requireNotNull(userId, "You must provide user ID. Provided user id is NULL!");

		if (!userRepository.existsById(userId)) {
			throw new EntityNotFoundException("User with id: %s was not found!".formatted(userId));
		}
	}

	private void validateLoggedUserIsAdmin() {
		if (!authUtil.isAuthenticatedUserAdmin())
			throw new UnauthorizedException("To set admin roles, you have to be admin!");
	}

}
