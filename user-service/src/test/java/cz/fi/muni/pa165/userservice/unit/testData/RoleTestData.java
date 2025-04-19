package cz.fi.muni.pa165.userservice.unit.testData;

import cz.fi.muni.pa165.dto.userService.RoleViewDto;
import cz.fi.muni.pa165.userservice.persistence.entities.Role;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.apache.commons.lang3.math.NumberUtils.min;

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
		return getListOfRoles(10, true);
	}

	public static List<Role> getListOfRoles(int count, boolean withGuid) {
		List<Role> roles = new ArrayList<Role>();

		if (withGuid)
			roles.add(getRole());

		for (int i = 0; i < count; i++) {
			String description = "TEST ".repeat(i);
			description = description.substring(0, min(description.length(), 255));

			roles.add(Role.builder()
				.guid(withGuid ? UUID.randomUUID() : null)
				.code("CODE %d".formatted(i))
				.description(description)
				.name("TEST - %d".formatted(i))
				.build());
		}

		return roles;
	}

}
