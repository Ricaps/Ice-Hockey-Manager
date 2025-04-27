package cz.fi.muni.pa165.teamservice.integration.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.fi.muni.pa165.dto.teamService.FictiveTeamCreateDTO;
import cz.fi.muni.pa165.dto.teamService.FictiveTeamUpdateDTO;
import cz.fi.muni.pa165.enums.TeamCharacteristicType;
import cz.fi.muni.pa165.teamservice.persistence.entities.FictiveTeam;
import cz.fi.muni.pa165.teamservice.persistence.repositories.FictiveTeamRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
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
@Transactional
class FictiveTeamControllerIT {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private FictiveTeamRepository fictiveTeamRepository;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void createFictiveTeam_withValidData_shouldReturnCreated() throws Exception {
		FictiveTeamCreateDTO createDTO = new FictiveTeamCreateDTO();
		createDTO.setName("New Team");
		createDTO.setOwnerId(UUID.randomUUID());
		createDTO.setPlayerIds(List.of(UUID.randomUUID()));
		createDTO.setCharacteristicType(TeamCharacteristicType.COLLABORATION);

		mockMvc
			.perform(post("/v1/fictive-team/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createDTO)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.name").value("New Team"))
			.andExpect(jsonPath("$.characteristicType").value("COLLABORATION"));
	}

	@Test
	void updateFictiveTeam_withValidUpdate_shouldReturnUpdatedTeam() throws Exception {
		FictiveTeam team = new FictiveTeam();
		team.setName("Original Name");
		team.setOwnerId(UUID.randomUUID());
		team.setPlayerIDs(new ArrayList<>(List.of(UUID.randomUUID())));
		team.setCharacteristicType(TeamCharacteristicType.COLLABORATION);
		team = fictiveTeamRepository.save(team);

		FictiveTeamUpdateDTO updateDTO = new FictiveTeamUpdateDTO();
		updateDTO.setGuid(team.getGuid());
		updateDTO.setName("Updated Name");
		updateDTO.setOwnerId(team.getOwnerId());
		updateDTO.setPlayerIds(new ArrayList<>(List.of(UUID.randomUUID())));
		updateDTO.setCharacteristicType(TeamCharacteristicType.SPEED);

		mockMvc
			.perform(put("/v1/fictive-team/{uuid}", team.getGuid()).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDTO)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.name").value("Updated Name"))
			.andExpect(jsonPath("$.characteristicType").value("SPEED"));

		FictiveTeam updated = fictiveTeamRepository.findById(team.getGuid()).orElseThrow();
		assertThat(updated.getName()).isEqualTo("Updated Name");
		assertThat(updated.getCharacteristicType()).isEqualTo(TeamCharacteristicType.SPEED);
	}

	@Test
	void getFictiveTeam_withExistingId_shouldReturnTeam() throws Exception {
		FictiveTeam team = new FictiveTeam();
		team.setName("Test Team");
		team.setCharacteristicType(TeamCharacteristicType.COLLABORATION);
		team = fictiveTeamRepository.save(team);

		mockMvc.perform(get("/v1/fictive-team/{uuid}", team.getGuid()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.guid").value(team.getGuid().toString()))
			.andExpect(jsonPath("$.name").value("Test Team"))
			.andExpect(jsonPath("$.characteristicType").value("COLLABORATION"));
	}

	@Test
	void deleteFictiveTeam_withExistingId_shouldReturnNoContent() throws Exception {
		FictiveTeam team = new FictiveTeam();
		team.setOwnerId(UUID.randomUUID());
		team.setName("Team to Delete");
		team = fictiveTeamRepository.save(team);

		mockMvc.perform(delete("/v1/fictive-team/{uuid}", team.getGuid())).andExpect(status().isNoContent());

		assertThat(fictiveTeamRepository.existsById(team.getGuid())).isFalse();
	}

	@Test
	void getAllFictiveTeams_withExistingTeams_shouldReturnAllTeams() throws Exception {
		FictiveTeam team1 = new FictiveTeam();
		team1.setName("Team 1");
		team1.setOwnerId(UUID.randomUUID());
		FictiveTeam team2 = new FictiveTeam();
		team2.setOwnerId(UUID.randomUUID());
		team2.setName("Team 2");
		fictiveTeamRepository.saveAll(List.of(team1, team2));

		mockMvc.perform(get("/v1/fictive-team/")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(2));
	}

}
