package cz.fi.muni.pa165.teamservice.integration.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.fi.muni.pa165.dto.teamservice.BudgetSystemCreateDTO;
import cz.fi.muni.pa165.dto.teamservice.BudgetSystemUpdateDTO;
import cz.fi.muni.pa165.teamservice.config.DisableSecurityTestConfig;
import cz.fi.muni.pa165.teamservice.persistence.entities.BudgetSystem;
import cz.fi.muni.pa165.teamservice.persistence.repositories.BudgetSystemRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import cz.fi.muni.pa165.teamservice.persistence.entities.FictiveTeam;
import cz.fi.muni.pa165.teamservice.persistence.repositories.FictiveTeamRepository;
import org.junit.jupiter.api.BeforeEach;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Jan Martinek
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import({ DisableSecurityTestConfig.class })
@Transactional
class BudgetSystemControllerIT {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private BudgetSystemRepository budgetSystemRepository;

	@Autowired
	private FictiveTeamRepository fictiveTeamRepository;

	@Autowired
	private ObjectMapper objectMapper;

	private FictiveTeam testTeam;

	@BeforeEach
	void setUp() {
		testTeam = new FictiveTeam();
		testTeam.setName("Test Team");
		testTeam.setOwnerId(UUID.randomUUID());
		testTeam = fictiveTeamRepository.save(testTeam);
	}

	@Test
	void updateBudgetSystem_withValidAmount_shouldUpdateAndReturnOk() throws Exception {
		BudgetSystem budgetSystem = new BudgetSystem();
		budgetSystem.setAmount(500.0);
		budgetSystem.setTeam(testTeam);
		budgetSystem = budgetSystemRepository.save(budgetSystem);

		BudgetSystemUpdateDTO updateDTO = new BudgetSystemUpdateDTO();
		updateDTO.setGuid(budgetSystem.getGuid());
		updateDTO.setAmount(1500.0);

		mockMvc
			.perform(put("/api/budget-systems/{id}", budgetSystem.getGuid()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDTO)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.amount").value(1500.0));

		BudgetSystem updated = budgetSystemRepository.findById(budgetSystem.getGuid()).orElseThrow();
		assertThat(updated.getAmount()).isEqualTo(1500.0);
	}

	@Test
	void getBudgetSystem_withExistingId_shouldReturnBudgetSystem() throws Exception {
		BudgetSystem budgetSystem = new BudgetSystem();
		budgetSystem.setAmount(2000.0);
		budgetSystem.setTeam(testTeam);
		budgetSystem = budgetSystemRepository.save(budgetSystem);

		mockMvc.perform(get("/api/budget-systems/{id}", budgetSystem.getGuid()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.guid").value(budgetSystem.getGuid().toString()))
			.andExpect(jsonPath("$.amount").value(2000.0));
	}

	@Test
	void deleteBudgetSystem_withExistingId_shouldReturnNoContent() throws Exception {
		BudgetSystem budgetSystem = new BudgetSystem();
		budgetSystem.setAmount(3000.0);
		budgetSystem.setTeam(testTeam);
		budgetSystem = budgetSystemRepository.save(budgetSystem);

		mockMvc.perform(delete("/api/budget-systems/{id}", budgetSystem.getGuid())).andExpect(status().isNoContent());

		assertThat(budgetSystemRepository.existsById(budgetSystem.getGuid())).isFalse();
	}

	@Test
	void createBudgetSystemWithInvalidTeam_shouldReturnNotFound() throws Exception {
		BudgetSystemCreateDTO createDTO = new BudgetSystemCreateDTO();
		createDTO.setAmount(1000.0);
		createDTO.setTeamId(UUID.randomUUID()); // Invalid team ID

		mockMvc
			.perform(post("/api/budget-systems").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createDTO)))
			.andExpect(status().isNotFound());
	}

	@Test
	void createBudgetSystemWithValidTeam_shouldSucceed() throws Exception {
		BudgetSystemCreateDTO createDTO = new BudgetSystemCreateDTO();
		createDTO.setAmount(1000.0);
		createDTO.setTeamId(testTeam.getGuid());

		mockMvc
			.perform(post("/api/budget-systems").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createDTO)))
			.andExpect(status().isCreated());
	}

}
