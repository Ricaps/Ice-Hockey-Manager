package cz.fi.muni.pa165.userservice.business.mappers;

import cz.fi.muni.pa165.dto.userService.RoleViewDto;
import cz.fi.muni.pa165.userservice.persistence.entities.Role;
import cz.fi.muni.pa165.userservice.persistence.entities.UserHasRole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RoleMapper {

	@Mapping(source = "role.guid", target = "guid")
	@Mapping(source = "role.name", target = "name")
	@Mapping(source = "role.description", target = "description")
	@Mapping(source = "role.code", target = "code")
	RoleViewDto userHasRoleToRoleViewDto(UserHasRole userHasRole);

	@Mapping(target = "user", ignore = true)
	@Mapping(target = "role", expression = "java(createRole(roleViewDto))")
	UserHasRole roleViewDtoToUserHasRole(RoleViewDto roleViewDto);

	RoleViewDto roleToRoleViewDto(Role role);

	List<RoleViewDto> userHasRolesToRoleViewDtos(List<UserHasRole> userHasRoles);

	Role roleViewDtoToRole(RoleViewDto roleDto);

	default Role createRole(RoleViewDto roleViewDto) {
		return Role.builder()
			.guid(roleViewDto.getGuid())
			.name(roleViewDto.getName())
			.description(roleViewDto.getDescription())
			.code(roleViewDto.getCode())
			.build();
	}

}
