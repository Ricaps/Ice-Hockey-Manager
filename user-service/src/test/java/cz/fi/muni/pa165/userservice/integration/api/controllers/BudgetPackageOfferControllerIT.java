package cz.fi.muni.pa165.userservice.integration.api.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.fi.muni.pa165.dto.userService.BudgetOfferPackageDto;
import cz.fi.muni.pa165.userservice.business.facades.BudgetOfferPackageFacade;
import cz.fi.muni.pa165.userservice.config.SecurityTestConfig;
import cz.fi.muni.pa165.userservice.persistence.entities.BudgetOfferPackage;
import cz.fi.muni.pa165.userservice.persistence.repositories.BudgetOfferPackageRepository;
import cz.fi.muni.pa165.userservice.util.AuthUtil;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Import({ SecurityTestConfig.class })
public class BudgetPackageOfferControllerIT extends BaseControllerIT<BudgetOfferPackageRepository, BudgetOfferPackage> {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoSpyBean
	private BudgetOfferPackageFacade budgetOfferPackageFacade;

	@MockitoBean
	private AuthUtil authUtil;

	private final BudgetOfferPackageRepository budgetOfferPackageRepository;

	@Autowired
	public BudgetPackageOfferControllerIT(BudgetOfferPackageRepository budgetOfferPackageRepository) {
		super(budgetOfferPackageRepository);
		this.budgetOfferPackageRepository = budgetOfferPackageRepository;
	}

