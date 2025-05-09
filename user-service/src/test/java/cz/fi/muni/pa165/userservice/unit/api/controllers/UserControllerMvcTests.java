package cz.fi.muni.pa165.userservice.unit.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.fi.muni.pa165.dto.userService.UserCreateDto;
import cz.fi.muni.pa165.dto.userService.UserUpdateDto;
import cz.fi.muni.pa165.dto.userService.UserViewDto;
import cz.fi.muni.pa165.service.userService.api.UserController;
import cz.fi.muni.pa165.userservice.business.facades.UserFacade;
import cz.fi.muni.pa165.userservice.config.SecurityTestConfig;
import cz.fi.muni.pa165.userservice.unit.testData.UserTestData;
import cz.fi.muni.pa165.userservice.util.AuthUtil;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
@Import(SecurityTestConfig.class)
@ExtendWith(MockitoExtension.class)
public class UserControllerMvcTests {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private UserFacade userFacade;

	@MockitoBean
	private AuthUtil authUtil;

	@Autowired
	private ObjectMapper objectMapper;

	private UserViewDto userViewDto;

	private UserCreateDto userCreateDto;

	private UUID userId;

	@BeforeEach
	void setUp() {
		userId = UUID.randomUUID();
		userViewDto = UserTestData.getUserViewDto();
		userCreateDto = UserTestData.getUserCreateDto();
		userViewDto.setGuid(userId);
	}

