package cz.fi.muni.pa165.userservice.business.mappers;

import cz.fi.muni.pa165.dto.userService.RoleViewDto;
import cz.fi.muni.pa165.userservice.persistence.entities.Role;
import cz.fi.muni.pa165.userservice.persistence.entities.UserHasRole;
import cz.fi.muni.pa165.userservice.testData.RoleTestData;
import cz.fi.muni.pa165.userservice.testData.UserTestData;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RoleMapperTests {

	RoleMapper mapper = new RoleMapperImpl();

	@Test
	void mapRoleToRoleViewDto() {
		// Arrange
		Role role = RoleTestData.getRole();

		// Act
		RoleViewDto viewDto = mapper.roleToRoleViewDto(role);

		// Assert
		assertEquals(role.getGuid(), viewDto.getGuid());
		assertEquals(role.getCode(), viewDto.getCode());
		assertEquals(role.getName(), viewDto.getName());
		assertEquals(role.getDescription(), viewDto.getDescription());
	}

	@Test
	void mapRoleViewDtoToRole() {
		// Arrange
		RoleViewDto viewDto = RoleTestData.getRoleViewDto();

		// Act
		Role role = mapper.roleViewDtoToRole(viewDto);

		// Assert
		assertEquals(role.getGuid(), viewDto.getGuid());
		assertEquals(role.getCode(), viewDto.getCode());
		assertEquals(role.getName(), viewDto.getName());
		assertEquals(role.getDescription(), viewDto.getDescription());
	}

	@Test
	void mapUserHasRoleToRoleViewDto() {
		// Arrange
		UserHasRole userHasRole = UserHasRole.builder()
			.guid(UUID.randomUUID())
			.user(UserTestData.getUser())
			.role(RoleTestData.getRole())
			.build();

		// Act
		RoleViewDto viewDto = mapper.userHasRoleToRoleViewDto(userHasRole);

		// Assert
		assertEqualsUserHasRoleAndRoleViewDto(userHasRole, viewDto);
	}

	@Test
	void mapListOfUserHasRoleToListOfRoleViewDto() {
		// Arrange
		List<UserHasRole> userHasRoles = new ArrayList<>();
		for (Role role : RoleTestData.getListOfRoles()) {
			userHasRoles
				.add(UserHasRole.builder().guid(UUID.randomUUID()).role(role).user(UserTestData.getUser()).build());
		}

		// Act
		List<RoleViewDto> roles = mapper.userHasRolesToRoleViewDtos(userHasRoles);

		// Assert
		assertEquals(userHasRoles.size(), roles.size());
		for (int i = 0; i < userHasRoles.size(); i++) {
			assertEqualsUserHasRoleAndRoleViewDto(userHasRoles.get(i), roles.get(i));
		}
	}

	private void assertEqualsUserHasRoleAndRoleViewDto(UserHasRole userHasRole, RoleViewDto viewDto) {
		assertEquals(userHasRole.getRole().getGuid(), viewDto.getGuid());
		assertEquals(userHasRole.getRole().getCode(), viewDto.getCode());
		assertEquals(userHasRole.getRole().getName(), viewDto.getName());
		assertEquals(userHasRole.getRole().getDescription(), viewDto.getDescription());
	}

}
