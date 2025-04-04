package cz.fi.muni.pa165.userservice.testData;

import cz.fi.muni.pa165.dto.userService.RoleViewDto;
import cz.fi.muni.pa165.userservice.persistence.entities.Role;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RoleTestData {

	public static Role getRole() {
		return Role.builder().guid(UUID.randomUUID()).code("code").name("nammmeeee").description("asdfasdf").build();
	}

	public static RoleViewDto getRoleViewDto() {
		return RoleViewDto.builder()
			.guid(UUID.randomUUID())
			.code("SMCODE")
			.description("this is role")
			.name("TEST")
			.build();
	}

	public static List<Role> getListOfRoles() {
		List<Role> roles = new ArrayList<Role>();
		roles.add(getRole());

		for (int i = 0; i < 10; i++) {
			roles.add(Role.builder()
				.guid(UUID.randomUUID())
				.code("CODE %d".formatted(i))
				.description("TEST ".repeat(i))
				.name("TEST - %d".formatted(i))
				.build());
		}

		return roles;
	}

}
