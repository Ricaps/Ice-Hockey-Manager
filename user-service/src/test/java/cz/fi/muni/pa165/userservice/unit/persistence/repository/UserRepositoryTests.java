package cz.fi.muni.pa165.userservice.unit.persistence.repository;

import cz.fi.muni.pa165.userservice.persistence.entities.User;
import cz.fi.muni.pa165.userservice.persistence.repositories.UserRepository;
import cz.fi.muni.pa165.userservice.unit.testData.UserTestData;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@DataJpaTest
public class UserRepositoryTests {

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private UserRepository repository;

	@Test
	void findByUsername_whenUserExists_ShouldReturnUser() {
		// Arrange
		Random random = new Random();
		List<User> users = UserTestData.getListOfUsers(100);
		for (User user : users) {
			entityManager.persist(user);
		}
		User desiredUser = users.get(random.nextInt(0, users.size()));
		entityManager.flush();

		// Act
		Optional<User> foundUser = repository.findByUsername(desiredUser.getUsername());

		// Assert
		Assertions.assertTrue(foundUser.isPresent());
		Assertions.assertEquals(desiredUser.getUsername(), foundUser.get().getUsername());
		Assertions.assertEquals(desiredUser.getGuid(), foundUser.get().getGuid());
	}

	@Test
	void findByUsername_whenUserDoesNotExists_ShouldReturnEmpty() {
		// Arrange
		List<User> users = UserTestData.getListOfUsers(100);
		for (User user : users) {
			entityManager.persist(user);
		}
		String desiredUsername = "HopefullyNotExistingUsername";
		entityManager.flush();

		// Act
		Optional<User> foundUser = repository.findByUsername(desiredUsername);

		// Assert
		Assertions.assertTrue(foundUser.isEmpty());
	}

	@Test
	void findByUsername_whenDbEmpty_ShouldReturnEmpty() {
		// Arrange
		String desiredUsername = "HopefullyNotExistingUsername";

		// Act
		Optional<User> foundUser = repository.findByUsername(desiredUsername);

		// Assert
		Assertions.assertTrue(foundUser.isEmpty());
	}

	@Test
	void findByMail_whenUserExists_ShouldReturnUser() {
		// Arrange
		Random random = new Random();
		List<User> users = UserTestData.getListOfUsers(100);
		for (User user : users) {
			entityManager.persist(user);
		}
		User desiredUser = users.get(random.nextInt(0, users.size()));
		entityManager.flush();

		// Act
		Optional<User> foundUser = repository.findByMail(desiredUser.getMail());

		// Assert
		Assertions.assertTrue(foundUser.isPresent());
		Assertions.assertEquals(desiredUser.getMail(), foundUser.get().getMail());
		Assertions.assertEquals(desiredUser.getGuid(), foundUser.get().getGuid());
	}

	@Test
	void findByMail_whenUserDoesNotExists_ShouldReturnEmpty() {
		// Arrange
		List<User> users = UserTestData.getListOfUsers(100);
		for (User user : users) {
			entityManager.persist(user);
		}
		String desiredMail = "HopefullyNotExistingMail";
		entityManager.flush();

		// Act
		Optional<User> foundUser = repository.findByMail(desiredMail);

		// Assert
		Assertions.assertTrue(foundUser.isEmpty());
	}

	@Test
	void findByMail_whenDbEmpty_ShouldReturnEmpty() {
		// Arrange
		String desiredMail = "HopefullyNotExistingMail";

		// Act
		Optional<User> foundUser = repository.findByMail(desiredMail);

		// Assert
		Assertions.assertTrue(foundUser.isEmpty());
	}

	@Test
	void findIsAdminByGuid_whenUserIsAdmin_ShouldReturnTrue() {
		// Arrange
		Random random = new Random();
		List<User> users = UserTestData.getListOfUsers(100);
		for (User user : users) {
			entityManager.persist(user);
		}
		List<User> admins = users.stream().filter(User::getIsAdmin).toList();
		User desiredUser = admins.get(random.nextInt(0, admins.size()));
		entityManager.flush();

		// Act
		Optional<Boolean> isAdmin = repository.findIsAdminByGuid(desiredUser.getGuid());

		// Assert
		Assertions.assertTrue(isAdmin.isPresent());
		Assertions.assertTrue(isAdmin.get());
	}

