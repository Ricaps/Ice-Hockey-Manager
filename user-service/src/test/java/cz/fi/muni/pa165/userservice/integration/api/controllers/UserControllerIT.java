package cz.fi.muni.pa165.userservice.integration.api.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.fi.muni.pa165.dto.userService.ChangePasswordRequestDto;
import cz.fi.muni.pa165.dto.userService.UserCreateDto;
import cz.fi.muni.pa165.dto.userService.UserViewDto;
import cz.fi.muni.pa165.userservice.business.facades.UserFacade;
import cz.fi.muni.pa165.userservice.business.mappers.UserMapper;
import cz.fi.muni.pa165.userservice.persistence.entities.Payment;
import cz.fi.muni.pa165.userservice.persistence.entities.Role;
import cz.fi.muni.pa165.userservice.persistence.entities.User;
import cz.fi.muni.pa165.userservice.persistence.entities.UserHasRole;
import cz.fi.muni.pa165.userservice.persistence.repositories.BudgetOfferPackageRepository;
import cz.fi.muni.pa165.userservice.persistence.repositories.PaymentRepository;
import cz.fi.muni.pa165.userservice.persistence.repositories.RoleRepository;
import cz.fi.muni.pa165.userservice.persistence.repositories.UserHasRoleRepository;
import cz.fi.muni.pa165.userservice.persistence.repositories.UserRepository;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
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
public class UserControllerIT extends BaseControllerIT<UserRepository, User> {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private BudgetOfferPackageRepository budgetOfferPackageRepository;

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private PaymentRepository paymentRepository;

	@Autowired
	private TestDataFactory testDataFactory;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private UserHasRoleRepository userHasRoleRepository;

	private final UserRepository userRepository;

	@Autowired
	@MockitoSpyBean
	private UserFacade userFacade;

	@Autowired
	public UserControllerIT(UserRepository userRepository) {
		super(userRepository);
		this.userRepository = userRepository;
	}

	@Test
	void getUserById_whenUserExists_shouldReturnUser() throws Exception {
		var existingUser = getExistingEntity();

		ResultActions result = mockMvc
			.perform(get("/v1/user/{id}", existingUser.getGuid()).contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());

		assertUserEquals(result, existingUser);
	}

	@Test
	void getUserById_whenUserDoesNotExist_shouldReturnNotFound() throws Exception {
		var nonExistingUserId = getNonExistingId();

		mockMvc.perform(get("/v1/user/{id}", nonExistingUserId).contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());

		Mockito.verify(userFacade, Mockito.times(1)).getUserById(Mockito.eq(nonExistingUserId));
	}

	@Test
	void getUserById_whenIdDoesNotHveUuidForm_shouldReturnBadRequest() throws Exception {
		mockMvc.perform(get("/v1/user/{id}", "4f4c95a0-c85a-462c-b4fb-ytfytf").contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest());
	}

	@Test
	void getUserByEmail_whenUserExists_shouldReturnUser() throws Exception {
		var existingUser = getExistingEntity(5);

		ResultActions result = mockMvc
			.perform(get("/v1/user/by-email/{mail}", existingUser.getMail()).contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());

		assertUserEquals(result, existingUser);
	}

	@Test
	void getUserByEmail_whenUserDoesNotExist_shouldReturnNotFound() throws Exception {
		var nonExistingMail = getExistingEntity(5).getMail();
		nonExistingMail += "nonExistingSuffixHopefully";

		mockMvc.perform(get("/v1/user/by-email/{mail}", nonExistingMail).contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());

		Mockito.verify(userFacade, Mockito.times(1)).getUserByMail(Mockito.eq(nonExistingMail));
	}

	@Test
	void getUserByUsername_whenUserExists_shouldReturnUser() throws Exception {
		var existingUser = getExistingEntity(20);

		ResultActions result = mockMvc
			.perform(get("/v1/user/by-username/{username}", existingUser.getUsername())
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());

		assertUserEquals(result, existingUser);
	}

