package cz.fi.muni.pa165.userservice.unit.business.service;

import cz.fi.muni.pa165.userservice.api.exception.BlankValueException;
import cz.fi.muni.pa165.userservice.business.services.RoleService;
import cz.fi.muni.pa165.userservice.persistence.entities.Role;
import cz.fi.muni.pa165.userservice.persistence.repositories.RoleRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class RoleServiceTests {

	@Mock
	private RoleRepository roleRepository;

	@InjectMocks
	private RoleService roleService;

	@Test
	void createRole_whenRoleIsNull_shouldThrowBlankValueException() {
		// Act & Assert
		assertThrows(BlankValueException.class, () -> roleService.createRole(null));
		Mockito.verify(roleRepository, Mockito.never()).save(Mockito.any(Role.class));
	}

	@Test
	void createRole_whenRoleHasGuid_shouldThrowIllegalArgumentException() {
		// Arrange
		Role roleToCreate = getRole(false);

		// Act & Assert
		assertThrows(IllegalArgumentException.class, () -> roleService.createRole(roleToCreate));
		Mockito.verify(roleRepository, Mockito.never()).save(Mockito.any(Role.class));
	}

	@Test
	void createRole_whenCodeIsNull_shouldThrowBlankValueException() {
		// Arrange
		Role roleToCreate = getRole(true);
		roleToCreate.setCode(null);

		// Act & Assert
		assertThrows(BlankValueException.class, () -> roleService.createRole(roleToCreate));
		Mockito.verify(roleRepository, Mockito.never()).save(Mockito.any(Role.class));
	}

	@Test
	void createRole_whenNameIsNull_shouldThrowBlankValueException() {
		// Arrange
		Role roleToCreate = getRole(true);
		roleToCreate.setName(null);

		// Act & Assert
		assertThrows(BlankValueException.class, () -> roleService.createRole(roleToCreate));
		Mockito.verify(roleRepository, Mockito.never()).save(Mockito.any(Role.class));
	}

	@Test
	void createRole_whenNameIsEmpty_shouldThrowBlankValueException() {
		// Arrange
		Role roleToCreate = getRole(true);
		roleToCreate.setName("");

		// Act & Assert
		assertThrows(BlankValueException.class, () -> roleService.createRole(roleToCreate));
		Mockito.verify(roleRepository, Mockito.never()).save(Mockito.any(Role.class));
	}

	@Test
	void createRole_whenCodeIsEmpty_shouldThrowBlankValueException() {
		// Arrange
		Role roleToCreate = getRole(true);
		roleToCreate.setCode("");

		// Act & Assert
		assertThrows(BlankValueException.class, () -> roleService.createRole(roleToCreate));
		Mockito.verify(roleRepository, Mockito.never()).save(Mockito.any(Role.class));
	}

	@Test
	void createRole_whenRoleWithSameCodeExists_shouldThrowEntityExistsException() {
		// Arrange
		Role roleToCreate = getRole(true);
		Mockito.when(roleRepository.findByCode(Mockito.any(String.class))).thenReturn(Optional.of(getRole(true)));

		// Act & Assert
		assertThrows(EntityExistsException.class, () -> roleService.createRole(roleToCreate));
		Mockito.verify(roleRepository, Mockito.never()).save(Mockito.any(Role.class));
	}

	@Test
	void createRole_whenRoleWithSameNameExists_shouldThrowEntityExistsException() {
		// Arrange
		Role roleToCreate = getRole(true);
		Mockito.when(roleRepository.findByCode(Mockito.any(String.class))).thenReturn(Optional.empty());
		Mockito.when(roleRepository.findByName(Mockito.any(String.class))).thenReturn(Optional.of(getRole(true)));

		// Act & Assert
		assertThrows(EntityExistsException.class, () -> roleService.createRole(roleToCreate));
		Mockito.verify(roleRepository, Mockito.never()).save(Mockito.any(Role.class));
	}

	@Test
	void createRole_whenValidRoleIsGiven_shouldSaveRole() {
		// Arrange
		Role roleToCreate = getRole(true);
		Mockito.when(roleRepository.save(Mockito.any(Role.class))).thenReturn(roleToCreate);

		// Act
		Role role = roleService.createRole(roleToCreate);

		// Assert
		Mockito.verify(roleRepository, Mockito.times(1)).save(Mockito.any(Role.class));
	}

	@Test
	void updateRole_whenRoleIsNull_shouldThrowBlankValueException() {
		// Act & Assert
		assertThrows(BlankValueException.class, () -> roleService.updateRole(null));
		Mockito.verify(roleRepository, Mockito.never()).save(Mockito.any(Role.class));
	}

	@Test
	void updateRole_whenRoleGuidIsNull_shouldThrowBlankValueException() {
		// Arrange
		Role roleToUpdate = getRole(true);

		// Act & Assert
		assertThrows(BlankValueException.class, () -> roleService.updateRole(roleToUpdate));
		Mockito.verify(roleRepository, Mockito.never()).save(Mockito.any(Role.class));
	}

	@Test
	void updateRole_whenCodeIsNull_shouldThrowBlankValueException() {
		// Arrange
		Role roleToUpdate = getRole(false);
		roleToUpdate.setCode(null);

		// Act & Assert
		assertThrows(BlankValueException.class, () -> roleService.updateRole(roleToUpdate));
		Mockito.verify(roleRepository, Mockito.never()).save(Mockito.any(Role.class));
	}

	@Test
	void updateRole_whenNameIsNull_shouldThrowBlankValueException() {
		// Arrange
		Role roleToUpdate = getRole(false);
		roleToUpdate.setName(null);

		// Act & Assert
		assertThrows(BlankValueException.class, () -> roleService.updateRole(roleToUpdate));
		Mockito.verify(roleRepository, Mockito.never()).save(Mockito.any(Role.class));
	}

	@Test
	void updateRole_whenNameIsEmpty_shouldThrowBlankValueException() {
		// Arrange
		Role roleToUpdate = getRole(false);
		roleToUpdate.setName("");

		// Act & Assert
		assertThrows(BlankValueException.class, () -> roleService.updateRole(roleToUpdate));
		Mockito.verify(roleRepository, Mockito.never()).save(Mockito.any(Role.class));
	}

	@Test
	void updateRole_whenCodeIsEmpty_shouldThrowBlankValueException() {
		// Arrange
		Role roleToUpdate = getRole(false);
		roleToUpdate.setCode("");

		// Act & Assert
		assertThrows(BlankValueException.class, () -> roleService.updateRole(roleToUpdate));
		Mockito.verify(roleRepository, Mockito.never()).save(Mockito.any(Role.class));
	}

	@Test
	void updateRole_whenRoleWithSameCodeExists_shouldThrowEntityExistsException() {
		// Arrange
		Role roleToUpdate = getRole(false);
		Mockito.when(roleRepository.findByCode(Mockito.any(String.class))).thenReturn(Optional.of(getRole(true)));

		// Act & Assert
		assertThrows(EntityExistsException.class, () -> roleService.updateRole(roleToUpdate));
		Mockito.verify(roleRepository, Mockito.never()).save(Mockito.any(Role.class));
	}

	@Test
	void updateRole_whenRoleWithSameNameExists_shouldThrowEntityExistsException() {
		// Arrange
		Role roleToUpdate = getRole(false);
		Mockito.when(roleRepository.findByCode(Mockito.any(String.class))).thenReturn(Optional.empty());
		Mockito.when(roleRepository.findByName(Mockito.any(String.class))).thenReturn(Optional.of(getRole(true)));

		// Act & Assert
		assertThrows(EntityExistsException.class, () -> roleService.updateRole(roleToUpdate));
		Mockito.verify(roleRepository, Mockito.never()).save(Mockito.any(Role.class));
	}

	@Test
	void updateRole_whenValidRoleIsGiven_shouldSaveRole() {
		// Arrange
		Role roleToUpdate = getRole(false);
		Mockito.when(roleRepository.save(Mockito.any(Role.class))).thenReturn(roleToUpdate);
		Mockito.when(roleRepository.findByName(Mockito.any(String.class))).thenReturn(Optional.empty());
		Mockito.when(roleRepository.findByCode(Mockito.any(String.class))).thenReturn(Optional.empty());
		Mockito.when(roleRepository.existsById(Mockito.any(UUID.class))).thenReturn(true);

		// Act
		Role role = roleService.updateRole(roleToUpdate);

		// Assert
		Mockito.verify(roleRepository, Mockito.times(1)).save(Mockito.any(Role.class));
	}

	@Test
	void getRoleById_whenIdIsNull_shouldThrowBlankValueException() {
		// Act & Assert
		assertThrows(BlankValueException.class, () -> roleService.getRoleById(null));
	}

	@Test
	void getRoleById_whenRoleIsNotFound_shouldThrowEntityNotFoundException() {
		// Arrange
		Mockito.when(roleRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.empty());

		// Act & Assert
		assertThrows(EntityNotFoundException.class, () -> roleService.getRoleById(UUID.randomUUID()));
	}

	@Test
	void getRoleById_whenRoleIsFound_shouldReturnRole() {
		// Arrange
		Mockito.when(roleRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(getRole(false)));

		// Act
		roleService.getRoleById(UUID.randomUUID());

		// Assert
		Mockito.verify(roleRepository, Mockito.times(1)).findById(Mockito.any(UUID.class));
	}

	@Test
	void getAllRoles_whenRolesAreFound_shouldReturnRoles() {
		// Arrange
		Mockito.when(roleRepository.findAll()).thenReturn(List.of(getRole(false)));

		// Act
		Collection<Role> roles = roleService.getAllRoles();

		// Assert
		Mockito.verify(roleRepository, Mockito.times(1)).findAll();
		assertNotNull(roles);
		assertFalse(roles.isEmpty());
	}

	private Role getRole(boolean emptyGuid) {
		return Role.builder()
			.guid(emptyGuid ? null : UUID.randomUUID())
			.code("ROLE")
			.name("TEST ROLE")
			.description("this is description")
			.build();
	}

}
