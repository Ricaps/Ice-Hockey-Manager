package cz.fi.muni.pa165.userservice.unit.persistence.repository;

import cz.fi.muni.pa165.userservice.persistence.entities.Role;
import cz.fi.muni.pa165.userservice.persistence.repositories.RoleRepository;
import cz.fi.muni.pa165.userservice.unit.testData.RoleTestData;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@DataJpaTest
public class RoleRepositoryTests {

	@Autowired
	private RoleRepository repository;

	@Autowired
	private EntityManager entityManager;

	@Test
	void findByName_whenRoleExists_ShouldReturnRole() {
		// Arrange
		Random random = new Random();
		List<Role> roles = RoleTestData.getListOfRoles(100, false);
		for (Role role : roles) {
			entityManager.persist(role);
		}
		Role desiredRole = roles.get(random.nextInt(0, roles.size()));
		entityManager.flush();

		// Act
		Optional<Role> foundRole = repository.findByName(desiredRole.getName());

		// Assert
		Assertions.assertTrue(foundRole.isPresent());
		Assertions.assertEquals(desiredRole.getName(), foundRole.get().getName());
		Assertions.assertEquals(desiredRole.getGuid(), foundRole.get().getGuid());
	}

	@Test
	void findByName_whenRoleDoesNotExists_ShouldReturnEmpty() {
		// Arrange
		List<Role> roles = RoleTestData.getListOfRoles(100, false);
		for (Role role : roles) {
			entityManager.persist(role);
		}
		String desiredName = "HopefullyNotExistingName";
		entityManager.flush();

		// Act
		Optional<Role> foundRole = repository.findByName(desiredName);

		// Assert
		Assertions.assertTrue(foundRole.isEmpty());
	}

	@Test
	void findByName_whenDbEmpty_ShouldReturnEmpty() {
		// Arrange
		String desiredName = "HopefullyNotExistingName";

		// Act
		Optional<Role> foundRole = repository.findByName(desiredName);

		// Assert
		Assertions.assertTrue(foundRole.isEmpty());
	}

	@Test
	void findByCode_whenRoleExists_ShouldReturnRole() {
		// Arrange
		Random random = new Random();
		List<Role> roles = RoleTestData.getListOfRoles(100, false);
		for (Role role : roles) {
			entityManager.persist(role);
		}
		Role desiredRole = roles.get(random.nextInt(0, roles.size()));
		entityManager.flush();

		// Act
		Optional<Role> foundRole = repository.findByCode(desiredRole.getCode());

		// Assert
		Assertions.assertTrue(foundRole.isPresent());
		Assertions.assertEquals(desiredRole.getCode(), foundRole.get().getCode());
		Assertions.assertEquals(desiredRole.getGuid(), foundRole.get().getGuid());
	}

	@Test
	void findByCode_whenRoleDoesNotExists_ShouldReturnEmpty() {
		// Arrange
		List<Role> roles = RoleTestData.getListOfRoles(100, false);
		for (Role role : roles) {
			entityManager.persist(role);
		}
		String desiredCode = "HopefullyNotExistingCode";
		entityManager.flush();

		// Act
		Optional<Role> foundRole = repository.findByCode(desiredCode);

		// Assert
		Assertions.assertTrue(foundRole.isEmpty());
	}

	@Test
	void findByCode_whenDbEmpty_ShouldReturnEmpty() {
		// Arrange
		String desiredCode = "HopefullyNotExistingCode";

		// Act
		Optional<Role> foundRole = repository.findByCode(desiredCode);

		// Assert
		Assertions.assertTrue(foundRole.isEmpty());
	}

}
