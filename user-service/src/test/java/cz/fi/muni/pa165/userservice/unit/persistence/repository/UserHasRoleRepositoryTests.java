package cz.fi.muni.pa165.userservice.unit.persistence.repository;

import cz.fi.muni.pa165.userservice.persistence.entities.Role;
import cz.fi.muni.pa165.userservice.persistence.entities.User;
import cz.fi.muni.pa165.userservice.persistence.entities.UserHasRole;
import cz.fi.muni.pa165.userservice.persistence.repositories.RoleRepository;
import cz.fi.muni.pa165.userservice.persistence.repositories.UserHasRoleRepository;
import cz.fi.muni.pa165.userservice.unit.testData.RoleTestData;
import cz.fi.muni.pa165.userservice.unit.testData.UserTestData;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

@DataJpaTest
public class UserHasRoleRepositoryTests {

	@Autowired
	private UserHasRoleRepository userHasRoleRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	EntityManager entityManager;

	@Test
	void deleteByUserIdAndRoleId_whenUserHasRole_ShouldDelete() {
		// Arrange
		Random random = new Random();
		List<UserHasRole> userHasRoles = seedData();
		UserHasRole userHasRole = userHasRoles.get(random.nextInt(userHasRoles.size()));
		int assignedRoleCountBefore = userHasRoles.size();

		// Act
		int deleted = userHasRoleRepository.deleteByUserIdAndRoleId(userHasRole.getUser().getGuid(),
				userHasRole.getRole().getGuid());

		// Assert
		Assertions.assertEquals(1, deleted);
		Assertions.assertEquals(assignedRoleCountBefore - 1, userHasRoleRepository.count());
	}

	@Test
	void deleteByUserIdAndRoleId_whenUserDoesNotHaveRole_ShouldDeleteNothing() {
		// Arrange
		List<UserHasRole> userHasRoles = seedData();
		UserHasRole userHasRole = userHasRoles.getFirst();
		List<String> userRoles = userHasRoles.stream()
			.filter(ur -> ur.getUser().getGuid().equals(userHasRole.getUser().getGuid()))
			.map(ur -> ur.getRole().getGuid().toString())
			.toList();
		UUID nonAssignedRoleId = roleRepository.findAll()
			.stream()
			.filter(r -> !userRoles.contains(r.getGuid().toString()))
			.findFirst()
			.orElseThrow()
			.getGuid();

		int assignedRoleCountBefore = userHasRoles.size();

		// Act
		int deleted = userHasRoleRepository.deleteByUserIdAndRoleId(userHasRole.getUser().getGuid(), nonAssignedRoleId);

		// Assert
		Assertions.assertEquals(0, deleted);
		Assertions.assertEquals(assignedRoleCountBefore, userHasRoleRepository.count());
	}

	@Test
	void deleteByUserIdAndRoleId_whenRoleDoesNotExists_ShouldDeleteNothing() {
		// Arrange
		List<UserHasRole> userHasRoles = seedData();
		UUID nonExistingRoleId = UUID.randomUUID();
		int assignedRoleCountBefore = userHasRoles.size();

		// Act
		int deleted = userHasRoleRepository.deleteByUserIdAndRoleId(userHasRoles.getFirst().getUser().getGuid(),
				nonExistingRoleId);

		// Assert
		Assertions.assertEquals(0, deleted);
		Assertions.assertEquals(assignedRoleCountBefore, userHasRoleRepository.count());
	}

	@Test
	void deleteByUserIdAndRoleId_whenUserDoesNotExists_ShouldDeleteNothing() {
		// Arrange
		List<UserHasRole> userHasRoles = seedData();
		UUID nonExistingUserId = UUID.randomUUID();

		int assignedRoleCountBefore = userHasRoles.size();

		// Act
		int deleted = userHasRoleRepository.deleteByUserIdAndRoleId(nonExistingUserId,
				userHasRoles.getFirst().getRole().getGuid());

		// Assert
		Assertions.assertEquals(0, deleted);
		Assertions.assertEquals(assignedRoleCountBefore, userHasRoleRepository.count());
	}

	@Test
	void deleteByUserIdAndRoleId_whenDbEmpty_ShouldDeleteNothing() {
		// Act
		int deleted = userHasRoleRepository.deleteByUserIdAndRoleId(UUID.randomUUID(), UUID.randomUUID());

		// Assert
		Assertions.assertEquals(0, deleted);
		Assertions.assertEquals(0, userHasRoleRepository.count());
	}

	private List<UserHasRole> seedData() {
		List<UserHasRole> userHasRoles = new ArrayList<>();

		List<Role> roles = RoleTestData.getListOfRoles(5, false);
		for (Role role : roles) {
			entityManager.persist(role);
		}

		List<User> users = UserTestData.getListOfUsers(50);
		for (User user : users) {
			entityManager.persist(user);
		}

		Random random = new Random(123L);
		for (User user : users) {
			int rolesToAssign = random.nextInt(0, roles.size() - 1);
			Set<Role> assignedRoles = new HashSet<>();

			while (assignedRoles.size() < rolesToAssign) {
				Role randomRole = roles.get(random.nextInt(roles.size()));
				if (assignedRoles.add(randomRole)) {
					UserHasRole userHasRole = new UserHasRole();
					userHasRole.setUser(user);
					userHasRole.setRole(randomRole);

					userHasRoles.add(userHasRole);
					entityManager.persist(userHasRole);
				}
			}
		}

		return userHasRoles;
	}

}
