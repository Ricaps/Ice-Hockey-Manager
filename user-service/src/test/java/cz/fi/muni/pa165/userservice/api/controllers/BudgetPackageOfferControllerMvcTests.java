package cz.fi.muni.pa165.userservice.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.fi.muni.pa165.dto.userService.BudgetOfferPackageDto;
import cz.fi.muni.pa165.service.userService.api.api.BudgetOfferPackageController;
import cz.fi.muni.pa165.userservice.business.facades.BudgetOfferPackageFacade;
import cz.fi.muni.pa165.userservice.security.SecurityConfig;
import cz.fi.muni.pa165.userservice.testData.BudgetOfferPackageTestData;
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

import java.util.Collections;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BudgetOfferPackageController.class)
@Import(SecurityConfig.class)
public class BudgetPackageOfferControllerMvcTests {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private BudgetOfferPackageFacade budgetOfferPackageFacade;

	@InjectMocks
	private BudgetPackageOfferControllerImpl budgetPackageOfferController;

	@Autowired
	ObjectMapper objectMapper;

	private BudgetOfferPackageDto budgetOfferPackageDto;

	private UUID packageId;

	@BeforeEach
	void setUp() {
		packageId = UUID.randomUUID();
		budgetOfferPackageDto = BudgetOfferPackageTestData.getBudgetOfferPackageDto();
		budgetOfferPackageDto.setGuid(packageId);
	}

