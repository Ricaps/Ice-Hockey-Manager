package cz.fi.muni.pa165.worldlistservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.fi.muni.pa165.dto.worldlistservice.player.create.PlayerCreateDto;
import cz.fi.muni.pa165.dto.worldlistservice.player.update.PlayerUpdateDto;
import cz.fi.muni.pa165.worldlistservice.business.facades.interfaces.PlayerFacade;
import cz.fi.muni.pa165.worldlistservice.business.mappers.PlayerMapper;
import cz.fi.muni.pa165.worldlistservice.config.DisableSecurityTestConfig;
import cz.fi.muni.pa165.worldlistservice.persistence.repositories.PlayerCharacteristicRepository;
import cz.fi.muni.pa165.worldlistservice.persistence.repositories.PlayerRepository;
import cz.fi.muni.pa165.worldlistservice.persistence.repositories.TeamRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Import(DisableSecurityTestConfig.class)
class PlayerControllerIT {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private PlayerRepository playerRepository;

	@Autowired
	private PlayerCharacteristicRepository playerCharacteristicRepository;

	@Autowired
	private TeamRepository teamRepository;

	@Autowired
	private PlayerMapper playerMapper;

	@MockitoSpyBean
	private PlayerFacade playerFacade;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void getAllPlayer_validPage_shouldReturnPlayersPage() throws Exception {
		mockMvc.perform(get("/v1/players/").param("page", "0").param("size", "10"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").isArray())
			.andExpect(jsonPath("$.page.size").value(10))
			.andExpect(jsonPath("$.page.totalElements").value(playerRepository.count()))
			.andExpect(jsonPath("$.page.totalPages").value((int) Math.ceil(playerRepository.count() / 10.0)));
	}

	@Test
	void getAllPlayer_nonExistentPage_shouldReturnNotFound() throws Exception {
		mockMvc.perform(get("/v1/players/").param("page", "10").param("size", "10")).andExpect(status().isNotFound());

		verify(playerFacade, times(1)).findAll(any());
	}

	@Test
	void getPlayerById_existingId_shouldReturnTeam() throws Exception {
		var existing = playerMapper.toDetailModel(playerRepository.findAll().get(1));

		mockMvc.perform(get("/v1/players/{id}", existing.getId()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(existing.getId().toString()))
			.andExpect(jsonPath("$.firstName").value(existing.getFirstName()))
			.andExpect(jsonPath("$.lastName").value(existing.getLastName()))
			.andExpect(jsonPath("$.overallRating").value(existing.getOverallRating()))
			.andExpect(jsonPath("$.marketValue").value(existing.getMarketValue()))
			.andExpect(jsonPath("$.team.id").value(existing.getTeam().getId().toString()))
			.andExpect(jsonPath("$.team.name").value(existing.getTeam().getName()))
			.andExpect(
					jsonPath("$.team.championship.id").value(existing.getTeam().getChampionship().getId().toString()))
			.andExpect(jsonPath("$.team.championship.name").value(existing.getTeam().getChampionship().getName()))
			.andExpect(jsonPath("$.playerCharacteristics").isArray())
			.andExpect(jsonPath("$.playerCharacteristics.length()").value(existing.getPlayerCharacteristics().size()));
	}

	@Test
	void getPlayerById_nonExistingId_shouldReturnNotFound() throws Exception {
		mockMvc.perform(get("/v1/players/{id}", UUID.randomUUID())).andExpect(status().isNotFound());

		verify(playerFacade, times(1)).findById(any());
	}

	@Test
	void createPlayer_validData_shouldCreatePlayer() throws Exception {
		var playerTeam = teamRepository.findAll().get(1);
		var playerCharacteristic = playerCharacteristicRepository.findAll().getFirst();
		var createDto = new PlayerCreateDto("FName", "LName", 10, playerTeam.getId(), false,
				Set.of(playerCharacteristic.getId()));

		mockMvc
			.perform(post("/v1/players/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createDto)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.firstName").value(createDto.getFirstName()))
			.andExpect(jsonPath("$.lastName").value(createDto.getLastName()))
			.andExpect(jsonPath("$.marketValue").value(createDto.getMarketValue()))
			.andExpect(jsonPath("$.team.id").value(createDto.getTeamId().toString()))
			.andExpect(jsonPath("$.playerCharacteristics").isArray())
			.andExpect(
					jsonPath("$.playerCharacteristics.length()").value(createDto.getPlayerCharacteristicsIds().size()));

		assertThat(playerRepository.findAll())
			.anyMatch(team -> team.getFirstName().equals("FName") && team.getLastName().equals("LName"));
	}

	@Test
	void createPlayer_nonExistentTeam_shouldReturnNotFound() throws Exception {
		var playerCharacteristic = playerCharacteristicRepository.findAll().getFirst();
		var createDto = new PlayerCreateDto("Test", "Team", 10, UUID.randomUUID(), false,
				Set.of(playerCharacteristic.getId()));

		mockMvc
			.perform(post("/v1/players/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createDto)))
			.andExpect(status().isNotFound());

		verify(playerFacade, times(1)).create(any());
	}

	@Test
	void createPlayer_nonExistentPlayerCharacteristic_shouldReturnNotFound() throws Exception {
		var team = teamRepository.findAll().getFirst();
		var createDto = new PlayerCreateDto("Test", "Team", 10, team.getId(), false, Set.of(UUID.randomUUID()));

		mockMvc
			.perform(post("/v1/players/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createDto)))
			.andExpect(status().isNotFound());

		verify(playerFacade, times(1)).create(any());
	}

	@Test
	void createPlayer_firstNameTooShort_shouldReturnBadRequest() throws Exception {
		var playerTeam = teamRepository.findAll().get(1);
		var playerCharacteristic = playerCharacteristicRepository.findAll().getFirst();
		var createDto = new PlayerCreateDto("FN", "LName", 10, playerTeam.getId(), false,
				Set.of(playerCharacteristic.getId()));
		mockMvc
			.perform(post("/v1/players/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createDto)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.firstName").value("First name has to be longer than 2 characters"));
	}

	@Test
	void createPlayer_lastNameTooShort_shouldReturnBadRequest() throws Exception {
		var playerTeam = teamRepository.findAll().get(1);
		var playerCharacteristic = playerCharacteristicRepository.findAll().getFirst();
		var createDto = new PlayerCreateDto("FName", "LN", 10, playerTeam.getId(), false,
				Set.of(playerCharacteristic.getId()));
		mockMvc
			.perform(post("/v1/players/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createDto)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.lastName").value("Last name has to be longer than 2 characters"));
	}

	@Test
	void createPlayer_firstNameTooLong_shouldReturnBadRequest() throws Exception {
		var playerTeam = teamRepository.findAll().get(1);
		var playerCharacteristic = playerCharacteristicRepository.findAll().getFirst();
		var createDto = new PlayerCreateDto("FN".repeat(200), "LName", 10, playerTeam.getId(), false,
				Set.of(playerCharacteristic.getId()));
		mockMvc
			.perform(post("/v1/players/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createDto)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.firstName").value("First name cannot be longer than 100 characters"));
	}

	@Test
	void createPlayer_lastNameTooLong_shouldReturnBadRequest() throws Exception {
		var playerTeam = teamRepository.findAll().get(1);
		var playerCharacteristic = playerCharacteristicRepository.findAll().getFirst();
		var createDto = new PlayerCreateDto("FName", "LN".repeat(200), 10, playerTeam.getId(), false,
				Set.of(playerCharacteristic.getId()));
		mockMvc
			.perform(post("/v1/players/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createDto)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.lastName").value("Last name cannot be longer than 100 characters"));
	}

	@Test
	void createPlayer_teamNull_shouldCreatePlayer() throws Exception {
		var playerCharacteristic = playerCharacteristicRepository.findAll().getFirst();
		var createDto = new PlayerCreateDto("FName", "LName", 10, null, false, Set.of(playerCharacteristic.getId()));

		mockMvc
			.perform(post("/v1/players/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createDto)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.firstName").value(createDto.getFirstName()))
			.andExpect(jsonPath("$.lastName").value(createDto.getLastName()))
			.andExpect(jsonPath("$.marketValue").value(createDto.getMarketValue()))
			.andExpect(jsonPath("$.playerCharacteristics").isArray())
			.andExpect(
					jsonPath("$.playerCharacteristics.length()").value(createDto.getPlayerCharacteristicsIds().size()));

		assertThat(playerRepository.findAll())
			.anyMatch(team -> team.getFirstName().equals("FName") && team.getLastName().equals("LName"));
	}

	@Test
	void createPlayer_playerCharacteristicsNull_shouldReturnBadRequest() throws Exception {
		var team = teamRepository.findAll().getFirst();
		var createDto = new PlayerCreateDto("Test", "Team", 10, team.getId(), false, null);

		mockMvc
			.perform(post("/v1/players/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createDto)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.playerCharacteristicsIds").value(
					anyOf(is("Player characteristics cannot be null"), is("Player characteristics cannot be empty"))));
	}

	@Test
	void createPlayer_invalidJson_shouldReturnBadRequest() throws Exception {
		String invalidJson = "{}";

		mockMvc.perform(post("/v1/players/").contentType(MediaType.APPLICATION_JSON).content(invalidJson))
			.andExpect(status().isBadRequest());
	}

	@Test
	void updatePlayer_validData_shouldUpdatePlayer() throws Exception {
		var existingPlayer = playerRepository.findAll().get(1);
		var playerCharacteristic = playerCharacteristicRepository.findAll().get(1);

		var updateDto = new PlayerUpdateDto(existingPlayer.getId(), "Test", "Player", 10,
				existingPlayer.getTeam().getId(), false, Set.of(playerCharacteristic.getId()));

		mockMvc
			.perform(put("/v1/players/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(existingPlayer.getId().toString()))
			.andExpect(jsonPath("$.firstName").value(updateDto.getFirstName()))
			.andExpect(jsonPath("$.lastName").value(updateDto.getLastName()))
			.andExpect(jsonPath("$.marketValue").value(updateDto.getMarketValue()))
			.andExpect(jsonPath("$.team.id").value(updateDto.getTeamId().toString()))
			.andExpect(jsonPath("$.playerCharacteristics").isArray())
			.andExpect(
					jsonPath("$.playerCharacteristics.length()").value(updateDto.getPlayerCharacteristicsIds().size()));

		assertThat(playerRepository.findAll())
			.anyMatch(player -> player.getFirstName().equals("Test") && player.getLastName().equals("Player"));
	}

	@Test
	void updatePlayer_nonExistingId_shouldReturnNotFound() throws Exception {
		var existingPlayer = playerRepository.findAll().get(1);
		var playerCharacteristic = playerCharacteristicRepository.findAll().get(1);

		var updateDto = new PlayerUpdateDto(UUID.randomUUID(), "Test", "Player", 10, existingPlayer.getTeam().getId(),
				false, Set.of(playerCharacteristic.getId()));

		mockMvc
			.perform(put("/v1/players/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
			.andExpect(status().isNotFound());

		verify(playerFacade, times(1)).update(any());
	}

	@Test
	void updatePlayer_nonExistentTeam_shouldReturnNotFound() throws Exception {
		var existingPlayer = playerRepository.findAll().get(1);
		var playerCharacteristic = playerCharacteristicRepository.findAll().get(1);

		var updateDto = new PlayerUpdateDto(existingPlayer.getId(), "Test", "Player", 10, UUID.randomUUID(), false,
				Set.of(playerCharacteristic.getId()));

		mockMvc
			.perform(put("/v1/players/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
			.andExpect(status().isNotFound());

		verify(playerFacade, times(1)).update(any());
	}

	@Test
	void updatePlayer_nonExistentPlayerCharacteristic_shouldReturnNotFound() throws Exception {
		var existingPlayer = playerRepository.findAll().get(1);

		var updateDto = new PlayerUpdateDto(existingPlayer.getId(), "Test", "Player", 10,
				existingPlayer.getTeam().getId(), false, Set.of(UUID.randomUUID()));

		mockMvc
			.perform(put("/v1/players/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
			.andExpect(status().isNotFound());

		verify(playerFacade, times(1)).update(any());
	}

	@Test
	void updatePlayer_firstNameTooShort_shouldReturnBadRequest() throws Exception {
		var existingPlayer = playerRepository.findAll().get(1);
		var playerCharacteristic = playerCharacteristicRepository.findAll().get(1);

		var updateDto = new PlayerUpdateDto(existingPlayer.getId(), "Te", "Player", 10,
				existingPlayer.getTeam().getId(), false, Set.of(playerCharacteristic.getId()));

		mockMvc
			.perform(put("/v1/players/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.firstName").value("First name has to be longer than 2 characters"));

	}

	@Test
	void updatePlayer_lastNameTooShort_shouldReturnBadRequest() throws Exception {
		var existingPlayer = playerRepository.findAll().get(1);
		var playerCharacteristic = playerCharacteristicRepository.findAll().get(1);

		var updateDto = new PlayerUpdateDto(existingPlayer.getId(), "Test", "Pl", 10, existingPlayer.getTeam().getId(),
				false, Set.of(playerCharacteristic.getId()));

		mockMvc
			.perform(put("/v1/players/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.lastName").value("Last name has to be longer than 2 characters"));

	}

	@Test
	void updatePlayer_firstNameTooLong_shouldReturnBadRequest() throws Exception {
		var existingPlayer = playerRepository.findAll().get(1);
		var playerCharacteristic = playerCharacteristicRepository.findAll().get(1);

		var updateDto = new PlayerUpdateDto(existingPlayer.getId(), "Test".repeat(200), "Player", 10,
				existingPlayer.getTeam().getId(), false, Set.of(playerCharacteristic.getId()));

		mockMvc
			.perform(put("/v1/players/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.firstName").value("First name cannot be longer than 100 characters"));
	}

	@Test
	void updatePlayer_lastNameTooLong_shouldReturnBadRequest() throws Exception {
		var existingPlayer = playerRepository.findAll().get(1);
		var playerCharacteristic = playerCharacteristicRepository.findAll().get(1);

		var updateDto = new PlayerUpdateDto(existingPlayer.getId(), "Test", "Player".repeat(200), 10,
				existingPlayer.getTeam().getId(), false, Set.of(playerCharacteristic.getId()));

		mockMvc
			.perform(put("/v1/players/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.lastName").value("Last name cannot be longer than 100 characters"));
	}

	@Test
	void updatePlayer_teamNull_shouldUpdatePlayer() throws Exception {
		var existingPlayer = playerRepository.findAll().get(1);
		var playerCharacteristic = playerCharacteristicRepository.findAll().get(1);

		var updateDto = new PlayerUpdateDto(existingPlayer.getId(), "Test", "Player", 10, null, false,
				Set.of(playerCharacteristic.getId()));

		mockMvc
			.perform(put("/v1/players/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(existingPlayer.getId().toString()))
			.andExpect(jsonPath("$.firstName").value(updateDto.getFirstName()))
			.andExpect(jsonPath("$.lastName").value(updateDto.getLastName()))
			.andExpect(jsonPath("$.marketValue").value(updateDto.getMarketValue()))
			.andExpect(jsonPath("$.playerCharacteristics").isArray())
			.andExpect(
					jsonPath("$.playerCharacteristics.length()").value(updateDto.getPlayerCharacteristicsIds().size()));

		assertThat(playerRepository.findAll())
			.anyMatch(player -> player.getFirstName().equals("Test") && player.getLastName().equals("Player"));
	}

	@Test
	void updatePlayer_playerCharacteristicsNull_shouldReturnBadRequest() throws Exception {
		var existingPlayer = playerRepository.findAll().get(1);

		var updateDto = new PlayerUpdateDto(existingPlayer.getId(), "Test", "Player", 10,
				existingPlayer.getTeam().getId(), false, null);

		mockMvc
			.perform(put("/v1/players/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.playerCharacteristicsIds").value(
					anyOf(is("Player characteristics cannot be null"), is("Player characteristics cannot be empty"))));
	}

	@Test
	void updatePlayer_invalidJson_shouldReturnBadRequest() throws Exception {
		String invalidJson = "{}";

		mockMvc.perform(put("/v1/players/").contentType(MediaType.APPLICATION_JSON).content(invalidJson))
			.andExpect(status().isBadRequest());
	}

	@Test
	void deletePlayer_playerNotAssigned_shouldDeletePlayer() throws Exception {
		var player = playerRepository.findAll().getFirst();

		mockMvc.perform(delete("/v1/players/{id}", player.getId())).andExpect(status().isOk());

		assertThat(teamRepository.existsById(player.getId())).isFalse();
	}

	@Test
	void deletePlayer_PlayerAssigned_shouldReturnConflict() throws Exception {
		var player = playerRepository.findAll().get(1);

		mockMvc.perform(delete("/v1/players/{id}", player.getId())).andExpect(status().isConflict());
	}

	@Test
	void deletePlayer_nonExistingId_shouldReturnNotFound() throws Exception {
		mockMvc.perform(delete("/v1/players/{id}", UUID.randomUUID())).andExpect(status().isNotFound());

		verify(playerFacade, times(1)).delete(any());
	}

}