	@Test
	void getBudgetOfferPackageById_whenPackageExists_shouldReturnPackage() throws Exception {
		var existingPackage = getExistingEntity();

		mockMvc
			.perform(get("/v1/budget-package-offer/{id}", existingPackage.getGuid())
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.guid").value(existingPackage.getGuid().toString()))
			.andExpect(jsonPath("$.budgetIncrease").value(existingPackage.getBudgetIncrease()))
			.andExpect(jsonPath("$.description").value(existingPackage.getDescription()))
			.andExpect(jsonPath("$.isAvailable").value(existingPackage.getIsAvailable()))
			.andExpect(jsonPath("$.priceDollars").value(existingPackage.getPriceDollars()));
	}

	@Test
	void getBudgetOfferPackageById_whenPackageDoesNotExist_shouldReturnNotFound() throws Exception {
		var nonExistingId = getNonExistingId();

		mockMvc.perform(get("/v1/budget-package-offer/{id}", nonExistingId).contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());

		Mockito.verify(budgetOfferPackageFacade, Mockito.times(1)).getBudgetPackageOfferById(Mockito.eq(nonExistingId));
	}

	@Test
	void createBudgetOfferPackage_whenValidEntity_shouldReturnCreatedPackage() throws Exception {
		var packages = getExistingEntities();
		var validPackage = getValidBudgetOfferPackageDto();
		validPackage.setGuid(null);
		Mockito.when(authUtil.isAuthenticatedUserAdmin()).thenReturn(true);

		ResultActions result = mockMvc
			.perform(post("/v1/budget-package-offer/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(validPackage)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.guid").isNotEmpty())
			.andExpect(jsonPath("$.budgetIncrease").value(validPackage.getBudgetIncrease()))
			.andExpect(jsonPath("$.description").value(validPackage.getDescription()))
			.andExpect(jsonPath("$.isAvailable").value(validPackage.getIsAvailable()))
			.andExpect(jsonPath("$.priceDollars").value(validPackage.getPriceDollars()));

		BudgetOfferPackageDto dto = objectMapper.readValue(result.andReturn().getResponse().getContentAsString(),
				BudgetOfferPackageDto.class);

		Assertions.assertEquals(packages.size() + 1, getExistingEntities().size());
		Assertions.assertTrue(budgetOfferPackageRepository.existsById(dto.getGuid()));
	}

	@Test
	void createBudgetOfferPackage_whenValidationFails_shouldReturnBadRequest() throws Exception {
		var packages = getExistingEntities();
		var notValidPackage = getValidBudgetOfferPackageDto();
		notValidPackage.setGuid(null);
		notValidPackage.setBudgetIncrease(-1);

		mockMvc
			.perform(post("/v1/budget-package-offer/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(notValidPackage)))
			.andExpect(status().isBadRequest());

		Assertions.assertEquals(packages.size(), getExistingEntities().size());
	}

	@Test
	void createBudgetOfferPackage_whenAuthenticatedUserIsNotAdmin_shouldReturnUnauthorized() throws Exception {
		var validPackage = getValidBudgetOfferPackageDto();
		validPackage.setGuid(null);
		Mockito.when(authUtil.isAuthenticatedUserAdmin()).thenReturn(false);

		mockMvc
			.perform(post("/v1/budget-package-offer/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(validPackage)))
			.andExpect(status().isUnauthorized());
	}

	@Test
	void deactivateBudgetOfferPackage_whenPackageExists_shouldReturnDeactivatedPackage() throws Exception {
		var existingPackage = getFirstFilteredEntity(BudgetOfferPackage::getIsAvailable);
		Mockito.when(authUtil.isAuthenticatedUserAdmin()).thenReturn(true);

		mockMvc
			.perform(put("/v1/budget-package-offer/deactivate/{id}", existingPackage.getGuid())
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.guid").value(existingPackage.getGuid().toString()))
			.andExpect(jsonPath("$.budgetIncrease").value(existingPackage.getBudgetIncrease()))
			.andExpect(jsonPath("$.description").value(existingPackage.getDescription()))
			.andExpect(jsonPath("$.isAvailable").value(false))
			.andExpect(jsonPath("$.priceDollars").value(existingPackage.getPriceDollars()));

		Assertions.assertFalse(
				budgetOfferPackageRepository.findById(existingPackage.getGuid()).orElseThrow().getIsAvailable());
	}

	@Test
	void deactivateBudgetOfferPackage_whenPackageDoesNotExist_shouldReturnNotFound() throws Exception {
		var nonExistingId = getNonExistingId();
		Mockito.when(authUtil.isAuthenticatedUserAdmin()).thenReturn(true);

		mockMvc
			.perform(put("/v1/budget-package-offer/deactivate/{id}", nonExistingId)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());

		Mockito.verify(budgetOfferPackageFacade, Mockito.times(1))
			.deactivateBudgetOfferPackage(Mockito.eq(nonExistingId));
	}

	@Test
	void deactivateBudgetOfferPackage_whenAuthenticatedUserIsNotAdmin_shouldReturnUnauthorized() throws Exception {
		var existingPackage = getFirstFilteredEntity(BudgetOfferPackage::getIsAvailable);
		Mockito.when(authUtil.isAuthenticatedUserAdmin()).thenReturn(false);

		mockMvc
			.perform(put("/v1/budget-package-offer/deactivate/{id}", existingPackage.getGuid())
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnauthorized());
	}

	@Test
	void activateBudgetOfferPackage_whenPackageExists_shouldReturnActivatedPackage() throws Exception {
		var existingPackage = getFirstFilteredEntity(p -> !p.getIsAvailable());
		Mockito.when(authUtil.isAuthenticatedUserAdmin()).thenReturn(true);

		mockMvc
			.perform(put("/v1/budget-package-offer/activate/{id}", existingPackage.getGuid())
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.guid").value(existingPackage.getGuid().toString()))
			.andExpect(jsonPath("$.budgetIncrease").value(existingPackage.getBudgetIncrease()))
			.andExpect(jsonPath("$.description").value(existingPackage.getDescription()))
			.andExpect(jsonPath("$.isAvailable").value(true))
			.andExpect(jsonPath("$.priceDollars").value(existingPackage.getPriceDollars()));

		Assertions.assertTrue(
				budgetOfferPackageRepository.findById(existingPackage.getGuid()).orElseThrow().getIsAvailable());
	}

	@Test
	void activateBudgetOfferPackage_whenPackageDoesNotExist_shouldReturnNotFound() throws Exception {
		var nonExistingId = getNonExistingId();
		Mockito.when(authUtil.isAuthenticatedUserAdmin()).thenReturn(true);

		mockMvc
			.perform(put("/v1/budget-package-offer/activate/{id}", nonExistingId)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());

		Mockito.verify(budgetOfferPackageFacade, Mockito.times(1))
			.activateBudgetOfferPackage(Mockito.eq(nonExistingId));
	}

	@Test
	void activateBudgetOfferPackage_whenAuthenticatedUserIsNotAdmin_shouldReturnUnauthorized() throws Exception {
		var existingPackage = getFirstFilteredEntity(p -> !p.getIsAvailable());
		Mockito.when(authUtil.isAuthenticatedUserAdmin()).thenReturn(false);

		mockMvc
			.perform(put("/v1/budget-package-offer/activate/{id}", existingPackage.getGuid())
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnauthorized());
	}

	@Test
	void getAllBudgetOfferPackages_whenValidRequest_shouldReturnPackages() throws Exception {
		var allPackages = getExistingEntities();

		ResultActions result = mockMvc.perform(get("/v1/budget-package-offer/").contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isArray())
			.andExpect(jsonPath("$.length()").value(allPackages.size()));

		assertAllPackagesEquals(allPackages, result);
	}

	@Test
	void getAllAvailableBudgetOfferPackages_whenValidRequest_shouldReturnAvailablePackages() throws Exception {
		var allPackages = getFilteredEntities(BudgetOfferPackage::getIsAvailable);

		ResultActions result = mockMvc
			.perform(get("/v1/budget-package-offer/available-packages").contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isArray())
			.andExpect(jsonPath("$.length()").value(allPackages.size()));

		assertAllPackagesEquals(allPackages, result);
	}

	private void assertAllPackagesEquals(List<BudgetOfferPackage> allPackages, ResultActions result) throws Exception {
		Assertions.assertFalse(allPackages.isEmpty());
		List<Map<String, String>> jsonPackages = objectMapper
			.readValue(result.andReturn().getResponse().getContentAsString(), new TypeReference<>() {
			});
		for (Map<String, String> jsonPackage : jsonPackages) {
			UUID packageId = UUID.fromString(jsonPackage.get("guid"));
			BudgetOfferPackage expectedPackage = allPackages.stream()
				.filter(p -> p.getGuid().equals(packageId))
				.findFirst()
				.orElseThrow(() -> new EntityNotFoundException("Package with guid " + packageId + " not found!"));

			Assertions.assertEquals(expectedPackage.getBudgetIncrease().toString(), jsonPackage.get("budgetIncrease"));
			Assertions.assertEquals(expectedPackage.getDescription(), jsonPackage.get("description"));
			Assertions.assertEquals(expectedPackage.getIsAvailable().toString(), jsonPackage.get("isAvailable"));
			Assertions.assertEquals(expectedPackage.getPriceDollars().toString(), jsonPackage.get("priceDollars"));
		}
	}

	private BudgetOfferPackageDto getValidBudgetOfferPackageDto() {
		return BudgetOfferPackageDto.builder()
			.budgetIncrease(6000000)
			.guid(UUID.randomUUID())
			.isAvailable(true)
			.description("Special valentine lonely pack!")
			.priceDollars(20)
			.build();

	}

}