	@Test
	void findIsAdminByGuid_whenUserIsNotAdmin_ShouldReturnFalse() {
		// Arrange
		Random random = new Random();
		List<User> users = UserTestData.getListOfUsers(100);
		for (User user : users) {
			entityManager.persist(user);
		}
		List<User> notAdmins = users.stream().filter(u -> !u.getIsAdmin()).toList();
		User desiredUser = notAdmins.get(random.nextInt(0, notAdmins.size()));
		entityManager.flush();

		// Act
		Optional<Boolean> isAdmin = repository.findIsAdminByGuid(desiredUser.getGuid());

		// Assert
		Assertions.assertTrue(isAdmin.isPresent());
		Assertions.assertFalse(isAdmin.get());
	}

	@Test
	void findIsAdminByGuid_whenUserDoesNotExists_ShouldReturnFalse() {
		// Arrange
		List<User> users = UserTestData.getListOfUsers(1);
		for (User user : users) {
			entityManager.persist(user);
		}
		entityManager.flush();

		// Act
		Optional<Boolean> isAdmin = repository.findIsAdminByGuid(UUID.randomUUID());

		// Assert
		Assertions.assertTrue(isAdmin.isEmpty());
	}

	@Test
	void findIsAdminByMail_whenUserIsAdmin_ShouldReturnTrue() {
		// Arrange
		Random random = new Random();
		List<User> users = UserTestData.getListOfUsers(100);
		for (User user : users) {
			entityManager.persist(user);
		}
		List<User> admins = users.stream().filter(User::getIsAdmin).toList();
		User desiredUser = admins.get(random.nextInt(0, admins.size()));
		entityManager.flush();

		// Act
		Optional<Boolean> isAdmin = repository.findIsAdminByMail(desiredUser.getMail());

		// Assert
		Assertions.assertTrue(isAdmin.isPresent());
		Assertions.assertTrue(isAdmin.get());
	}

	@Test
	void findIsAdminByMail_whenUserIsNotAdmin_ShouldReturnFalse() {
		// Arrange
		Random random = new Random();
		List<User> users = UserTestData.getListOfUsers(100);
		for (User user : users) {
			entityManager.persist(user);
		}
		List<User> notAdmins = users.stream().filter(u -> !u.getIsAdmin()).toList();
		User desiredUser = notAdmins.get(random.nextInt(0, notAdmins.size()));
		entityManager.flush();

		// Act
		Optional<Boolean> isAdmin = repository.findIsAdminByMail(desiredUser.getMail());

		// Assert
		Assertions.assertTrue(isAdmin.isPresent());
		Assertions.assertFalse(isAdmin.get());
	}

	@Test
	void findIsAdminByMail_whenUserDoesNotExists_ShouldReturnFalse() {
		// Arrange
		List<User> users = UserTestData.getListOfUsers(1);
		for (User user : users) {
			entityManager.persist(user);
		}
		entityManager.flush();

		// Act
		Optional<Boolean> isAdmin = repository.findIsAdminByMail("hopefullyNotExistingMail@mail.com");

		// Assert
		Assertions.assertTrue(isAdmin.isEmpty());
	}

	@Test
	void updateIsAdminByGuid_whenUserIsNotAdmin_ShouldSetToTrue() {
		// Arrange
		Random random = new Random();
		List<User> users = UserTestData.getListOfUsers(100);
		for (User user : users) {
			entityManager.persist(user);
		}
		List<User> notAdmins = users.stream().filter(u -> !u.getIsAdmin()).toList();
		User desiredUser = notAdmins.get(random.nextInt(0, notAdmins.size()));
		entityManager.flush();

		// Act
		repository.updateIsAdminByGuid(desiredUser.getGuid(), true);

		// Assert
		entityManager.clear();
		User updatedUser = entityManager.find(User.class, desiredUser.getGuid());
		Assertions.assertTrue(updatedUser.getIsAdmin());
	}

	@Test
	void updateIsAdminByGuid_whenUserIsAdmin_ShouldSetToFalse() {
		// Arrange
		Random random = new Random();
		List<User> users = UserTestData.getListOfUsers(100);
		for (User user : users) {
			entityManager.persist(user);
		}
		List<User> admins = users.stream().filter(User::getIsAdmin).toList();
		User desiredUser = admins.get(random.nextInt(0, admins.size()));
		entityManager.flush();

		// Act
		repository.updateIsAdminByGuid(desiredUser.getGuid(), false);

		// Assert
		entityManager.clear();
		User updatedUser = entityManager.find(User.class, desiredUser.getGuid());
		Assertions.assertFalse(updatedUser.getIsAdmin());
	}

}