	@Test
	void getUserById_whenUserExists_shouldReturnUser() throws Exception {
		Mockito.when(userFacade.getUserById(userId)).thenReturn(userViewDto);

		mockMvc.perform(get("/v1/user/{id}", userId).contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.guid").value(userId.toString()))
			.andExpect(jsonPath("$.mail").value(userViewDto.getMail()));

		Mockito.verify(userFacade, Mockito.times(1)).getUserById(userId);
	}

	@Test
	void getUserById_whenUserDoesNotExist_shouldReturnNotFound() throws Exception {
		Mockito.when(userFacade.getUserById(userId)).thenThrow(EntityNotFoundException.class);

		mockMvc.perform(get("/v1/user/{id}", userId).contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());

		Mockito.verify(userFacade, Mockito.times(1)).getUserById(userId);
	}

	@Test
	void getUserById_whenIdDoesNotHveUuidForm_shouldReturnBadRequest() throws Exception {
		Mockito.when(userFacade.getUserById(userId)).thenThrow(EntityNotFoundException.class);

		mockMvc.perform(get("/v1/user/{id}", "4f4c95a0-c85a-462c-b4fb-ytfytf").contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest());

		Mockito.verify(userFacade, Mockito.never()).getUserById(Mockito.any());
	}

	@Test
	void getUserByEmail_whenUserExists_shouldReturnUser() throws Exception {
		Mockito.when(userFacade.getUserByMail(userViewDto.getMail())).thenReturn(userViewDto);

		mockMvc.perform(get("/v1/user/by-email/{mail}", userViewDto.getMail()).contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.guid").value(userViewDto.getGuid().toString()))
			.andExpect(jsonPath("$.mail").value(userViewDto.getMail()))
			.andExpect(jsonPath("$.name").value(userViewDto.getName()))
			.andExpect(jsonPath("$.birthDate").value(userViewDto.getBirthDate().toString()))
			.andExpect(jsonPath("$.surname").value(userViewDto.getSurname()))
			.andExpect(jsonPath("$.username").value(userViewDto.getUsername()))
			.andExpect(jsonPath("$.deletedAt").value(userViewDto.getDeletedAt()))
			.andExpect(jsonPath("$.isActive").value(userViewDto.getIsActive()));

		Mockito.verify(userFacade, Mockito.times(1)).getUserByMail(userViewDto.getMail());
	}

	@Test
	void getUserByEmail_whenUserDoesNotExist_shouldReturnNotFound() throws Exception {
		Mockito.when(userFacade.getUserByMail(userViewDto.getMail())).thenThrow(EntityNotFoundException.class);

		mockMvc.perform(get("/v1/user/by-email/{mail}", userViewDto.getMail()).contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());

		Mockito.verify(userFacade, Mockito.times(1)).getUserByMail(userViewDto.getMail());
	}

	@Test
	void getUserByUsername_whenUserExists_shouldReturnUser() throws Exception {
		Mockito.when(userFacade.getUserByUsername(userViewDto.getUsername())).thenReturn(userViewDto);

		mockMvc
			.perform(get("/v1/user/by-username/{username}", userViewDto.getUsername())
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.guid").value(userViewDto.getGuid().toString()))
			.andExpect(jsonPath("$.mail").value(userViewDto.getMail()))
			.andExpect(jsonPath("$.name").value(userViewDto.getName()))
			.andExpect(jsonPath("$.birthDate").value(userViewDto.getBirthDate().toString()))
			.andExpect(jsonPath("$.surname").value(userViewDto.getSurname()))
			.andExpect(jsonPath("$.username").value(userViewDto.getUsername()))
			.andExpect(jsonPath("$.deletedAt").value(userViewDto.getDeletedAt()))
			.andExpect(jsonPath("$.isActive").value(userViewDto.getIsActive()));

		Mockito.verify(userFacade, Mockito.times(1)).getUserByUsername(userViewDto.getUsername());
	}

	@Test
	void getUserByUsername_whenUserDoesNotExist_shouldReturnNotFound() throws Exception {
		Mockito.when(userFacade.getUserByUsername(userViewDto.getUsername())).thenThrow(EntityNotFoundException.class);

		mockMvc
			.perform(get("/v1/user/by-username/{username}", userViewDto.getUsername())
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());

		Mockito.verify(userFacade, Mockito.times(1)).getUserByUsername(userViewDto.getUsername());
	}

	@Test
	void registerUser_whenValidUser_shouldReturnCreatedUser() throws Exception {
		Mockito.when(authUtil.getAuthMail()).thenReturn(userViewDto.getMail());

		Mockito.when(userFacade.registerUser(Mockito.any(UserCreateDto.class))).thenReturn(userViewDto);

		mockMvc
			.perform(post("/v1/user/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(userCreateDto)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.guid").value(userViewDto.getGuid().toString()))
			.andExpect(jsonPath("$.mail").value(userViewDto.getMail()))
			.andExpect(jsonPath("$.name").value(userViewDto.getName()))
			.andExpect(jsonPath("$.birthDate").value(userViewDto.getBirthDate().toString()))
			.andExpect(jsonPath("$.surname").value(userViewDto.getSurname()))
			.andExpect(jsonPath("$.username").value(userViewDto.getUsername()))
			.andExpect(jsonPath("$.deletedAt").value(userViewDto.getDeletedAt()))
			.andExpect(jsonPath("$.isActive").value(userViewDto.getIsActive()));

		Mockito.verify(userFacade, Mockito.times(1)).registerUser(Mockito.any(UserCreateDto.class));

	}

	@Test
	void registerUser_whenValidationFails_shouldReturnBadRequest() throws Exception {
		Mockito.when(authUtil.getAuthMail()).thenReturn("valid@mail.com");
		userViewDto.setUsername("");

		mockMvc
			.perform(post("/v1/user/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(userViewDto)))
			.andExpect(status().isBadRequest());

		Mockito.verify(userFacade, Mockito.never()).registerUser(Mockito.any(UserCreateDto.class));
	}

	@Test
	void deactivateUser_whenUserExists_shouldReturnDeactivatedUser() throws Exception {
		Mockito.when(userFacade.deactivateUser(userId)).thenReturn(userViewDto);

		mockMvc.perform(put("/v1/user/deactivate/{id}", userId).contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.guid").value(userViewDto.getGuid().toString()))
			.andExpect(jsonPath("$.mail").value(userViewDto.getMail()))
			.andExpect(jsonPath("$.name").value(userViewDto.getName()))
			.andExpect(jsonPath("$.birthDate").value(userViewDto.getBirthDate().toString()))
			.andExpect(jsonPath("$.surname").value(userViewDto.getSurname()))
			.andExpect(jsonPath("$.username").value(userViewDto.getUsername()))
			.andExpect(jsonPath("$.deletedAt").value(userViewDto.getDeletedAt()));

		Mockito.verify(userFacade, Mockito.times(1)).deactivateUser(userId);
	}

	@Test
	void deactivateUser_whenUserDoesNotExist_shouldReturnNotFound() throws Exception {
		Mockito.when(userFacade.deactivateUser(userId)).thenThrow(EntityNotFoundException.class);

		mockMvc.perform(put("/v1/user/deactivate/{id}", userId).contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());

		Mockito.verify(userFacade, Mockito.times(1)).deactivateUser(userId);
	}

	@Test
	void activateUser_whenUserExists_shouldReturnActivatedUser() throws Exception {
		Mockito.when(userFacade.activateUser(userId)).thenReturn(userViewDto);

		mockMvc.perform(put("/v1/user/activate/{id}", userId).contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.guid").value(userViewDto.getGuid().toString()))
			.andExpect(jsonPath("$.mail").value(userViewDto.getMail()))
			.andExpect(jsonPath("$.name").value(userViewDto.getName()))
			.andExpect(jsonPath("$.birthDate").value(userViewDto.getBirthDate().toString()))
			.andExpect(jsonPath("$.surname").value(userViewDto.getSurname()))
			.andExpect(jsonPath("$.username").value(userViewDto.getUsername()))
			.andExpect(jsonPath("$.deletedAt").value(userViewDto.getDeletedAt()));

		Mockito.verify(userFacade, Mockito.times(1)).activateUser(userId);
	}

	@Test
	void activateUser_whenUserDoesNotExist_shouldReturnNotFound() throws Exception {
		Mockito.when(userFacade.activateUser(userId)).thenThrow(EntityNotFoundException.class);

		mockMvc.perform(put("/v1/user/activate/{id}", userId).contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());

		Mockito.verify(userFacade, Mockito.times(1)).activateUser(userId);
	}

	@Test
	void getAllUsers_shouldReturnUsers() throws Exception {
		Mockito.when(userFacade.getAllUsers()).thenReturn(Collections.singletonList(userViewDto));

		mockMvc.perform(get("/v1/user/").contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].guid").value(userViewDto.getGuid().toString()))
			.andExpect(jsonPath("$[0].mail").value(userViewDto.getMail()))
			.andExpect(jsonPath("$[0].name").value(userViewDto.getName()))
			.andExpect(jsonPath("$[0].birthDate").value(userViewDto.getBirthDate().toString()))
			.andExpect(jsonPath("$[0].surname").value(userViewDto.getSurname()))
			.andExpect(jsonPath("$[0].username").value(userViewDto.getUsername()))
			.andExpect(jsonPath("$[0].deletedAt").value(userViewDto.getDeletedAt()))
			.andExpect(jsonPath("$[0].isActive").value(userViewDto.getIsActive()));

		Mockito.verify(userFacade, Mockito.times(1)).getAllUsers();
	}

	@Test
	void updateUser_whenValid_shouldReturnUpdatedUser() throws Exception {
		Mockito.when(userFacade.updateUser(Mockito.any(UserUpdateDto.class))).thenReturn(userViewDto);

		mockMvc
			.perform(put("/v1/user/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(userViewDto)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.guid").value(userViewDto.getGuid().toString()))
			.andExpect(jsonPath("$.mail").value(userViewDto.getMail()))
			.andExpect(jsonPath("$.name").value(userViewDto.getName()))
			.andExpect(jsonPath("$.birthDate").value(userViewDto.getBirthDate().toString()))
			.andExpect(jsonPath("$.surname").value(userViewDto.getSurname()))
			.andExpect(jsonPath("$.username").value(userViewDto.getUsername()))
			.andExpect(jsonPath("$.deletedAt").value(userViewDto.getDeletedAt()))
			.andExpect(jsonPath("$.isActive").value(userViewDto.getIsActive()));

		Mockito.verify(userFacade, Mockito.times(1)).updateUser(Mockito.any(UserUpdateDto.class));
	}

	@Test
	void updateUser_whenUserDoesNotExist_shouldReturnNotFound() throws Exception {
		Mockito.when(userFacade.updateUser(Mockito.any(UserUpdateDto.class)))
			.thenThrow(new EntityNotFoundException("User not found"));

		mockMvc
			.perform(put("/v1/user/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(userViewDto)))
			.andExpect(status().isNotFound());

		Mockito.verify(userFacade, Mockito.times(1)).updateUser(Mockito.any(UserUpdateDto.class));
	}

	@Test
	void updateUser_whenValidationFails_shouldReturnBadRequest() throws Exception {
		userViewDto.setUsername("");

		mockMvc
			.perform(put("/v1/user/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(userViewDto)))
			.andExpect(status().isBadRequest());

		Mockito.verify(userFacade, Mockito.never()).updateUser(Mockito.any(UserUpdateDto.class));
	}

	@Test
	void isUserAdmin_whenUserExists_shouldReturnTrue() throws Exception {
		Mockito.when(userFacade.isUserAdmin(Mockito.any(UUID.class))).thenReturn(true);

		mockMvc.perform(get("/v1/user/{id}/is-admin", UUID.randomUUID()).contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").value(true));

		Mockito.verify(userFacade, Mockito.times(1)).isUserAdmin(Mockito.any(UUID.class));
	}

	@Test
	void isUserAdmin_whenUserDoesNotExist_shouldReturnNotExists() throws Exception {
		Mockito.when(userFacade.isUserAdmin(Mockito.any(UUID.class))).thenThrow(new EntityNotFoundException());

		mockMvc.perform(get("/v1/user/{id}/is-admin", UUID.randomUUID()).contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());

		Mockito.verify(userFacade, Mockito.times(1)).isUserAdmin(Mockito.any(UUID.class));
	}

	@Test
	void setIsUserAdmin_whenUserDoesNotExist_shouldReturnNotExists() throws Exception {
		Mockito.doThrow(new EntityNotFoundException())
			.when(userFacade)
			.setIsUserAdmin(Mockito.any(UUID.class), Mockito.anyBoolean());

		mockMvc
			.perform(patch("/v1/user/{id}/is-admin", UUID.randomUUID()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(false)))
			.andExpect(status().isNotFound());

		Mockito.verify(userFacade, Mockito.times(1)).setIsUserAdmin(Mockito.any(UUID.class), Mockito.anyBoolean());
	}

	@Test
	void setIsUserAdmin_whenUserExists_shouldCallServiceMethods() throws Exception {
		Mockito.doNothing().when(userFacade).setIsUserAdmin(Mockito.any(UUID.class), Mockito.anyBoolean());

		mockMvc
			.perform(patch("/v1/user/{id}/is-admin", UUID.randomUUID()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(false)))
			.andExpect(status().isNoContent());

		Mockito.verify(userFacade, Mockito.times(1)).setIsUserAdmin(Mockito.any(UUID.class), Mockito.anyBoolean());
	}

}
