package cz.fi.muni.pa165.userservice.business.services;

import cz.fi.muni.pa165.userservice.api.exception.ValidationUtil;
import cz.fi.muni.pa165.userservice.persistence.entities.Role;
import cz.fi.muni.pa165.userservice.persistence.repositories.RoleRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class RoleService {

	private final RoleRepository roleRepository;

	@Autowired
	public RoleService(RoleRepository roleRepository) {
		this.roleRepository = roleRepository;
	}

	@Transactional
	public Role getRoleById(UUID id) {
		ValidationUtil.requireNotNull(id, "id cannot be null! Provide a valid id");
		final var role = roleRepository.findById(id);

		return role.orElseThrow(() -> new EntityNotFoundException("Role with id " + id + " was not found"));
	}

	@Transactional
	public List<Role> getAllRoles() {
		return roleRepository.findAll();
	}

	@Transactional
	public Role createRole(@Valid Role role) {
		validateRole(role);
		ValidationUtil.requireNull(role.getGuid(), "ID must be null when creating a new role!");

		if (roleRepository.findByCode(role.getCode()).isPresent()) {
			throw new EntityExistsException("Role with code " + role.getCode() + " already exists");
		}

		if (roleRepository.findByName(role.getName()).isPresent()) {
			throw new EntityExistsException("Role with name " + role.getName() + " already exists");
		}

		return roleRepository.save(role);
	}

	@Transactional
	public Role updateRole(@Valid Role role) {
		validateRole(role);
		ValidationUtil.requireNotNull(role.getGuid(), "ID cannot be null when updating a role!");

		if (!roleRepository.existsById(role.getGuid())) {
			throw new EntityNotFoundException("Role with id " + role.getGuid() + " was not found");
		}

		return roleRepository.save(role);
	}

	@Transactional
	public void deleteRole(UUID id) {
		ValidationUtil.requireNotNull(id, "id cannot be null! Provide a valid id");

		if (!roleRepository.existsById(id)) {
			throw new EntityNotFoundException("Role with id " + id + " was not found");
		}

		roleRepository.deleteById(id);
	}

	private void validateRole(Role role) {
		ValidationUtil.requireNotNull(role, "role cannot be null! Provide a valid role");
		ValidationUtil.requireNotBlankString(role.getName(), "Name cannot be null or empty! Provide a valid name!");
		ValidationUtil.requireNotBlankString(role.getCode(), "Code cannot be null or empty! Provide a valid code!");

		if (roleRepository.findByCode(role.getCode()).isPresent()) {
			throw new EntityExistsException("Role with code " + role.getCode() + " already exists");
		}

		if (roleRepository.findByName(role.getName()).isPresent()) {
			throw new EntityExistsException("Role with name " + role.getName() + " already exists");
		}
	}

}