	@Test
	void getUserByUsername_whenUserDoesNotExist_shouldReturnNotFound() throws Exception {
		var nonExistingUsername = getExistingEntity(36).getUsername() + "nonExistingSuffixHopefully";

		mockMvc
			.perform(
					get("/v1/user/by-username/{username}", nonExistingUsername).contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());

		Mockito.verify(userFacade, Mockito.times(1)).getUserByUsername(Mockito.eq(nonExistingUsername));
	}

	@Test
	void registerUser_whenValidUser_shouldReturnCreatedUser() throws Exception {
		var allUsersSize = getExistingEntities().size();
		var newUser = getValidUserCreateDto();

		mockMvc
			.perform(post("/v1/user/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newUser)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.guid").isNotEmpty())
			.andExpect(jsonPath("$.mail").value(newUser.getMail()))
			.andExpect(jsonPath("$.name").value(newUser.getName()))
			.andExpect(jsonPath("$.birthDate").value(newUser.getBirthDate().toString()))
			.andExpect(jsonPath("$.surname").value(newUser.getSurname()))
			.andExpect(jsonPath("$.username").value(newUser.getUsername()))
			.andExpect(jsonPath("$.deletedAt").isEmpty())
			.andExpect(jsonPath("$.isActive").value(true));

		Assertions.assertEquals(allUsersSize + 1, getExistingEntities().size());
	}

