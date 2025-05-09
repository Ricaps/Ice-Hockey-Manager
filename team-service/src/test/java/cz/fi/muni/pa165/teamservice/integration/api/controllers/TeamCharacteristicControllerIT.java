package cz.fi.muni.pa165.teamservice.integration.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.fi.muni.pa165.dto.teamService.TeamCharacteristicCreateDTO;
import cz.fi.muni.pa165.dto.teamService.TeamCharacteristicUpdateDTO;
import cz.fi.muni.pa165.enums.TeamCharacteristicType;
import cz.fi.muni.pa165.teamservice.config.DisableSecurityTestConfig;
import cz.fi.muni.pa165.teamservice.persistence.entities.TeamCharacteristic;
import cz.fi.muni.pa165.teamservice.persistence.repositories.TeamCharacteristicRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

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
@Import({ DisableSecurityTestConfig.class })
@Transactional
class TeamCharacteristicControllerIT {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private TeamCharacteristicRepository teamCharacteristicRepository;

	@Autowired
	private ObjectMapper objectMapper;

	@BeforeEach
	public void setup() {
		teamCharacteristicRepository.deleteAll();
	}

	@Test
	void createTeamCharacteristic_withValidData_shouldReturnCreated() throws Exception {
		TeamCharacteristicCreateDTO createDTO = new TeamCharacteristicCreateDTO();
		createDTO.setTeamId(UUID.randomUUID());
		createDTO.setCharacteristicType(TeamCharacteristicType.COLLABORATION);
		createDTO.setCharacteristicValue(85);

		mockMvc
			.perform(post("/api/team-characteristics").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createDTO)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.characteristicType").value("COLLABORATION"))
			.andExpect(jsonPath("$.characteristicValue").value(85));

		assertThat(teamCharacteristicRepository.findAll()).hasSize(1);
	}

	@Test
	void updateTeamCharacteristic_withValidUpdate_shouldReturnUpdatedCharacteristic() throws Exception {
		TeamCharacteristic characteristic = new TeamCharacteristic();
		characteristic.setTeamId(UUID.randomUUID());
		characteristic.setCharacteristicType(TeamCharacteristicType.COLLABORATION);
		characteristic.setCharacteristicValue(50);
		characteristic = teamCharacteristicRepository.save(characteristic);

		TeamCharacteristicUpdateDTO updateDTO = new TeamCharacteristicUpdateDTO();
		updateDTO.setGuid(characteristic.getGuid());
		updateDTO.setCharacteristicType(TeamCharacteristicType.SPEED);
		updateDTO.setCharacteristicValue(75);

		mockMvc
			.perform(put("/api/team-characteristics/{id}", characteristic.getGuid())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDTO)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.characteristicType").value("SPEED"))
			.andExpect(jsonPath("$.characteristicValue").value(75));

		TeamCharacteristic updated = teamCharacteristicRepository.findById(characteristic.getGuid()).orElseThrow();
		assertThat(updated.getCharacteristicType()).isEqualTo(TeamCharacteristicType.SPEED);
		assertThat(updated.getCharacteristicValue()).isEqualTo(75);
	}

	@Test
	void getTeamCharacteristic_withExistingId_shouldReturnCharacteristic() throws Exception {
		TeamCharacteristic characteristic = new TeamCharacteristic();
		characteristic.setTeamId(UUID.randomUUID());
		characteristic.setCharacteristicType(TeamCharacteristicType.COLLABORATION);
		characteristic.setCharacteristicValue(90);
		characteristic = teamCharacteristicRepository.save(characteristic);

		mockMvc.perform(get("/api/team-characteristics/{id}", characteristic.getGuid()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.guid").value(characteristic.getGuid().toString()))
			.andExpect(jsonPath("$.characteristicType").value("COLLABORATION"))
			.andExpect(jsonPath("$.characteristicValue").value(90));
	}

	@Test
	void getTeamCharacteristic_withSpecialId_shouldReturnPredefinedCharacteristic() throws Exception {
		UUID specialId = UUID.fromString("588a699c-f622-4ff4-8933-fcfae7963e50");

		mockMvc.perform(get("/api/team-characteristics/{id}", specialId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.characteristicType").value("COLLABORATION"));
	}

	@Test
	void deleteTeamCharacteristic_withExistingId_shouldReturnNoContent() throws Exception {
		TeamCharacteristic characteristic = new TeamCharacteristic();
		characteristic.setTeamId(UUID.randomUUID());
		characteristic.setCharacteristicType(TeamCharacteristicType.COLLABORATION);
		characteristic = teamCharacteristicRepository.save(characteristic);

		mockMvc.perform(delete("/api/team-characteristics/{id}", characteristic.getGuid()))
			.andExpect(status().isNoContent());

		assertThat(teamCharacteristicRepository.existsById(characteristic.getGuid())).isFalse();
	}

	@Test
	void findByTeamId_withExistingTeamId_shouldReturnAllCharacteristics() throws Exception {
		UUID teamId = UUID.randomUUID();

		TeamCharacteristic characteristic1 = new TeamCharacteristic();
		characteristic1.setTeamId(teamId);
		characteristic1.setCharacteristicType(TeamCharacteristicType.COLLABORATION);

		TeamCharacteristic characteristic2 = new TeamCharacteristic();
		characteristic2.setTeamId(teamId);
		characteristic2.setCharacteristicType(TeamCharacteristicType.SPEED);

		teamCharacteristicRepository.saveAll(List.of(characteristic1, characteristic2));

		mockMvc.perform(get("/api/team-characteristics/team/{teamId}", teamId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(2));
	}

}
