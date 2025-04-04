package cz.fi.muni.pa165.service.userService.api.api;

import cz.fi.muni.pa165.dto.userService.RoleViewDto;

import java.util.List;
import java.util.UUID;

public interface RoleController {

	RoleViewDto getRoleById(UUID id);

	List<RoleViewDto> getAllRoles();

	RoleViewDto createRole(RoleViewDto roleViewDto);

	RoleViewDto updateRole(RoleViewDto roleViewDto);

	void deleteRole(UUID id);

}
