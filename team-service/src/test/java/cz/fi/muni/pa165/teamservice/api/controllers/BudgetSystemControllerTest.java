package cz.fi.muni.pa165.teamservice.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.fi.muni.pa165.dto.teamService.BudgetSystemCreateDTO;
import cz.fi.muni.pa165.dto.teamService.BudgetSystemDTO;
import cz.fi.muni.pa165.dto.teamService.BudgetSystemUpdateDTO;
import cz.fi.muni.pa165.teamservice.api.exception.ResourceNotFoundException;
import cz.fi.muni.pa165.teamservice.business.facades.BudgetSystemFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Jan Martinek
 */
public class BudgetSystemControllerTest {

	private final ObjectMapper objectMapper = new ObjectMapper();

	private final UUID budgetSystemId = UUID.randomUUID();

	private MockMvc mockMvc;

	@Mock
	private BudgetSystemFacade budgetSystemFacade;

	@InjectMocks
	private BudgetSystemController budgetSystemController;

	private BudgetSystemDTO budgetSystemDTO;

	private BudgetSystemCreateDTO budgetSystemCreateDTO;

	private BudgetSystemUpdateDTO budgetSystemUpdateDTO;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(budgetSystemController).build();

		budgetSystemDTO = new BudgetSystemDTO();
		budgetSystemDTO.setGuid(budgetSystemId);
		budgetSystemDTO.setAmount(1000000.00);

		budgetSystemCreateDTO = new BudgetSystemCreateDTO();
		budgetSystemCreateDTO.setAmount(1000000.00);

		budgetSystemUpdateDTO = new BudgetSystemUpdateDTO();
		budgetSystemUpdateDTO.setGuid(budgetSystemId);
		budgetSystemUpdateDTO.setAmount(1000000.00);
	}

	@Test
	void createBudgetSystem() throws Exception {
		when(budgetSystemFacade.createBudgetSystem(any(BudgetSystemCreateDTO.class))).thenReturn(budgetSystemDTO);

		mockMvc
			.perform(post("/api/budget-systems").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(budgetSystemCreateDTO)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.guid").value(budgetSystemId.toString()))
			.andExpect(jsonPath("$.amount").value(budgetSystemDTO.getAmount()));

		verify(budgetSystemFacade, times(1)).createBudgetSystem(any(BudgetSystemCreateDTO.class));
	}

	@Test
	void getBudgetSystemById() throws Exception {
		when(budgetSystemFacade.findById(budgetSystemId)).thenReturn(budgetSystemDTO);

		mockMvc.perform(get("/api/budget-systems/{id}", budgetSystemId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.guid").value(budgetSystemId.toString()))
			.andExpect(jsonPath("$.amount").value(budgetSystemDTO.getAmount()));

		verify(budgetSystemFacade, times(1)).findById(budgetSystemId);
	}

	@Test
	void getBudgetSystemById_notFound() throws Exception {
		when(budgetSystemFacade.findById(budgetSystemId)).thenThrow(ResourceNotFoundException.class);

		mockMvc.perform(get("/api/budget-systems/{id}", budgetSystemId)).andExpect(status().isNotFound());

		verify(budgetSystemFacade, times(1)).findById(budgetSystemId);
	}

	@Test
	void updateBudgetSystem() throws Exception {
		when(budgetSystemFacade.updateBudgetSystem(any(BudgetSystemUpdateDTO.class))).thenReturn(budgetSystemDTO);

		mockMvc
			.perform(put("/api/budget-systems/{id}", budgetSystemId).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(budgetSystemUpdateDTO)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.guid").value(budgetSystemId.toString()));

		verify(budgetSystemFacade, times(1)).updateBudgetSystem(any(BudgetSystemUpdateDTO.class));
	}

	@Test
	void updateBudgetSystem_mismatchedIds() throws Exception {
		UUID differentId = UUID.randomUUID();
		while (differentId.equals(budgetSystemId)) {
			differentId = UUID.randomUUID();
		}

		BudgetSystemUpdateDTO updateDTO = new BudgetSystemUpdateDTO();
		updateDTO.setGuid(differentId);
		updateDTO.setAmount(1000000.00);

		mockMvc
			.perform(put("/api/budget-systems/{id}", budgetSystemId).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDTO)))
			.andExpect(status().isBadRequest());

		verify(budgetSystemFacade, never()).updateBudgetSystem(any());
	}

	@Test
	void deleteBudgetSystem() throws Exception {
		doNothing().when(budgetSystemFacade).deleteBudgetSystem(budgetSystemId);

		mockMvc.perform(delete("/api/budget-systems/{id}", budgetSystemId)).andExpect(status().isNoContent());

		verify(budgetSystemFacade, times(1)).deleteBudgetSystem(budgetSystemId);
	}

	@Test
	void createBudgetSystem_invalidInput() throws Exception {
		BudgetSystemDTO invalidDTO = new BudgetSystemDTO();
		invalidDTO.setAmount(-100.00); // Negative amount

		mockMvc
			.perform(post("/api/budget-systems").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidDTO)))
			.andExpect(status().isBadRequest());

		verify(budgetSystemFacade, never()).createBudgetSystem(any());
	}

	@Test
	void updateBudgetSystem_invalidInput() throws Exception {
		budgetSystemDTO.setAmount(-50.00); // Negative amount

		mockMvc
			.perform(put("/api/budget-systems/{id}", budgetSystemId).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(budgetSystemDTO)))
			.andExpect(status().isBadRequest());

		verify(budgetSystemFacade, never()).updateBudgetSystem(any());
	}

	@Test
	void getBudgetSystemById_invalidUuid() throws Exception {
		mockMvc.perform(get("/api/budget-systems/{id}", "invalid-uuid")).andExpect(status().isBadRequest());

		verify(budgetSystemFacade, never()).findById(any());
	}

	@Test
	void updateBudgetSystem_nullAmount() throws Exception {
		budgetSystemDTO.setAmount(null);

		mockMvc
			.perform(put("/api/budget-systems/{id}", budgetSystemId).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(budgetSystemDTO)))
			.andExpect(status().isBadRequest());

		verify(budgetSystemFacade, never()).updateBudgetSystem(any());
	}

	@Test
	void createBudgetSystem_zeroAmount() throws Exception {
		budgetSystemDTO.setAmount(0.00);
		when(budgetSystemFacade.createBudgetSystem(any())).thenReturn(budgetSystemDTO);

		mockMvc
			.perform(post("/api/budget-systems").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(budgetSystemDTO)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.amount").value(budgetSystemDTO.getAmount()));

		verify(budgetSystemFacade, times(1)).createBudgetSystem(any());
	}

	@Test
	void updateBudgetSystem_largeAmount() throws Exception {
		budgetSystemDTO.setAmount(Double.MAX_VALUE);
		when(budgetSystemFacade.updateBudgetSystem(any())).thenReturn(budgetSystemDTO);

		mockMvc
			.perform(put("/api/budget-systems/{id}", budgetSystemId).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(budgetSystemDTO)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.amount").value(Double.MAX_VALUE));

		verify(budgetSystemFacade, times(1)).updateBudgetSystem(any());
	}

}
