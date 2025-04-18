package cz.fi.muni.pa165.userservice.unit.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.fi.muni.pa165.dto.userService.RoleViewDto;
import cz.fi.muni.pa165.service.userService.api.RoleController;
import cz.fi.muni.pa165.userservice.api.controllers.RoleControllerImpl;
import cz.fi.muni.pa165.userservice.business.facades.RoleFacade;
import cz.fi.muni.pa165.userservice.security.SecurityConfig;
import cz.fi.muni.pa165.userservice.unit.testData.RoleTestData;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = RoleController.class)
@Import(SecurityConfig.class)
public class RoleControllerMvcTests {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private RoleFacade roleFacade;

	@InjectMocks
	private RoleControllerImpl roleController;

	@Autowired
	ObjectMapper objectMapper;

	private RoleViewDto roleViewDto;

	private UUID roleId;

	@BeforeEach
	void setUp() {
		roleId = UUID.randomUUID();
		roleViewDto = RoleTestData.getRoleViewDto();
		roleViewDto.setGuid(roleId);
	}

	@Test
	void getRoleById_whenRoleExists_shouldReturnRole() throws Exception {
		Mockito.when(roleFacade.getRoleById(roleId)).thenReturn(roleViewDto);

		mockMvc.perform(get("/v1/role/{id}", roleId).contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.guid").value(roleId.toString()))
			.andExpect(jsonPath("$.name").value(roleViewDto.getName()))
			.andExpect(jsonPath("$.code").value(roleViewDto.getCode()))
			.andExpect(jsonPath("$.description").value(roleViewDto.getDescription()));

		Mockito.verify(roleFacade, Mockito.times(1)).getRoleById(roleId);
	}

	@Test
	void getRoleById_whenRoleDoesNotExist_shouldReturnNotFound() throws Exception {
		Mockito.when(roleFacade.getRoleById(roleId)).thenThrow(EntityNotFoundException.class);

		mockMvc.perform(get("/v1/role/{id}", roleId).contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());
	}

	@Test
	void getAllRoles_whenValidRequest_shouldReturnRoles() throws Exception {
		Mockito.when(roleFacade.getAllRoles()).thenReturn(Collections.singletonList(roleViewDto));

		mockMvc.perform(get("/v1/role/").contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].guid").value(roleId.toString()))
			.andExpect(jsonPath("$[0].name").value(roleViewDto.getName()))
			.andExpect(jsonPath("$[0].code").value(roleViewDto.getCode()))
			.andExpect(jsonPath("$[0].description").value(roleViewDto.getDescription()));

		Mockito.verify(roleFacade, Mockito.times(1)).getAllRoles();
	}

	@Test
	void createRole_whenValidEntity_shouldReturnCreatedRole() throws Exception {
		Mockito.when(roleFacade.createRole(Mockito.any(RoleViewDto.class))).thenReturn(roleViewDto);

		mockMvc
			.perform(post("/v1/role/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(roleViewDto)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.guid").value(roleId.toString()))
			.andExpect(jsonPath("$.name").value(roleViewDto.getName()))
			.andExpect(jsonPath("$.code").value(roleViewDto.getCode()))
			.andExpect(jsonPath("$.description").value(roleViewDto.getDescription()));

		Mockito.verify(roleFacade, Mockito.times(1)).createRole(Mockito.any(RoleViewDto.class));
	}

	@Test
	void createRole_whenValidationFails_shouldReturnBadRequest() throws Exception {
		roleViewDto.setCode(null);

		var responseContent = mockMvc
			.perform(post("/v1/role/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(roleViewDto)))
			.andExpect(status().isBadRequest())
			.andReturn()
			.getResponse()
			.getContentAsString(StandardCharsets.UTF_8);

		Mockito.verify(roleFacade, Mockito.never()).createRole(Mockito.any(RoleViewDto.class));
	}

	@Test
	void updateRole_whenEntityIsValid_shouldReturnUpdatedRole() throws Exception {
		Mockito.when(roleFacade.updateRole(Mockito.any(RoleViewDto.class))).thenReturn(roleViewDto);

		mockMvc
			.perform(put("/v1/role/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(roleViewDto)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.guid").value(roleId.toString()))
			.andExpect(jsonPath("$.name").value(roleViewDto.getName()))
			.andExpect(jsonPath("$.code").value(roleViewDto.getCode()))
			.andExpect(jsonPath("$.description").value(roleViewDto.getDescription()));

		Mockito.verify(roleFacade, Mockito.times(1)).updateRole(Mockito.any(RoleViewDto.class));
	}

	@Test
	void updateRole_whenRoleDoesNotExist_shouldReturnNotFound() throws Exception {
		Mockito.when(roleFacade.updateRole(Mockito.any(RoleViewDto.class))).thenThrow(EntityNotFoundException.class);

		mockMvc
			.perform(put("/v1/role/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(roleViewDto)))
			.andExpect(status().isNotFound());

		Mockito.verify(roleFacade, Mockito.times(1)).updateRole(Mockito.any(RoleViewDto.class));
	}

	@Test
	void deleteRole_whenRoleIsDeleted_shouldReturnOk() throws Exception {
		Mockito.doNothing().when(roleFacade).deleteRole(roleId);

		mockMvc.perform(delete("/v1/role/{id}", roleId).contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());

		Mockito.verify(roleFacade, Mockito.times(1)).deleteRole(roleId);
	}

	@Test
	void deleteRole_whenRoleDoesNotExist_shouldReturnNotFound() throws Exception {
		Mockito.doThrow(EntityNotFoundException.class).when(roleFacade).deleteRole(roleId);

		mockMvc.perform(delete("/v1/role/{id}", roleId).contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());

		Mockito.verify(roleFacade, Mockito.times(1)).deleteRole(roleId);
	}

}
