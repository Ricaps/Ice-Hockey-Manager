package cz.fi.muni.pa165.userservice.integration.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import cz.fi.muni.pa165.dto.userService.RoleViewDto;
import cz.fi.muni.pa165.userservice.business.facades.RoleFacade;
import cz.fi.muni.pa165.userservice.business.mappers.RoleMapper;
import cz.fi.muni.pa165.userservice.persistence.entities.Role;
import cz.fi.muni.pa165.userservice.persistence.repositories.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class RoleControllerIT extends BaseControllerIT<RoleRepository, Role> {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	RoleMapper roleMapper;

	@Autowired
	ObjectMapper objectMapper;

	@MockitoSpyBean
	RoleFacade roleFacade;

	private final RoleRepository roleRepository;

	@Autowired
	public RoleControllerIT(RoleRepository repository) {
		super(repository);
		this.roleRepository = repository;
	}

	@Test
	void getRoleById_whenRoleExists_shouldReturnRole() throws Exception {
		var existingRole = roleMapper.roleToRoleViewDto(getExistingEntity());

		mockMvc.perform(get("/v1/role/{id}", existingRole.getGuid()).contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.guid").value(existingRole.getGuid().toString()))
			.andExpect(jsonPath("$.name").value(existingRole.getName()))
			.andExpect(jsonPath("$.code").value(existingRole.getCode()))
			.andExpect(jsonPath("$.description").value(existingRole.getDescription()));
	}

	@Test
	void getRoleById_whenRoleDoesNotExist_shouldReturnNotFound() throws Exception {
		UUID roleId = getNonExistingId();

		mockMvc.perform(get("/v1/role/{id}", roleId).contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());

		Mockito.verify(roleFacade, Mockito.times(1)).getRoleById(Mockito.eq(roleId));
	}

	@Test
	void getAllRoles_whenValidRequest_shouldReturnRoles() throws Exception {
		var roles = getMappedEntities(roleMapper::roleToRoleViewDto);

		ResultActions result = mockMvc.perform(get("/v1/role/").contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isArray())
			.andExpect(jsonPath("$.length()").value(roles.size()));

		Assertions.assertFalse(roles.isEmpty());
		List<Map<String, String>> jsonRoles = objectMapper
			.readValue(result.andReturn().getResponse().getContentAsString(), new TypeReference<>() {
			});
		for (Map<String, String> jsonRole : jsonRoles) {
			UUID roleId = UUID.fromString(jsonRole.get("guid"));
			RoleViewDto expectedRole = roles.stream()
				.filter(r -> r.getGuid().equals(roleId))
				.findFirst()
				.orElseThrow(() -> new EntityNotFoundException("Role with guid " + roleId + " not found!"));

			Assertions.assertEquals(expectedRole.getName(), jsonRole.get("name"));
			Assertions.assertEquals(expectedRole.getCode(), jsonRole.get("code"));
			Assertions.assertEquals(expectedRole.getDescription(), jsonRole.get("description"));
		}
	}

	@Test
	void createRole_whenValidEntity_shouldReturnCreatedRole() throws Exception {
		RoleViewDto roleViewDto = getValidRoleViewDto();

		mockMvc
			.perform(post("/v1/role/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(roleViewDto)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.guid").isNotEmpty())
			.andExpect(jsonPath("$.name").value(roleViewDto.getName()))
			.andExpect(jsonPath("$.code").value(roleViewDto.getCode()))
			.andExpect(jsonPath("$.description").value(roleViewDto.getDescription()));
	}

	@Test
	void createRole_whenValidationFails_shouldReturnBadRequest() throws Exception {
		var existingRole = roleMapper.roleToRoleViewDto(getExistingEntity());
		existingRole.setGuid(null);

		mockMvc
			.perform(post("/v1/role/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(existingRole)))
			.andExpect(status().isConflict())
			.andExpect(jsonPath("$").isString())
			.andExpect(jsonPath("$").isNotEmpty());
	}

	@Test
	void updateRole_whenEntityIsValid_shouldReturnUpdatedRole() throws Exception {
		var existingRole = roleMapper.roleToRoleViewDto(getExistingEntity(3));
		existingRole.setCode("RND_CODE_HERE");

		mockMvc
			.perform(put("/v1/role/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(existingRole)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.guid").value(existingRole.getGuid().toString()))
			.andExpect(jsonPath("$.name").value(existingRole.getName()))
			.andExpect(jsonPath("$.code").value(existingRole.getCode()))
			.andExpect(jsonPath("$.description").value(existingRole.getDescription()));
	}

	@Test
	void updateRole_whenRoleDoesNotExist_shouldReturnNotFound() throws Exception {
		var role = getValidRoleViewDto();
		role.setGuid(getNonExistingId());

		mockMvc
			.perform(put("/v1/role/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(role)))
			.andExpect(status().isNotFound());

		Mockito.verify(roleFacade, Mockito.times(1)).updateRole(Mockito.any());
	}

	@Test
	void deleteRole_whenRoleIsDeleted_shouldReturnOk() throws Exception {
		var allRoles = getExistingEntities();
		var role = allRoles.getLast();

		mockMvc.perform(delete("/v1/role/{id}", role.getGuid()).contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());

		Assertions.assertFalse(roleRepository.findById(role.getGuid()).isPresent());
		Assertions.assertEquals(getExistingEntities().size() + 1, allRoles.size());
	}

	@Test
	void deleteRole_whenRoleDoesNotExist_shouldReturnNotFound() throws Exception {
		var allRoles = getExistingEntities();
		var roleId = getNonExistingId();

		mockMvc.perform(delete("/v1/role/{id}", roleId).contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());

		Assertions.assertFalse(roleRepository.findById(roleId).isPresent());
		Assertions.assertEquals(getExistingEntities().size(), allRoles.size());
	}

	private RoleViewDto getValidRoleViewDto() {
		return RoleViewDto.builder().name("Admin").code("ADMN").description("Admin role").build();
	}

}