	@Test
	void registerUser_whenValidationFails_shouldReturnBadRequest() throws Exception {
		var allUsersSize = getExistingEntities().size();
		var newUser = getValidUserCreateDto();
		newUser.setPassword("abc");

		mockMvc
			.perform(post("/v1/user/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newUser)))
			.andExpect(status().isBadRequest());

		Assertions.assertEquals(allUsersSize, getExistingEntities().size());
	}

	@Test
	void changeUserPassword_whenPasswordIsShort_shouldReturnBadRequest() throws Exception {
		int index = 42;
		User userBefore = getExistingEntity(index);
		ChangePasswordRequestDto requestDto = getValidChangePasswordRequestDto(index);
		requestDto.setNewPassword("s");

		mockMvc
			.perform(put("/v1/user/change-password").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andExpect(status().isBadRequest());

		Assertions.assertEquals(userBefore.getPasswordHash(), getExistingEntity(index).getPasswordHash());
	}

	@Test
	void changeUserPassword_whenPasswordDoesNotContainUpperCaseLetter_shouldReturnBadRequest() throws Exception {
		int index = 24;
		User userBefore = getExistingEntity(index);
		ChangePasswordRequestDto requestDto = getValidChangePasswordRequestDto(index);
		requestDto.setNewPassword("pswd123*****");

		mockMvc
			.perform(put("/v1/user/change-password").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andExpect(status().isBadRequest());

		Assertions.assertEquals(userBefore.getPasswordHash(), getExistingEntity(index).getPasswordHash());
	}

	@Test
	void changeUserPassword_whenPasswordDoesNotContainLowerCaseLetter_shouldReturnBadRequest() throws Exception {
		int index = 4;
		User userBefore = getExistingEntity(index);
		ChangePasswordRequestDto requestDto = getValidChangePasswordRequestDto(index);
		requestDto.setNewPassword("PSWD123*****");

		mockMvc
			.perform(put("/v1/user/change-password").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andExpect(status().isBadRequest());

		Assertions.assertEquals(userBefore.getPasswordHash(), getExistingEntity(index).getPasswordHash());
	}

	@Test
	void changeUserPassword_whenPasswordDoesNotContainDigit_shouldReturnBadRequest() throws Exception {
		int index = 9;
		User userBefore = getExistingEntity(index);
		ChangePasswordRequestDto requestDto = getValidChangePasswordRequestDto(index);
		requestDto.setNewPassword("PSWDpswd*****");

		mockMvc
			.perform(put("/v1/user/change-password").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andExpect(status().isBadRequest());

		Assertions.assertEquals(userBefore.getPasswordHash(), getExistingEntity(index).getPasswordHash());
	}

	@Test
	void changeUserPassword_whenPasswordDoesNotContainSpecialCharacter_shouldReturnBadRequest() throws Exception {
		int index = 18;
		User userBefore = getExistingEntity(index);
		ChangePasswordRequestDto requestDto = getValidChangePasswordRequestDto(index);
		requestDto.setNewPassword("PSWD123pswd");

		mockMvc
			.perform(put("/v1/user/change-password").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andExpect(status().isBadRequest());

		Assertions.assertEquals(userBefore.getPasswordHash(), getExistingEntity(index).getPasswordHash());
	}

	@Test
	void changeUserPassword_whenOldPasswordDoesNotMatch_shouldReturnBadRequest() throws Exception {
		int index = 40;
		User userBefore = getExistingEntity(index);
		ChangePasswordRequestDto requestDto = getValidChangePasswordRequestDto(index);
		requestDto.setOldPassword("ThisPasswordFakerProbablyDidNotGenerated");

		mockMvc
			.perform(put("/v1/user/change-password").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andExpect(status().isBadRequest());

		Assertions.assertEquals(userBefore.getPasswordHash(), getExistingEntity(index).getPasswordHash());
	}

	@Test
	void changeUserPassword_whenUserDoesNotExists_shouldReturnNotFound() throws Exception {
		ChangePasswordRequestDto requestDto = getValidChangePasswordRequestDto(6);
		requestDto.setUserId(getNonExistingId());

		var r = mockMvc
			.perform(put("/v1/user/change-password").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andExpect(status().isNotFound());

		Mockito.verify(userFacade, Mockito.times(1)).changePassword(Mockito.any());
	}

	@Test
	void changeUserPassword_whenValid_shouldReturnUserViewDto() throws Exception {
		int index = 23;
		User userBefore = getExistingEntity(index);
		ChangePasswordRequestDto requestDto = getValidChangePasswordRequestDto(index);
		String hashBefore = getExistingEntity(index).getPasswordHash();

		mockMvc
			.perform(put("/v1/user/change-password").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.guid").value(userBefore.getGuid().toString()))
			.andExpect(jsonPath("$.mail").value(userBefore.getMail()))
			.andExpect(jsonPath("$.name").value(userBefore.getName()))
			.andExpect(jsonPath("$.birthDate").value(userBefore.getBirthDate().toString()))
			.andExpect(jsonPath("$.surname").value(userBefore.getSurname()))
			.andExpect(jsonPath("$.username").value(userBefore.getUsername()))
			.andExpect(jsonPath("$.deletedAt").value(userBefore.getDeletedAt()))
			.andExpect(jsonPath("$.isActive").value(userBefore.getIsActive()));

		Assertions.assertNotEquals(hashBefore, getExistingEntity(index).getPasswordHash());
	}

	@Test
	void resetUserPassword_whenUserNotFound_shouldReturnNotFound() throws Exception {
		var nonExistingId = getNonExistingId();
		var password = getValidPassword();

		mockMvc
			.perform(put("/v1/user/{userId}/password/reset", nonExistingId).contentType(MediaType.APPLICATION_JSON)
				.content(password))
			.andExpect(status().isNotFound());

		Mockito.verify(userFacade, Mockito.times(1)).resetPassword(Mockito.eq(nonExistingId), Mockito.eq(password));
	}

	@Test
	void resetUserPassword_whenNotValidPassword_shouldReturnBadRequest() throws Exception {
		int userIndex = 11;
		User existingUser = getExistingEntity(userIndex);
		var notValidPassword = "aaa";

		mockMvc
			.perform(put("/v1/user/{userId}/password/reset", existingUser.getGuid())
				.contentType(MediaType.APPLICATION_JSON)
				.content(notValidPassword))
			.andExpect(status().isBadRequest());

		Assertions.assertEquals(existingUser.getPasswordHash(), getExistingEntity(userIndex).getPasswordHash());
	}

	@Test
	void resetUserPassword_whenValid_shouldReturnUserViewDto() throws Exception {
		int index = 4;
		User userBefore = getExistingEntity(index);
		var hashBefore = getExistingEntity(index).getPasswordHash();

		mockMvc
			.perform(put("/v1/user/{userId}/password/reset", userBefore.getGuid())
				.contentType(MediaType.APPLICATION_JSON)
				.content(getValidPassword()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.guid").value(userBefore.getGuid().toString()))
			.andExpect(jsonPath("$.mail").value(userBefore.getMail()))
			.andExpect(jsonPath("$.name").value(userBefore.getName()))
			.andExpect(jsonPath("$.birthDate").value(userBefore.getBirthDate().toString()))
			.andExpect(jsonPath("$.surname").value(userBefore.getSurname()))
			.andExpect(jsonPath("$.username").value(userBefore.getUsername()))
			.andExpect(jsonPath("$.deletedAt").value(userBefore.getDeletedAt()))
			.andExpect(jsonPath("$.isActive").value(userBefore.getIsActive()));

		Assertions.assertNotEquals(hashBefore, getExistingEntity(index).getPasswordHash());
	}

	@Test
	void deactivateUser_whenUserExists_shouldReturnDeactivatedUser() throws Exception {
		User userBefore = getFirstFilteredEntity(User::getIsActive);

		mockMvc.perform(put("/v1/user/deactivate/{id}", userBefore.getGuid()).contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.guid").value(userBefore.getGuid().toString()))
			.andExpect(jsonPath("$.mail").value(userBefore.getMail()))
			.andExpect(jsonPath("$.name").value(userBefore.getName()))
			.andExpect(jsonPath("$.birthDate").value(userBefore.getBirthDate().toString()))
			.andExpect(jsonPath("$.surname").value(userBefore.getSurname()))
			.andExpect(jsonPath("$.username").value(userBefore.getUsername()))
			.andExpect(jsonPath("$.deletedAt").isNotEmpty())
			.andExpect(jsonPath("$.isActive").value(false));

		Assertions.assertFalse(userRepository.findById(userBefore.getGuid()).orElseThrow().getIsActive());
	}

	@Test
	void deactivateUser_whenUserDoesNotExist_shouldReturnNotFound() throws Exception {
		var nonExistingId = getNonExistingId();

		mockMvc.perform(put("/v1/user/deactivate/{id}", nonExistingId).contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());

		Mockito.verify(userFacade, Mockito.times(1)).deactivateUser(Mockito.eq(nonExistingId));
	}

	@Test
	void activateUser_whenUserExists_shouldReturnActivatedUser() throws Exception {
		var nonActiveUser = getFirstFilteredEntity(u -> !u.getIsActive());

		mockMvc.perform(put("/v1/user/activate/{id}", nonActiveUser.getGuid()).contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.guid").value(nonActiveUser.getGuid().toString()))
			.andExpect(jsonPath("$.mail").value(nonActiveUser.getMail()))
			.andExpect(jsonPath("$.name").value(nonActiveUser.getName()))
			.andExpect(jsonPath("$.birthDate").value(nonActiveUser.getBirthDate().toString()))
			.andExpect(jsonPath("$.surname").value(nonActiveUser.getSurname()))
			.andExpect(jsonPath("$.username").value(nonActiveUser.getUsername()))
			.andExpect(jsonPath("$.deletedAt").isEmpty())
			.andExpect(jsonPath("$.isActive").value(true));

		Assertions.assertTrue(userRepository.findById(nonActiveUser.getGuid()).orElseThrow().getIsActive());
	}

	@Test
	void activateUser_whenUserDoesNotExist_shouldReturnNotFound() throws Exception {
		var nonExistingId = getNonExistingId();

		mockMvc.perform(put("/v1/user/activate/{id}", nonExistingId).contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());

		Mockito.verify(userFacade, Mockito.times(1)).activateUser(Mockito.eq(nonExistingId));
	}

	@Test
	void getAllUsers_shouldReturnUsers() throws Exception {
		var allUsers = getExistingEntities();

		ResultActions result = mockMvc.perform(get("/v1/user/").contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());

		assertAllUsersEquals(result, allUsers);
	}

	@Test
	void updateUser_whenValid_shouldReturnUpdatedUser() throws Exception {
		var userViewDto = getValidUserViewDto();
		userViewDto.setName("NewName");
		userViewDto.setBirthDate(LocalDate.now());
		userViewDto.setSurname("NewSurname");
		userViewDto.setUsername("NewUsername");
		userViewDto.setMail("NewMail@mail.example");

		mockMvc
			.perform(put("/v1/user/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(userViewDto)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.guid").value(userViewDto.getGuid().toString()))
			.andExpect(jsonPath("$.mail").value(userViewDto.getMail()))
			.andExpect(jsonPath("$.name").value(userViewDto.getName()))
			.andExpect(jsonPath("$.birthDate").value(userViewDto.getBirthDate().toString()))
			.andExpect(jsonPath("$.surname").value(userViewDto.getSurname()))
			.andExpect(jsonPath("$.username").value(userViewDto.getUsername()));

		var updatedUser = repository.findById(userViewDto.getGuid()).orElseThrow();
		Assertions.assertEquals(userViewDto.getName(), updatedUser.getName());
		Assertions.assertEquals(userViewDto.getBirthDate(), updatedUser.getBirthDate());
		Assertions.assertEquals(userViewDto.getSurname(), updatedUser.getSurname());
		Assertions.assertEquals(userViewDto.getUsername(), updatedUser.getUsername());
		Assertions.assertEquals(userViewDto.getMail(), updatedUser.getMail());
	}

	@Test
	void updateUser_whenUserDoesNotExist_shouldReturnNotFound() throws Exception {
		var userViewDto = getValidUserViewDto();
		userViewDto.setUsername("NewUsernameHopefullyDoesNotExist");
		userViewDto.setMail("NewMail@mail.exampleHopefullyDoesNotExist");
		userViewDto.setGuid(getNonExistingId());

		mockMvc
			.perform(put("/v1/user/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(userViewDto)))
			.andExpect(status().isNotFound());

		Mockito.verify(userFacade, Mockito.times(1)).updateUser(Mockito.any());
	}

	@Test
	void updateUser_whenValidationFails_shouldReturnBadRequest() throws Exception {
		var nonValidDto = getValidUserViewDto();
		var lastValidMail = nonValidDto.getMail();
		nonValidDto.setMail("aaa");

		mockMvc
			.perform(put("/v1/user/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(nonValidDto)))
			.andExpect(status().isBadRequest());

		Assertions.assertEquals(lastValidMail, userRepository.findById(nonValidDto.getGuid()).orElseThrow().getMail());
	}

	@Test
	void addRoleToUser_whenValid_shouldReturnUpdatedUser() throws Exception {
		var existingUser = getExistingEntity();
		var userRoleIds = new HashSet<UUID>(
				existingUser.getRoles().stream().map(ur -> ur.getRole().getGuid()).toList());
		var role = roleRepository.findAll()
			.stream()
			.filter(r -> !userRoleIds.contains(r.getGuid()))
			.findFirst()
			.orElseThrow();

		ResultActions result = mockMvc
			.perform(put("/v1/user/{userId}/role/{roleId}", existingUser.getGuid(), role.getGuid())
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.guid").value(existingUser.getGuid().toString()))
			.andExpect(jsonPath("$.mail").value(existingUser.getMail()))
			.andExpect(jsonPath("$.name").value(existingUser.getName()))
			.andExpect(jsonPath("$.birthDate").value(existingUser.getBirthDate().toString()))
			.andExpect(jsonPath("$.surname").value(existingUser.getSurname()))
			.andExpect(jsonPath("$.username").value(existingUser.getUsername()))
			.andExpect(jsonPath("$.isActive").value(existingUser.getIsActive()))
			.andExpect(jsonPath("$.roles").isArray())
			.andExpect(jsonPath("$.roles").isNotEmpty())
			.andExpect(jsonPath("$.roles.length()").value(userRoleIds.size() + 1));

		assertUserEquals(result, userRepository.findById(existingUser.getGuid()).orElseThrow());
	}

	@Test
	void addRoleToUser_whenNotFound_shouldReturnNotFound() throws Exception {
		var userId = getExistingEntity().getGuid();
		var nonExistingId = getNonExistingEntityId(roleRepository);

		mockMvc
			.perform(put("/v1/user/{userId}/role/{roleId}", userId, nonExistingId)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());

		Mockito.verify(userFacade, Mockito.times(1)).addRoleToUser(Mockito.eq(userId), Mockito.eq(nonExistingId));
	}

	@Test
	void deleteRoleFromUser_whenValid_shouldReturnUpdatedUser() throws Exception {
		var existingUser = getFirstFilteredEntity(u -> !u.getRoles().isEmpty());
		var roleCountBefore = existingUser.getRoles().size();
		var roleId = existingUser.getRoles().stream().findFirst().orElseThrow().getRole().getGuid();

		ResultActions result = mockMvc
			.perform(delete("/v1/user/{userId}/role/{roleId}", existingUser.getGuid(), roleId)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.guid").value(existingUser.getGuid().toString()))
			.andExpect(jsonPath("$.mail").value(existingUser.getMail()))
			.andExpect(jsonPath("$.name").value(existingUser.getName()))
			.andExpect(jsonPath("$.birthDate").value(existingUser.getBirthDate().toString()))
			.andExpect(jsonPath("$.surname").value(existingUser.getSurname()))
			.andExpect(jsonPath("$.username").value(existingUser.getUsername()))
			.andExpect(jsonPath("$.isActive").value(existingUser.getIsActive()))
			.andExpect(jsonPath("$.roles").isArray())
			.andExpect(jsonPath("$.roles.length()").value(roleCountBefore - 1));

		assertUserEquals(result, userRepository.findById(existingUser.getGuid()).orElseThrow());
	}

	@Test
	void deleteRoleFromUser_whenNotFound_shouldReturnNotFound() throws Exception {
		var userId = getExistingEntity().getGuid();
		var roleId = getNonExistingEntityId(roleRepository);

		mockMvc
			.perform(delete("/v1/user/{userId}/role/{roleId}", userId, roleId).contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());

		Mockito.verify(userFacade, Mockito.times(1)).deleteRoleFromUser(Mockito.eq(userId), Mockito.eq(roleId));
	}

	private void assertAllUsersEquals(ResultActions result, List<User> users) throws Exception {
		Assertions.assertFalse(users.isEmpty());
		List<Map<String, Object>> jsonUsers = objectMapper
			.readValue(result.andReturn().getResponse().getContentAsString(), new TypeReference<>() {
			});

		for (Map<String, Object> jsonUser : jsonUsers) {
			UUID userId = UUID.fromString(jsonUser.get("guid").toString());

			User expectedUser = users.stream()
				.filter(u -> u.getGuid().equals(userId))
				.findFirst()
				.orElseThrow(() -> new EntityNotFoundException("User with guid " + userId + " not found!"));

			assertUserEquals(jsonUser, expectedUser);
		}
	}

	private void assertUserEquals(ResultActions result, User user) throws Exception {
		Map<String, Object> jsonUser = objectMapper.readValue(result.andReturn().getResponse().getContentAsString(),
				new TypeReference<>() {
				});

		assertUserEquals(jsonUser, user);
	}

	private void assertUserEquals(Map<String, Object> jsonUser, User user) throws Exception {
		Assertions.assertEquals(jsonUser.get("guid"), user.getGuid().toString());
		Assertions.assertEquals(jsonUser.get("mail"), user.getMail());
		Assertions.assertEquals(jsonUser.get("surname"), user.getSurname());
		Assertions.assertEquals(jsonUser.get("name"), user.getName());
		Assertions.assertEquals(jsonUser.get("username"), user.getUsername());
		Assertions.assertEquals(jsonUser.get("birthDate"), user.getBirthDate().toString());

		List<Map<String, Object>> jsonPayments = (List<Map<String, Object>>) jsonUser.get("payments");
		assertAllUserPaymentsEquals(jsonPayments, user);

		List<Map<String, Object>> jsonRoles = (List<Map<String, Object>>) jsonUser.get("roles");
		assertAllUserRolesEquals(jsonRoles, user);
	}

	private void assertUserRoleEquals(Map<String, Object> jsonRole, Role role) throws Exception {
		Assertions.assertEquals(role.getGuid().toString(), jsonRole.get("guid"));
		Assertions.assertEquals(role.getCode(), jsonRole.get("code"));
		Assertions.assertEquals(role.getName(), jsonRole.get("name"));
		Assertions.assertEquals(role.getDescription(), jsonRole.get("description"));
	}

	private void assertAllUserRolesEquals(List<Map<String, Object>> jsonRoles, User user) throws Exception {
		for (Map<String, Object> jsonRole : jsonRoles) {
			UUID roleId = UUID.fromString(jsonRole.get("guid").toString());

			Role expectedRole = user.getRoles()
				.stream()
				.map(UserHasRole::getRole)
				.filter(u -> u.getGuid().equals(roleId))
				.findFirst()
				.orElseThrow(() -> new EntityNotFoundException("Role with guid " + roleId + " not found in user!"));

			assertUserRoleEquals(jsonRole, expectedRole);
		}
	}

	private void assertUserPaymentEquals(Map<String, Object> jsonPayment, Payment payment) {
		Assertions.assertEquals(payment.getGuid().toString(), jsonPayment.get("guid"));
		Assertions.assertEquals(payment.getPaid(), jsonPayment.get("paid"));
		Assertions.assertEquals(payment.getCreatedAt().truncatedTo(ChronoUnit.SECONDS),
				LocalDateTime.parse(jsonPayment.get("createdAt").toString()).truncatedTo(ChronoUnit.SECONDS));
	}

	private void assertAllUserPaymentsEquals(List<Map<String, Object>> jsonPayments, User user) {
		for (Map<String, Object> jsonPayment : jsonPayments) {
			UUID paymentId = UUID.fromString(jsonPayment.get("guid").toString());

			Payment expectedRole = user.getPayments()
				.stream()
				.filter(p -> p.getGuid().equals(paymentId))
				.findFirst()
				.orElseThrow(
						() -> new EntityNotFoundException("Payment with guid " + paymentId + " not found in user!"));

			assertUserPaymentEquals(jsonPayment, expectedRole);
		}
	}

	private UserCreateDto getValidUserCreateDto() {
		return UserCreateDto.builder()
			.mail("example@of.mail")
			.name("John")
			.surname("Example")
			.username("princess123")
			.password("*1*VeryBigSecret++")
			.birthDate(LocalDate.of(2000, 1, 1))
			.build();
	}

	private ChangePasswordRequestDto getValidChangePasswordRequestDto(int index) {
		var user = getExistingEntity(index);

		return ChangePasswordRequestDto.builder()
			.oldPassword(testDataFactory.getUserPasswordById(user.getGuid()))
			.userId(user.getGuid())
			.newPassword("NewSafePassword123*+-")
			.build();
	}

	private String getValidPassword() {
		return "poiajPIJ123*+";
	}

	private UserViewDto getValidUserViewDto() {
		var entity = getExistingEntity();
		UserViewDto dto = userMapper.userToUserViewDto(entity);
		return dto;
	}

}
