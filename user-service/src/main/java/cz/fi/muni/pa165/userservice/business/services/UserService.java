package cz.fi.muni.pa165.userservice.business.services;

import cz.fi.muni.pa165.userservice.api.exception.ValidationUtil;
import cz.fi.muni.pa165.userservice.persistence.entities.Role;
import cz.fi.muni.pa165.userservice.persistence.entities.User;
import cz.fi.muni.pa165.userservice.persistence.entities.UserHasRole;
import cz.fi.muni.pa165.userservice.persistence.repositories.RoleRepository;
import cz.fi.muni.pa165.userservice.persistence.repositories.UserHasRoleRepository;
import cz.fi.muni.pa165.userservice.persistence.repositories.UserRepository;
import cz.fi.muni.pa165.util.PasswordValidationUtil;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService extends EntityServiceBase<User> {

	private final UserRepository userRepository;

	private final RoleRepository roleRepository;

	private final UserHasRoleRepository userHasRoleRepository;

	private final PasswordEncoder passwordEncoder;

	private final EntityManager entityManager;

	@Autowired
	public UserService(UserRepository userRepository, RoleRepository roleRepository,
			UserHasRoleRepository userHasRoleRepository, PasswordEncoder passwordEncoder, EntityManager entityManager) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.roleRepository = roleRepository;
		this.userHasRoleRepository = userHasRoleRepository;
		this.entityManager = entityManager;
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
		validatePassword(user.getPasswordHash());
		ValidationUtil.requireNull(user.getGuid(), "ID must be null when creating a new user!");

		user.setPasswordHash(encodePassword(user.getPasswordHash()));
		user.setIsActive(true);
		user.setDeletedAt(null);

		return userRepository.save(user);
	}

	@Transactional
	public User updateUser(@Valid User updatedUser) {
		validateUser(updatedUser);

		User storedUser = getUserById(updatedUser.getGuid());
		updatedUser.setPasswordHash(storedUser.getPasswordHash());
		updatedUser.setRoles(storedUser.getRoles());
		updatedUser.setPayments(storedUser.getPayments());

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
	public User changePassword(UUID userId, String oldPassword, String newPassword) {
		ValidationUtil.requireNotNull(userId, "You must provide user ID. Provided user id is NULL!");
		validatePassword(newPassword);

		User user = getUserById(userId);

		if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
			throw new ValidationException("Old password does not match!");
		}

		user.setPasswordHash(encodePassword(newPassword));
		return userRepository.save(user);
	}

	@Transactional
	public User addRoleToUser(UUID userId, UUID roleId) {
		validateUserAndRoleExists(userId, roleId);
		User user = getUserById(userId);
		Role role = roleRepository.findById(roleId)
			.orElseThrow(() -> new EntityNotFoundException("Role with id: %s was not found!".formatted(userId)));

		UserHasRole userHasRole = UserHasRole.builder().user(user).role(role).build();

		userHasRoleRepository.saveAndFlush(userHasRole);
		entityManager.flush();
		entityManager.refresh(user);

		return user;
	}

	@Transactional
	public User deleteRoleFromUser(UUID userId, UUID roleId) {
		validateUserAndRoleExists(userId, roleId);

		userHasRoleRepository.deleteByUserIdAndRoleId(userId, roleId);
		entityManager.flush();
		User updatedUser = getUserById(userId);
		entityManager.refresh(updatedUser);
		return getUserById(userId);
	}

	@Transactional
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	@Transactional
	public User resetPassword(UUID userId, String newPassword) {
		ValidationUtil.requireNotNull(userId, "You must provide user ID. Provided user id is NULL!");
		validatePassword(newPassword);

		User user = getUserById(userId);

		user.setPasswordHash(encodePassword(newPassword));
		return userRepository.save(user);
	}

	private void validatePassword(String password) {
		if (!PasswordValidationUtil.isPasswordValid(password)) {
			throw new ValidationException(PasswordValidationUtil.requirementDescription);
		}
	}

	private void validateUser(User user) {
		ValidationUtil.requireNotNull(user, "You must provide a valid user! Provided updatedUser is NULL!");
		ValidationUtil.requireValidEmailAddress(user.getMail(), "Invalid email address!");

		Optional<User> existingUser = userRepository.findByMail(user.getMail());
		if (areEntitiesDuplicated(user, existingUser)) {
			throw new EntityExistsException("User with given email: %s already exists!".formatted(user.getMail()));
		}

		existingUser = userRepository.findByUsername(user.getUsername());
		if (areEntitiesDuplicated(user, existingUser)) {
			throw new EntityExistsException("User with username: %s already exists!".formatted(user.getUsername()));
		}
	}

	private void validateUserAndRoleExists(UUID userId, UUID roleId) {
		ValidationUtil.requireNotNull(userId, "You must provide user ID. Provided user id is NULL!");
		ValidationUtil.requireNotNull(roleId, "You must provide role ID. Provided role id is NULL!");

		if (!userRepository.existsById(userId)) {
			throw new EntityNotFoundException("User with id: %s was not found!".formatted(userId));
		}

		if (!roleRepository.existsById(roleId)) {
			throw new EntityNotFoundException("Role with id: %s was not found!".formatted(roleId));
		}
	}

	public String encodePassword(String password) {
		return passwordEncoder.encode(password);
	}

}
