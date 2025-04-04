package cz.fi.muni.pa165.userservice.business.facades;

import cz.fi.muni.pa165.dto.userService.RoleViewDto;
import cz.fi.muni.pa165.userservice.business.mappers.RoleMapper;
import cz.fi.muni.pa165.userservice.business.services.RoleService;
import cz.fi.muni.pa165.userservice.persistence.entities.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RoleFacade {

	private final RoleService roleService;

	private final RoleMapper roleMapper;

	@Autowired
	public RoleFacade(RoleService roleService, RoleMapper roleMapper) {
		this.roleService = roleService;
		this.roleMapper = roleMapper;
	}

	public RoleViewDto getRoleById(UUID id) {
		final Role role = roleService.getRoleById(id);
		return roleMapper.roleToRoleViewDto(role);
	}

	public List<RoleViewDto> getAllRoles() {
		return roleService.getAllRoles().stream().map(roleMapper::roleToRoleViewDto).collect(Collectors.toList());
	}

	public RoleViewDto createRole(RoleViewDto roleViewDto) {
		final Role role = roleMapper.roleViewDtoToRole(roleViewDto);
		return roleMapper.roleToRoleViewDto(roleService.createRole(role));
	}

	public RoleViewDto updateRole(RoleViewDto roleViewDto) {
		final Role role = roleMapper.roleViewDtoToRole(roleViewDto);
		return roleMapper.roleToRoleViewDto(roleService.updateRole(role));
	}

	public void deleteRole(UUID id) {
		roleService.deleteRole(id);
	}

}