	@Test
	void getBudgetOfferPackageById_whenPackageExists_shouldReturnPackage() throws Exception {
		Mockito.when(budgetOfferPackageFacade.getBudgetPackageOfferById(packageId)).thenReturn(budgetOfferPackageDto);

		mockMvc.perform(get("/v1/budget-package-offer/{id}", packageId).contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.guid").value(packageId.toString()))
			.andExpect(jsonPath("$.budgetIncrease").value(budgetOfferPackageDto.getBudgetIncrease()))
			.andExpect(jsonPath("$.description").value(budgetOfferPackageDto.getDescription()))
			.andExpect(jsonPath("$.isAvailable").value(budgetOfferPackageDto.getIsAvailable()))
			.andExpect(jsonPath("$.priceDollars").value(budgetOfferPackageDto.getPriceDollars()));

		Mockito.verify(budgetOfferPackageFacade, Mockito.times(1)).getBudgetPackageOfferById(packageId);
	}

	@Test
	void getBudgetOfferPackageById_whenPackageDoesNotExist_shouldReturnNotFound() throws Exception {
		Mockito.when(budgetOfferPackageFacade.getBudgetPackageOfferById(packageId))
			.thenThrow(EntityNotFoundException.class);

		mockMvc.perform(get("/v1/budget-package-offer/{id}", packageId).contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());
	}

	@Test
	void createBudgetOfferPackage_whenValidEntity_shouldReturnCreatedPackage() throws Exception {
		Mockito.when(budgetOfferPackageFacade.create(Mockito.any(BudgetOfferPackageDto.class)))
			.thenReturn(budgetOfferPackageDto);

		mockMvc
			.perform(post("/v1/budget-package-offer/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(budgetOfferPackageDto)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.guid").value(packageId.toString()))
			.andExpect(jsonPath("$.budgetIncrease").value(budgetOfferPackageDto.getBudgetIncrease()))
			.andExpect(jsonPath("$.description").value(budgetOfferPackageDto.getDescription()))
			.andExpect(jsonPath("$.isAvailable").value(budgetOfferPackageDto.getIsAvailable()))
			.andExpect(jsonPath("$.priceDollars").value(budgetOfferPackageDto.getPriceDollars()));

		Mockito.verify(budgetOfferPackageFacade, Mockito.times(1)).create(Mockito.any(BudgetOfferPackageDto.class));
	}

	@Test
	void createBudgetOfferPackage_whenValidationFails_shouldReturnBadRequest() throws Exception {
		budgetOfferPackageDto.setBudgetIncrease(0);

		mockMvc
			.perform(post("/v1/budget-package-offer/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(budgetOfferPackageDto)))
			.andExpect(status().isBadRequest());

		Mockito.verify(budgetOfferPackageFacade, Mockito.never()).create(Mockito.any(BudgetOfferPackageDto.class));
	}

	@Test
	void deactivateBudgetOfferPackage_whenPackageExists_shouldReturnDeactivatedPackage() throws Exception {
		Mockito.when(budgetOfferPackageFacade.deactivateBudgetOfferPackage(packageId))
			.thenReturn(budgetOfferPackageDto);

		mockMvc
			.perform(put("/v1/budget-package-offer/deactivate/{id}", packageId).contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.guid").value(packageId.toString()))
			.andExpect(jsonPath("$.budgetIncrease").value(budgetOfferPackageDto.getBudgetIncrease()))
			.andExpect(jsonPath("$.description").value(budgetOfferPackageDto.getDescription()))
			.andExpect(jsonPath("$.isAvailable").value(budgetOfferPackageDto.getIsAvailable()))
			.andExpect(jsonPath("$.priceDollars").value(budgetOfferPackageDto.getPriceDollars()));

		Mockito.verify(budgetOfferPackageFacade, Mockito.times(1)).deactivateBudgetOfferPackage(packageId);
	}

	@Test
	void deactivateBudgetOfferPackage_whenPackageDoesNotExist_shouldReturnNotFound() throws Exception {
		Mockito.when(budgetOfferPackageFacade.deactivateBudgetOfferPackage(packageId))
			.thenThrow(EntityNotFoundException.class);

		mockMvc
			.perform(put("/v1/budget-package-offer/deactivate/{id}", packageId).contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());

		Mockito.verify(budgetOfferPackageFacade, Mockito.times(1)).deactivateBudgetOfferPackage(packageId);
	}

	@Test
	void activateBudgetOfferPackage_whenPackageExists_shouldReturnActivatedPackage() throws Exception {
		Mockito.when(budgetOfferPackageFacade.activateBudgetOfferPackage(packageId)).thenReturn(budgetOfferPackageDto);

		mockMvc
			.perform(put("/v1/budget-package-offer/activate/{id}", packageId).contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.guid").value(packageId.toString()))
			.andExpect(jsonPath("$.budgetIncrease").value(budgetOfferPackageDto.getBudgetIncrease()))
			.andExpect(jsonPath("$.description").value(budgetOfferPackageDto.getDescription()))
			.andExpect(jsonPath("$.isAvailable").value(budgetOfferPackageDto.getIsAvailable()))
			.andExpect(jsonPath("$.priceDollars").value(budgetOfferPackageDto.getPriceDollars()));

		Mockito.verify(budgetOfferPackageFacade, Mockito.times(1)).activateBudgetOfferPackage(packageId);
	}

	@Test
	void activateBudgetOfferPackage_whenPackageDoesNotExist_shouldReturnNotFound() throws Exception {
		Mockito.when(budgetOfferPackageFacade.activateBudgetOfferPackage(packageId))
			.thenThrow(EntityNotFoundException.class);

		mockMvc
			.perform(put("/v1/budget-package-offer/activate/{id}", packageId).contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());

		Mockito.verify(budgetOfferPackageFacade, Mockito.times(1)).activateBudgetOfferPackage(packageId);
	}

	@Test
	void getAllBudgetOfferPackages_whenValidRequest_shouldReturnPackages() throws Exception {
		Mockito.when(budgetOfferPackageFacade.getAllBudgetOfferPackages())
			.thenReturn(Collections.singletonList(budgetOfferPackageDto));

		mockMvc.perform(get("/v1/budget-package-offer/all-packages").contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].guid").value(packageId.toString()))
			.andExpect(jsonPath("$[0].budgetIncrease").value(budgetOfferPackageDto.getBudgetIncrease()))
			.andExpect(jsonPath("$[0].description").value(budgetOfferPackageDto.getDescription()))
			.andExpect(jsonPath("$[0].isAvailable").value(budgetOfferPackageDto.getIsAvailable()))
			.andExpect(jsonPath("$[0].priceDollars").value(budgetOfferPackageDto.getPriceDollars()));

		Mockito.verify(budgetOfferPackageFacade, Mockito.times(1)).getAllBudgetOfferPackages();
	}

	@Test
	void getAllAvailableBudgetOfferPackages_whenValidRequest_shouldReturnAvailablePackages() throws Exception {
		Mockito.when(budgetOfferPackageFacade.getAllAvailableBudgetOfferPackages())
			.thenReturn(Collections.singletonList(budgetOfferPackageDto));

		mockMvc.perform(get("/v1/budget-package-offer/available-packages").contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].guid").value(packageId.toString()))
			.andExpect(jsonPath("$[0].budgetIncrease").value(budgetOfferPackageDto.getBudgetIncrease()))
			.andExpect(jsonPath("$[0].description").value(budgetOfferPackageDto.getDescription()))
			.andExpect(jsonPath("$[0].isAvailable").value(budgetOfferPackageDto.getIsAvailable()))
			.andExpect(jsonPath("$[0].priceDollars").value(budgetOfferPackageDto.getPriceDollars()));

		Mockito.verify(budgetOfferPackageFacade, Mockito.times(1)).getAllAvailableBudgetOfferPackages();
	}

}
