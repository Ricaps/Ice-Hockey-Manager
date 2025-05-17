package cz.fi.muni.pa165.userservice.integration.api.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.fi.muni.pa165.dto.userservice.UserCreateDto;
import cz.fi.muni.pa165.dto.userservice.UserViewDto;
import cz.fi.muni.pa165.userservice.business.facades.UserFacade;
import cz.fi.muni.pa165.userservice.business.mappers.UserMapper;
import cz.fi.muni.pa165.userservice.persistence.entities.Payment;
import cz.fi.muni.pa165.userservice.persistence.entities.User;
import cz.fi.muni.pa165.userservice.persistence.repositories.UserRepository;
import cz.fi.muni.pa165.userservice.util.AuthUtil;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
	private UserMapper userMapper;

	@MockitoBean
	AuthUtil authUtil;

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
		Mockito.when(authUtil.getAuthMail()).thenReturn(newUser.getMail());

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
		Mockito.when(authUtil.getAuthMail()).thenReturn("valid@mail.com");
		var allUsersSize = getExistingEntities().size();
		var newUser = getValidUserCreateDto();
		newUser.setUsername("");

		mockMvc
			.perform(post("/v1/user/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newUser)))
			.andExpect(status().isBadRequest());

		Assertions.assertEquals(allUsersSize, getExistingEntities().size());
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
		nonValidDto.setUsername("");

		mockMvc
			.perform(put("/v1/user/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(nonValidDto)))
			.andExpect(status().isBadRequest());

		Assertions.assertEquals(lastValidMail, userRepository.findById(nonValidDto.getGuid()).orElseThrow().getMail());
	}

	@Test
	void isUserAdmin_whenUserExistsAndIsNotAdmin_shouldReturnFalse() throws Exception {
		var nonAdminUser = getFirstFilteredEntity(u -> !u.getIsAdmin());

		mockMvc.perform(get("/v1/user/{id}/is-admin", nonAdminUser.getGuid()).contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").value(false));
	}

	@Test
	void isUserAdmin_whenUserExistsAndIsAdmin_shouldReturnTrue() throws Exception {
		var adminUser = getFirstFilteredEntity(User::getIsAdmin);

		mockMvc.perform(get("/v1/user/{id}/is-admin", adminUser.getGuid()).contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").value(true));
	}

	@Test
	void isUserAdmin_whenUserDoesNotExists_shouldReturnNotExists() throws Exception {
		mockMvc.perform(get("/v1/user/{id}/is-admin", UUID.randomUUID()).contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());

		Mockito.verify(userFacade, Mockito.times(1)).isUserAdmin(Mockito.any(UUID.class));
	}

	@Test
	void setIsUserAdmin_whenUserDoesNotExists_shouldReturnNotExists() throws Exception {
		mockMvc
			.perform(patch("/v1/user/{id}/is-admin", UUID.randomUUID()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(false)))
			.andExpect(status().isNotFound());

		Mockito.verify(userFacade, Mockito.times(1)).setIsUserAdmin(Mockito.any(UUID.class), Mockito.anyBoolean());
	}

	@Test
	void setIsUserAdmin_whenUserExistsAndIsAdmin_shouldMakeUserNonAdmin() throws Exception {
		Mockito.when(authUtil.isAuthenticatedUserAdmin()).thenReturn(true);
		var adminUser = getFirstFilteredEntity(User::getIsAdmin);

		mockMvc
			.perform(patch("/v1/user/{id}/is-admin", adminUser.getGuid()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(false)))
			.andExpect(status().isNoContent());

		var isAdmin = repository.findIsAdminByGuid(adminUser.getGuid());
		Assertions.assertTrue(isAdmin.isPresent());
		Assertions.assertFalse(isAdmin.get());
	}

	@Test
	void setIsUserAdmin_whenUserExistsAndIsNotAdmin_shouldMakeUserAdmin() throws Exception {
		var adminUser = getFirstFilteredEntity(u -> !u.getIsAdmin());
		Mockito.when(authUtil.isAuthenticatedUserAdmin()).thenReturn(true);

		mockMvc
			.perform(patch("/v1/user/{id}/is-admin", adminUser.getGuid()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(true)))
			.andExpect(status().isNoContent());

		var isAdmin = repository.findIsAdminByGuid(adminUser.getGuid());
		Assertions.assertTrue(isAdmin.isPresent());
		Assertions.assertTrue(isAdmin.get());
	}

	@Test
	void setIsUserAdmin_whenAuthenticatedUserIsNotAdmin_shouldReturnUnauthorized() throws Exception {
		var adminUser = getFirstFilteredEntity(u -> !u.getIsAdmin());
		Mockito.when(authUtil.isAuthenticatedUserAdmin()).thenReturn(false);

		mockMvc
			.perform(patch("/v1/user/{id}/is-admin", adminUser.getGuid()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(true)))
			.andExpect(status().isUnauthorized());

		var isAdmin = repository.findIsAdminByGuid(adminUser.getGuid());
		Assertions.assertTrue(isAdmin.isPresent());
		Assertions.assertFalse(isAdmin.get());
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

	private void assertUserEquals(Map<String, Object> jsonUser, User user) {
		Assertions.assertEquals(jsonUser.get("guid"), user.getGuid().toString());
		Assertions.assertEquals(jsonUser.get("mail"), user.getMail());
		Assertions.assertEquals(jsonUser.get("surname"), user.getSurname());
		Assertions.assertEquals(jsonUser.get("name"), user.getName());
		Assertions.assertEquals(jsonUser.get("username"), user.getUsername());
		Assertions.assertEquals(jsonUser.get("birthDate"), user.getBirthDate().toString());

		List<Map<String, Object>> jsonPayments = (List<Map<String, Object>>) jsonUser.get("payments");
		assertAllUserPaymentsEquals(jsonPayments, user);
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
			.birthDate(LocalDate.of(2000, 1, 1))
			.build();
	}

	private UserViewDto getValidUserViewDto() {
		var entity = getExistingEntity();
		return userMapper.userToUserViewDto(entity);
	}

}
