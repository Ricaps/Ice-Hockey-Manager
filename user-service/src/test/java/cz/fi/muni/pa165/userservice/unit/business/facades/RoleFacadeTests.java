package cz.fi.muni.pa165.userservice.unit.business.facades;

import cz.fi.muni.pa165.dto.userService.RoleViewDto;
import cz.fi.muni.pa165.userservice.business.facades.RoleFacade;
import cz.fi.muni.pa165.userservice.business.mappers.RoleMapper;
import cz.fi.muni.pa165.userservice.business.services.RoleService;
import cz.fi.muni.pa165.userservice.persistence.entities.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class RoleFacadeTests {

	@Mock
	private RoleService roleService;

	@Mock
	private RoleMapper roleMapper;

	@InjectMocks
	private RoleFacade roleFacade;

	private Role role;

	private RoleViewDto roleViewDto;

	private UUID roleId;

	@BeforeEach
	void setUp() {
		roleId = UUID.randomUUID();
		role = new Role(roleId, "Admin", "Administrator role", "ADMIN", new HashSet<>());
		roleViewDto = new RoleViewDto(roleId, "Admin", "Administrator role", "ADMIN");
	}

	@Test
	void getRoleById_whenRoleExists_returnsMappedRoleViewDto() {
		// Arrange
		Mockito.when(roleService.getRoleById(roleId)).thenReturn(role);
		Mockito.when(roleMapper.roleToRoleViewDto(role)).thenReturn(roleViewDto);

		// Act
		RoleViewDto result = roleFacade.getRoleById(roleId);

		// Assert
		assertThat(result).isEqualTo(roleViewDto);
		Mockito.verify(roleService).getRoleById(roleId);
		Mockito.verify(roleMapper).roleToRoleViewDto(role);
	}

	@Test
	void getAllRoles_whenRolesExist_returnsMappedRoleViewDtoList() {
		// Arrange
		List<Role> roles = List.of(role);
		List<RoleViewDto> roleDtos = List.of(roleViewDto);

		Mockito.when(roleService.getAllRoles()).thenReturn(roles);
		Mockito.when(roleMapper.roleToRoleViewDto(role)).thenReturn(roleViewDto);

		// Act
		List<RoleViewDto> result = roleFacade.getAllRoles();

		// Assert
		assertThat(result).isEqualTo(roleDtos);
		Mockito.verify(roleService).getAllRoles();
		Mockito.verify(roleMapper, Mockito.times(roles.size())).roleToRoleViewDto(Mockito.any(Role.class));
	}

	@Test
	void createRole_whenValidRoleViewDto_createsAndReturnsMappedRoleViewDto() {
		// Arrange
		Mockito.when(roleMapper.roleViewDtoToRole(roleViewDto)).thenReturn(role);
		Mockito.when(roleService.createRole(role)).thenReturn(role);
		Mockito.when(roleMapper.roleToRoleViewDto(role)).thenReturn(roleViewDto);

		// Act
		RoleViewDto result = roleFacade.createRole(roleViewDto);

		// Assert
		assertThat(result).isEqualTo(roleViewDto);
		Mockito.verify(roleService).createRole(role);
		Mockito.verify(roleMapper).roleViewDtoToRole(roleViewDto);
		Mockito.verify(roleMapper).roleToRoleViewDto(role);
	}

	@Test
	void updateRole_whenValidRoleViewDto_updatesAndReturnsMappedRoleViewDto() {
		// Arrange
		Mockito.when(roleMapper.roleViewDtoToRole(roleViewDto)).thenReturn(role);
		Mockito.when(roleService.updateRole(role)).thenReturn(role);
		Mockito.when(roleMapper.roleToRoleViewDto(role)).thenReturn(roleViewDto);

		// Act
		RoleViewDto result = roleFacade.updateRole(roleViewDto);

		// Assert
		assertThat(result).isEqualTo(roleViewDto);
		Mockito.verify(roleService).updateRole(role);
		Mockito.verify(roleMapper).roleViewDtoToRole(roleViewDto);
		Mockito.verify(roleMapper).roleToRoleViewDto(role);
	}

	@Test
	void deleteRole_whenCalled_invokesServiceDeleteMethod() {
		// Arrange
		Mockito.doNothing().when(roleService).deleteRole(roleId);

		// Act
		roleFacade.deleteRole(roleId);

		// Assert
		Mockito.verify(roleService).deleteRole(roleId);
	}

}