package cz.fi.muni.pa165.teamservice.integration.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.fi.muni.pa165.dto.teamservice.TeamCharacteristicCreateDTO;
import cz.fi.muni.pa165.dto.teamservice.TeamCharacteristicUpdateDTO;
import cz.fi.muni.pa165.enums.TeamCharacteristicType;
import cz.fi.muni.pa165.teamservice.config.DisableSecurityTestConfig;
import cz.fi.muni.pa165.teamservice.persistence.entities.FictiveTeam;
import cz.fi.muni.pa165.teamservice.persistence.entities.TeamCharacteristic;
import cz.fi.muni.pa165.teamservice.persistence.repositories.FictiveTeamRepository;
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

	private final UUID teamId = UUID.randomUUID();

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private TeamCharacteristicRepository teamCharacteristicRepository;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private FictiveTeamRepository fictiveTeamRepository;

	@BeforeEach
	public void setup() {
		teamCharacteristicRepository.deleteAll();
	}

	@Test
	void createTeamCharacteristic_withValidData_shouldReturnCreated() throws Exception {
		TeamCharacteristicCreateDTO createDTO = new TeamCharacteristicCreateDTO();
		createDTO.setCharacteristicType(TeamCharacteristicType.COLLABORATION);
		createDTO.setCharacteristicValue(85);

		mockMvc
			.perform(post("/v1/team-characteristics").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createDTO)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.characteristicType").value("COLLABORATION"))
			.andExpect(jsonPath("$.characteristicValue").value(85));

		assertThat(teamCharacteristicRepository.findAll()).hasSize(1);
	}

	@Test
	void updateTeamCharacteristic_withValidUpdate_shouldReturnUpdatedCharacteristic() throws Exception {
		FictiveTeam team = new FictiveTeam();
		team.setOwnerId(UUID.randomUUID());
		team.setName("Team");

		var teamEntity = fictiveTeamRepository.save(team);
		TeamCharacteristic characteristic = new TeamCharacteristic();
		characteristic.setFictiveTeam(teamEntity);
		characteristic.setCharacteristicType(TeamCharacteristicType.COLLABORATION);
		characteristic.setCharacteristicValue(50);
		characteristic = teamCharacteristicRepository.save(characteristic);

		TeamCharacteristicUpdateDTO updateDTO = new TeamCharacteristicUpdateDTO();
		updateDTO.setGuid(characteristic.getGuid());
		updateDTO.setCharacteristicType(TeamCharacteristicType.SPEED);
		updateDTO.setCharacteristicValue(75);

		mockMvc
			.perform(put("/v1/team-characteristics/{id}", characteristic.getGuid())
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
		FictiveTeam team = new FictiveTeam();
		team.setGuid(teamId);
		TeamCharacteristic characteristic = new TeamCharacteristic();
		characteristic.setFictiveTeam(team);
		characteristic.setCharacteristicType(TeamCharacteristicType.COLLABORATION);
		characteristic.setCharacteristicValue(90);
		characteristic = teamCharacteristicRepository.save(characteristic);

		mockMvc.perform(get("/v1/team-characteristics/{id}", characteristic.getGuid()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.guid").value(characteristic.getGuid().toString()))
			.andExpect(jsonPath("$.characteristicType").value("COLLABORATION"))
			.andExpect(jsonPath("$.characteristicValue").value(90));
	}

	@Test
	void getTeamCharacteristic_withSpecialId_shouldReturnPredefinedCharacteristic() throws Exception {
		UUID specialId = UUID.fromString("588a699c-f622-4ff4-8933-fcfae7963e50");

		mockMvc.perform(get("/v1/team-characteristics/{id}", specialId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.characteristicType").value("COLLABORATION"));
	}

	@Test
	void deleteTeamCharacteristic_withExistingId_shouldReturnNoContent() throws Exception {
		FictiveTeam team = new FictiveTeam();
		team.setOwnerId(UUID.randomUUID());
		team.setName("Team");

		var teamEntity = fictiveTeamRepository.save(team);

		TeamCharacteristic characteristic = new TeamCharacteristic();
		characteristic.setFictiveTeam(teamEntity);
		characteristic.setCharacteristicType(TeamCharacteristicType.COLLABORATION);
		characteristic = teamCharacteristicRepository.save(characteristic);

		mockMvc.perform(delete("/v1/team-characteristics/{id}", characteristic.getGuid()))
			.andExpect(status().isNoContent());

		assertThat(teamCharacteristicRepository.existsById(characteristic.getGuid())).isFalse();
	}

	@Test
	void findByTeamId_withExistingTeamId_shouldReturnAllCharacteristics() throws Exception {
		FictiveTeam team = new FictiveTeam();
		team.setOwnerId(UUID.randomUUID());
		team.setName("Team");

		var teamEntity = fictiveTeamRepository.save(team);

		TeamCharacteristic characteristic1 = new TeamCharacteristic();
		characteristic1.setFictiveTeam(teamEntity);
		characteristic1.setCharacteristicType(TeamCharacteristicType.COLLABORATION);

		TeamCharacteristic characteristic2 = new TeamCharacteristic();
		characteristic2.setFictiveTeam(teamEntity);
		characteristic2.setCharacteristicType(TeamCharacteristicType.SPEED);

		teamCharacteristicRepository.saveAll(List.of(characteristic1, characteristic2));

		mockMvc.perform(get("/v1/team-characteristics/team/{teamId}", teamEntity.getGuid()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(2));
	}

}
