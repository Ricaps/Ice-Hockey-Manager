package cz.fi.muni.pa165.userservice.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.fi.muni.pa165.dto.userService.ChangePasswordRequestDto;
import cz.fi.muni.pa165.dto.userService.UserCreateDto;
import cz.fi.muni.pa165.dto.userService.UserViewDto;
import cz.fi.muni.pa165.service.userService.api.api.UserController;
import cz.fi.muni.pa165.userservice.business.facades.UserFacade;
import cz.fi.muni.pa165.userservice.security.SecurityConfig;
import cz.fi.muni.pa165.userservice.testData.UserTestData;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
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

import java.util.Collections;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
@Import(SecurityConfig.class)
public class UserControllerMvcTests {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private UserFacade userFacade;

	@InjectMocks
	private UserControllerImpl userController;

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
		userViewDto.setMail("");

		mockMvc
			.perform(post("/v1/user/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(userViewDto)))
			.andExpect(status().isBadRequest());

		Mockito.verify(userFacade, Mockito.never()).registerUser(Mockito.any(UserCreateDto.class));
	}

	@Test
	void changeUserPassword_whenPasswordIsShort_shouldReturnBadRequest() throws Exception {
		ChangePasswordRequestDto requestDto = UserTestData.getChangePasswordRequestDto();
		requestDto.setNewPassword("newpswd");

		mockMvc
			.perform(put("/v1/user/change-password").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andExpect(status().isBadRequest());

		Mockito.verify(userFacade, Mockito.never()).changePassword(Mockito.any(ChangePasswordRequestDto.class));
	}

	@Test
	void changeUserPassword_whenPasswordDoesNotContainUpperCaseLetter_shouldReturnBadRequest() throws Exception {
		ChangePasswordRequestDto requestDto = UserTestData.getChangePasswordRequestDto();
		requestDto.setNewPassword("pswd123*****");

		mockMvc
			.perform(put("/v1/user/change-password").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andExpect(status().isBadRequest());

		Mockito.verify(userFacade, Mockito.never()).changePassword(Mockito.any(ChangePasswordRequestDto.class));
	}

	@Test
	void changeUserPassword_whenPasswordDoesNotContainLowerCaseLetter_shouldReturnBadRequest() throws Exception {
		ChangePasswordRequestDto requestDto = new ChangePasswordRequestDto("oldPassword", "PSWD123*****",
				userViewDto.getGuid());

		mockMvc
			.perform(put("/v1/user/change-password").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andExpect(status().isBadRequest());

		Mockito.verify(userFacade, Mockito.never()).changePassword(Mockito.any(ChangePasswordRequestDto.class));
	}

	@Test
	void changeUserPassword_whenPasswordDoesNotContainDigit_shouldReturnBadRequest() throws Exception {
		ChangePasswordRequestDto requestDto = UserTestData.getChangePasswordRequestDto();
		requestDto.setNewPassword("PSWDpswd*****");

		mockMvc
			.perform(put("/v1/user/change-password").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andExpect(status().isBadRequest());

		Mockito.verify(userFacade, Mockito.never()).changePassword(Mockito.any(ChangePasswordRequestDto.class));
	}

	@Test
	void changeUserPassword_whenPasswordDoesNotContainSpecialCharacter_shouldReturnBadRequest() throws Exception {
		ChangePasswordRequestDto requestDto = UserTestData.getChangePasswordRequestDto();
		requestDto.setNewPassword("PSWD123pswd");

		mockMvc
			.perform(put("/v1/user/change-password").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andExpect(status().isBadRequest());

		Mockito.verify(userFacade, Mockito.never()).changePassword(Mockito.any(ChangePasswordRequestDto.class));
	}

	@Test
	void changeUserPassword_whenOldPasswordDoesNotMatch_shouldReturnBadRequest() throws Exception {
		Mockito.when(userFacade.changePassword(Mockito.any(ChangePasswordRequestDto.class)))
			.thenThrow(ValidationException.class);
		ChangePasswordRequestDto requestDto = UserTestData.getChangePasswordRequestDto();

		mockMvc
			.perform(put("/v1/user/change-password").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andExpect(status().isBadRequest());

		Mockito.verify(userFacade, Mockito.times(1)).changePassword(Mockito.any(ChangePasswordRequestDto.class));
	}

	@Test
	void changeUserPassword_whenUserDoesNotExists_shouldReturnNotFound() throws Exception {
		Mockito.when(userFacade.changePassword(Mockito.any(ChangePasswordRequestDto.class)))
			.thenThrow(EntityNotFoundException.class);
		ChangePasswordRequestDto requestDto = UserTestData.getChangePasswordRequestDto();

		mockMvc
			.perform(put("/v1/user/change-password").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andExpect(status().isNotFound());

		Mockito.verify(userFacade, Mockito.times(1)).changePassword(Mockito.any(ChangePasswordRequestDto.class));
	}

	@Test
	void changeUserPassword_whenValid_shouldReturnUserViewDto() throws Exception {
		Mockito.when(userFacade.changePassword(Mockito.any(ChangePasswordRequestDto.class))).thenReturn(userViewDto);
		ChangePasswordRequestDto requestDto = UserTestData.getChangePasswordRequestDto();

		mockMvc
			.perform(put("/v1/user/change-password").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.guid").value(userViewDto.getGuid().toString()))
			.andExpect(jsonPath("$.mail").value(userViewDto.getMail()))
			.andExpect(jsonPath("$.name").value(userViewDto.getName()))
			.andExpect(jsonPath("$.birthDate").value(userViewDto.getBirthDate().toString()))
			.andExpect(jsonPath("$.surname").value(userViewDto.getSurname()))
			.andExpect(jsonPath("$.username").value(userViewDto.getUsername()))
			.andExpect(jsonPath("$.deletedAt").value(userViewDto.getDeletedAt()))
			.andExpect(jsonPath("$.isActive").value(userViewDto.getIsActive()));

		Mockito.verify(userFacade, Mockito.times(1)).changePassword(Mockito.any(ChangePasswordRequestDto.class));
	}

	@Test
	void resetUserPassword_whenUserNotFound_shouldReturnNotFound() throws Exception {
		Mockito.when(userFacade.resetPassword(Mockito.any(UUID.class), Mockito.any(String.class)))
			.thenThrow(EntityNotFoundException.class);
		String newPassword = UserTestData.getChangePasswordRequestDto().getNewPassword();

		mockMvc
			.perform(put("/v1/user/reset-password/{userId}", userId).contentType(MediaType.APPLICATION_JSON)
				.content(newPassword))
			.andExpect(status().isNotFound());

		Mockito.verify(userFacade, Mockito.times(1)).resetPassword(userId, newPassword);
	}

	@Test
	void resetUserPassword_whenNotValidPassword_shouldReturnBadRequest() throws Exception {
		Mockito.when(userFacade.resetPassword(Mockito.any(UUID.class), Mockito.any(String.class)))
			.thenThrow(ValidationException.class);
		String newPassword = "asd";

		mockMvc
			.perform(put("/v1/user/reset-password/{userId}", userId).contentType(MediaType.APPLICATION_JSON)
				.content(newPassword))
			.andExpect(status().isBadRequest());

		Mockito.verify(userFacade, Mockito.times(1)).resetPassword(userId, newPassword);
	}

	@Test
	void resetUserPassword_whenValid_shouldReturnUserViewDto() throws Exception {
		Mockito.when(userFacade.resetPassword(Mockito.any(UUID.class), Mockito.any(String.class)))
			.thenReturn(userViewDto);
		String newPassword = UserTestData.getChangePasswordRequestDto().getNewPassword();

		mockMvc
			.perform(put("/v1/user/reset-password/{userId}", userId).contentType(MediaType.APPLICATION_JSON)
				.content(newPassword))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.guid").value(userViewDto.getGuid().toString()))
			.andExpect(jsonPath("$.mail").value(userViewDto.getMail()))
			.andExpect(jsonPath("$.name").value(userViewDto.getName()))
			.andExpect(jsonPath("$.birthDate").value(userViewDto.getBirthDate().toString()))
			.andExpect(jsonPath("$.surname").value(userViewDto.getSurname()))
			.andExpect(jsonPath("$.username").value(userViewDto.getUsername()))
			.andExpect(jsonPath("$.deletedAt").value(userViewDto.getDeletedAt()))
			.andExpect(jsonPath("$.isActive").value(userViewDto.getIsActive()));

		Mockito.verify(userFacade, Mockito.times(1)).resetPassword(userId, newPassword);
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

		mockMvc.perform(get("/v1/user/all-users").contentType(MediaType.APPLICATION_JSON))
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
		Mockito.when(userFacade.updateUser(Mockito.any(UserViewDto.class))).thenReturn(userViewDto);

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

		Mockito.verify(userFacade, Mockito.times(1)).updateUser(Mockito.any(UserViewDto.class));
	}

	@Test
	void updateUser_whenUserDoesNotExist_shouldReturnNotFound() throws Exception {
		Mockito.when(userFacade.updateUser(Mockito.any(UserViewDto.class)))
			.thenThrow(new EntityNotFoundException("User not found"));

		mockMvc
			.perform(put("/v1/user/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(userViewDto)))
			.andExpect(status().isNotFound());

		Mockito.verify(userFacade, Mockito.times(1)).updateUser(Mockito.any(UserViewDto.class));
	}

	@Test
	void updateUser_whenValidationFails_shouldReturnBadRequest() throws Exception {
		userViewDto.setMail("ssss");

		mockMvc
			.perform(put("/v1/user/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(userViewDto)))
			.andExpect(status().isBadRequest());

		Mockito.verify(userFacade, Mockito.never()).updateUser(Mockito.any(UserViewDto.class));
	}

	@Test
	void addRoleToUser_whenValid_shouldReturnUpdatedUser() throws Exception {
		UUID roleId = UUID.randomUUID();
		Mockito.when(userFacade.addRoleToUser(userId, roleId)).thenReturn(userViewDto);

		mockMvc
			.perform(put("/v1/user/add-role/{userId}/{roleId}", userId, roleId).contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.guid").value(userViewDto.getGuid().toString()))
			.andExpect(jsonPath("$.mail").value(userViewDto.getMail()))
			.andExpect(jsonPath("$.name").value(userViewDto.getName()))
			.andExpect(jsonPath("$.birthDate").value(userViewDto.getBirthDate().toString()))
			.andExpect(jsonPath("$.surname").value(userViewDto.getSurname()))
			.andExpect(jsonPath("$.username").value(userViewDto.getUsername()))
			.andExpect(jsonPath("$.deletedAt").value(userViewDto.getDeletedAt()))
			.andExpect(jsonPath("$.isActive").value(userViewDto.getIsActive()))
			.andExpect(jsonPath("$.roles[0].guid").value(userViewDto.getRoles().getFirst().getGuid().toString()))
			.andExpect(jsonPath("$.roles[0].code").value(userViewDto.getRoles().getFirst().getCode()))
			.andExpect(jsonPath("$.roles[0].description").value(userViewDto.getRoles().getFirst().getDescription()))
			.andExpect(jsonPath("$.roles[0].name").value(userViewDto.getRoles().getFirst().getName()));

		Mockito.verify(userFacade, Mockito.times(1)).addRoleToUser(userId, roleId);
	}

	@Test
	void addRoleToUser_whenNotFound_shouldReturnNotFound() throws Exception {
		UUID roleId = UUID.randomUUID();
		Mockito.when(userFacade.addRoleToUser(userId, roleId)).thenThrow(EntityNotFoundException.class);

		mockMvc
			.perform(put("/v1/user/add-role/{userId}/{roleId}", userId, roleId).contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());

		Mockito.verify(userFacade, Mockito.times(1)).addRoleToUser(userId, roleId);
	}

	@Test
	void deleteRoleFromUser_whenValid_shouldReturnUpdatedUser() throws Exception {
		UUID roleId = UUID.randomUUID();
		Mockito.when(userFacade.deleteRoleFromUser(userId, roleId)).thenReturn(userViewDto);

		mockMvc
			.perform(put("/v1/user/delete-role/{userId}/{roleId}", userId, roleId)
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

		Mockito.verify(userFacade, Mockito.times(1)).deleteRoleFromUser(userId, roleId);
	}

	@Test
	void deleteRoleFromUser_whenNotFound_shouldReturnNotFound() throws Exception {
		UUID roleId = UUID.randomUUID();
		Mockito.when(userFacade.deleteRoleFromUser(userId, roleId)).thenThrow(EntityNotFoundException.class);

		mockMvc
			.perform(put("/v1/user/delete-role/{userId}/{roleId}", userId, roleId)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());

		Mockito.verify(userFacade, Mockito.times(1)).deleteRoleFromUser(userId, roleId);
	}

}
