package cz.fi.muni.pa165.worldlistservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.fi.muni.pa165.dto.worldlistservice.team.create.TeamCreateDto;
import cz.fi.muni.pa165.dto.worldlistservice.team.update.TeamUpdateDto;
import cz.fi.muni.pa165.worldlistservice.business.facades.interfaces.TeamFacade;
import cz.fi.muni.pa165.worldlistservice.business.mappers.TeamMapper;
import cz.fi.muni.pa165.worldlistservice.config.DisableSecurityTestConfig;
import cz.fi.muni.pa165.worldlistservice.persistence.entities.PlayerEntity;
import cz.fi.muni.pa165.worldlistservice.persistence.repositories.ChampionshipRepository;
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
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
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
class TeamControllerIT {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ChampionshipRepository championshipRepository;

	@Autowired
	private PlayerRepository playerRepository;

	@Autowired
	private TeamRepository teamRepository;

	@MockitoSpyBean
	private TeamFacade teamFacade;

	@Autowired
	private TeamMapper teamMapper;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void getAllTeams_validPage_shouldReturnTeamsPage() throws Exception {
		mockMvc.perform(get("/v1/teams/").param("page", "0").param("size", "10"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").isArray())
			.andExpect(jsonPath("$.page.size").value(10))
			.andExpect(jsonPath("$.page.totalElements").value(teamRepository.count()))
			.andExpect(jsonPath("$.page.totalPages").value((int) Math.ceil(teamRepository.count() / 10.0)));
	}

	@Test
	void getAllTeams_nonExistentPage_shouldReturnNotFound() throws Exception {
		mockMvc.perform(get("/v1/teams/").param("page", "10").param("size", "10")).andExpect(status().isNotFound());

		verify(teamFacade, times(1)).findAll(any());
	}

	@Test
	void getTeamById_existingId_shouldReturnTeam() throws Exception {
		var existing = teamMapper.toDetailModel(teamRepository.findAll().get(1));

		mockMvc.perform(get("/v1/teams/{id}", existing.getId()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(existing.getId().toString()))
			.andExpect(jsonPath("$.name").value(existing.getName()))
			.andExpect(jsonPath("$.championship.id").value(existing.getChampionship().getId().toString()))
			.andExpect(jsonPath("$.championship.name").value(existing.getChampionship().getName()))
			.andExpect(jsonPath("$.championship.championshipRegion.id")
				.value(existing.getChampionship().getChampionshipRegion().getId().toString()))
			.andExpect(jsonPath("$.championship.championshipRegion.name")
				.value(existing.getChampionship().getChampionshipRegion().getName()))

			.andExpect(jsonPath("$.championship.championshipRegion.type")
				.value(existing.getChampionship().getChampionshipRegion().getType().toString()))
			.andExpect(jsonPath("$.teamPlayers").isArray())
			.andExpect(jsonPath("$.teamPlayers.length()").value(existing.getTeamPlayers().size()));
	}

	@Test
	void getTeamById_nonExistingId_shouldReturnNotFound() throws Exception {
		mockMvc.perform(get("/v1/teams/{id}", UUID.randomUUID())).andExpect(status().isNotFound());

		verify(teamFacade, times(1)).findById(any());
	}

	@Test
	void createTeam_validData_shouldCreateTeam() throws Exception {
		var teamChampionship = championshipRepository.findAll().get(1);
		var teamPlayer = playerRepository.findAll().getFirst();
		var createDto = new TeamCreateDto("TestTeam", teamChampionship.getId(), Set.of(teamPlayer.getId()));

		mockMvc
			.perform(post("/v1/teams/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createDto)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.name").value(createDto.getName()))
			.andExpect(jsonPath("$.championship.id").value(teamChampionship.getId().toString()))
			.andExpect(jsonPath("$.championship.name").value(teamChampionship.getName()))
			.andExpect(jsonPath("$.championship.championshipRegion.id")
				.value(teamChampionship.getChampionshipRegion().getId().toString()))
			.andExpect(jsonPath("$.championship.championshipRegion.name")
				.value(teamChampionship.getChampionshipRegion().getName()))
			.andExpect(jsonPath("$.championship.championshipRegion.type")
				.value(teamChampionship.getChampionshipRegion().getType().toString()))
			.andExpect(jsonPath("$.teamPlayers").isArray())
			.andExpect(jsonPath("$.teamPlayers.length()").value(createDto.getTeamPlayersIds().size()));

		assertThat(teamRepository.findAll()).anyMatch(team -> team.getName().equals("TestTeam"));
	}

	@Test
	void createTeam_nonExistentChampionship_shouldReturnNotFound() throws Exception {
		var teamPlayer = playerRepository.findAll().getFirst();
		var createDto = new TeamCreateDto("TestTeam", UUID.randomUUID(), Set.of(teamPlayer.getId()));

		mockMvc
			.perform(post("/v1/teams/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createDto)))
			.andExpect(status().isNotFound());

		verify(teamFacade, times(1)).create(any());
	}

	@Test
	void createTeam_nonExistentPlayer_shouldReturnNotFound() throws Exception {
		var teamChampionship = championshipRepository.findAll().getFirst();
		var createDto = new TeamCreateDto("TestTeam", teamChampionship.getId(), Set.of(UUID.randomUUID()));

		mockMvc
			.perform(post("/v1/teams/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createDto)))
			.andExpect(status().isNotFound());

		verify(teamFacade, times(1)).create(any());
	}

	@Test
	void createTeam_nameTooShort_shouldReturnBadRequest() throws Exception {
		var teamChampionship = championshipRepository.findAll().getFirst();
		var teamPlayer = playerRepository.findAll().getFirst();
		var createDto = new TeamCreateDto("Te", teamChampionship.getId(), Set.of(teamPlayer.getId()));

		mockMvc
			.perform(post("/v1/teams/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createDto)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.name").value("Name has to be longer than 6 characters"));
	}

	@Test
	void createTeam_nameTooLong_shouldReturnBadRequest() throws Exception {
		var teamChampionship = championshipRepository.findAll().getFirst();
		var teamPlayer = playerRepository.findAll().getFirst();
		var createDto = new TeamCreateDto("N".repeat(300), teamChampionship.getId(), Set.of(teamPlayer.getId()));

		mockMvc
			.perform(post("/v1/teams/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createDto)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.name").value("Name cannot be longer than 100 characters"));
	}

	@Test
	void createTeam_championshipNull_shouldReturnBadRequest() throws Exception {
		var teamPlayer = playerRepository.findAll().getFirst();
		var createDto = new TeamCreateDto("TestTeam", null, Set.of(teamPlayer.getId()));

		mockMvc
			.perform(post("/v1/teams/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createDto)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.championshipId").value("Team has to be in championship"));
	}

	@Test
	void createTeam_playersNull_shouldCreateTeam() throws Exception {
		var teamChampionship = championshipRepository.findAll().get(1);
		var createDto = new TeamCreateDto("TestTeam", teamChampionship.getId(), null);

		mockMvc
			.perform(post("/v1/teams/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createDto)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.name").value(createDto.getName()))
			.andExpect(jsonPath("$.championship.id").value(teamChampionship.getId().toString()))
			.andExpect(jsonPath("$.championship.name").value(teamChampionship.getName()))
			.andExpect(jsonPath("$.championship.championshipRegion.id")
				.value(teamChampionship.getChampionshipRegion().getId().toString()))
			.andExpect(jsonPath("$.championship.championshipRegion.name")
				.value(teamChampionship.getChampionshipRegion().getName()))
			.andExpect(jsonPath("$.championship.championshipRegion.type")
				.value(teamChampionship.getChampionshipRegion().getType().toString()))
			.andExpect(jsonPath("$.teamPlayers").isArray())
			.andExpect(jsonPath("$.teamPlayers.length()").value(0));

		assertThat(teamRepository.findAll()).anyMatch(team -> team.getName().equals("TestTeam"));
	}

	@Test
	void createTeams_invalidJson_shouldReturnBadRequest() throws Exception {
		String invalidJson = "{}";

		mockMvc.perform(post("/v1/teams/").contentType(MediaType.APPLICATION_JSON).content(invalidJson))
			.andExpect(status().isBadRequest());
	}

	@Test
	void updateTeam_validData_shouldCreateTeam() throws Exception {
		var existingTeam = teamRepository.findAll().get(1);

		var updateDto = new TeamUpdateDto(existingTeam.getId(), "TestTeam", existingTeam.getChampionship().getId(),
				existingTeam.getTeamPlayers().stream().map(PlayerEntity::getId).collect(Collectors.toSet()));

		mockMvc
			.perform(put("/v1/teams/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(existingTeam.getId().toString()))
			.andExpect(jsonPath("$.name").value(updateDto.getName()))
			.andExpect(jsonPath("$.championship.id").value(updateDto.getChampionshipId().toString()))
			.andExpect(jsonPath("$.championship.name").value(existingTeam.getChampionship().getName()))
			.andExpect(jsonPath("$.championship.championshipRegion.id")
				.value(existingTeam.getChampionship().getChampionshipRegion().getId().toString()))
			.andExpect(jsonPath("$.championship.championshipRegion.name")
				.value(existingTeam.getChampionship().getChampionshipRegion().getName()))
			.andExpect(jsonPath("$.championship.championshipRegion.type")
				.value(existingTeam.getChampionship().getChampionshipRegion().getType().toString()))
			.andExpect(jsonPath("$.teamPlayers").isArray())
			.andExpect(jsonPath("$.teamPlayers.length()").value(updateDto.getTeamPlayersIds().size()));

		assertThat(teamRepository.findAll()).anyMatch(region -> region.getName().equals("TestTeam"));
	}

	@Test
	void updateTeam_nonExistingId_shouldReturnNotFound() throws Exception {
		var existingTeam = teamRepository.findAll().get(1);

		var updateDto = new TeamUpdateDto(UUID.randomUUID(), "TestTeam", existingTeam.getChampionship().getId(),
				existingTeam.getTeamPlayers().stream().map(PlayerEntity::getId).collect(Collectors.toSet()));

		mockMvc
			.perform(put("/v1/teams/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
			.andExpect(status().isNotFound());

		verify(teamFacade, times(1)).update(any());
	}

	@Test
	void updateTeam_nonExistentChampionship_shouldReturnNotFound() throws Exception {
		var existingTeam = teamRepository.findAll().get(1);

		var updateDto = new TeamUpdateDto(existingTeam.getId(), "TestTeam", UUID.randomUUID(),
				existingTeam.getTeamPlayers().stream().map(PlayerEntity::getId).collect(Collectors.toSet()));

		mockMvc
			.perform(put("/v1/teams/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
			.andExpect(status().isNotFound());

		verify(teamFacade, times(1)).update(any());
	}

	@Test
	void updateTeam_nonExistentPlayer_shouldReturnNotFound() throws Exception {
		var existingTeam = teamRepository.findAll().get(1);

		var updateDto = new TeamUpdateDto(existingTeam.getId(), "TestTeam", existingTeam.getChampionship().getId(),
				Set.of(UUID.randomUUID()));

		mockMvc
			.perform(put("/v1/teams/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
			.andExpect(status().isNotFound());

		verify(teamFacade, times(1)).update(any());
	}

	@Test
	void updateTeam_nameTooShort_shouldReturnBadRequest() throws Exception {
		var existingTeam = teamRepository.findAll().get(1);

		var updateDto = new TeamUpdateDto(existingTeam.getId(), "Na", existingTeam.getChampionship().getId(),
				existingTeam.getTeamPlayers().stream().map(PlayerEntity::getId).collect(Collectors.toSet()));

		mockMvc
			.perform(put("/v1/teams/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.name").value("Name has to be longer than 6 characters"));

	}

	@Test
	void updateTeam_nameTooLong_shouldReturnBadRequest() throws Exception {
		var existingTeam = teamRepository.findAll().get(1);

		var updateDto = new TeamUpdateDto(existingTeam.getId(), "N".repeat(300), existingTeam.getChampionship().getId(),
				existingTeam.getTeamPlayers().stream().map(PlayerEntity::getId).collect(Collectors.toSet()));

		mockMvc
			.perform(put("/v1/teams/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.name").value("Name cannot be longer than 100 characters"));
	}

	@Test
	void updateTeam_championshipNull_shouldReturnBadRequest() throws Exception {
		var existingTeam = teamRepository.findAll().get(1);

		var updateDto = new TeamUpdateDto(existingTeam.getId(), "TestTeam", null,
				existingTeam.getTeamPlayers().stream().map(PlayerEntity::getId).collect(Collectors.toSet()));

		mockMvc
			.perform(put("/v1/teams/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.championshipId").value("Team has to be in championship"));
	}

	@Test
	void updateTeam_teamsNull_shouldUpdateTeam() throws Exception {
		var existingTeam = teamRepository.findAll().get(1);

		var updateDto = new TeamUpdateDto(existingTeam.getId(), "TestTeam", existingTeam.getChampionship().getId(),
				null);

		mockMvc
			.perform(put("/v1/teams/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.name").value(updateDto.getName()))
			.andExpect(jsonPath("$.championship.id").value(existingTeam.getChampionship().getId().toString()))
			.andExpect(jsonPath("$.championship.name").value(existingTeam.getChampionship().getName()))
			.andExpect(jsonPath("$.championship.championshipRegion.id")
				.value(existingTeam.getChampionship().getChampionshipRegion().getId().toString()))
			.andExpect(jsonPath("$.championship.championshipRegion.name")
				.value(existingTeam.getChampionship().getChampionshipRegion().getName()))
			.andExpect(jsonPath("$.championship.championshipRegion.type")
				.value(existingTeam.getChampionship().getChampionshipRegion().getType().toString()))
			.andExpect(jsonPath("$.teamPlayers").isArray())
			.andExpect(jsonPath("$.teamPlayers.length()").value(0));

		assertThat(teamRepository.findAll()).anyMatch(team -> team.getName().equals("TestTeam"));
	}

	@Test
	void updateTeam_invalidJson_shouldReturnBadRequest() throws Exception {
		String invalidJson = "{}";

		mockMvc.perform(put("/v1/teams/").contentType(MediaType.APPLICATION_JSON).content(invalidJson))
			.andExpect(status().isBadRequest());
	}

	@Test
	void deleteTeam_teamNotAssigned_shouldDeleteChampionship() throws Exception {
		var team = teamRepository.findAll().getFirst();

		mockMvc.perform(delete("/v1/teams/{id}", team.getId())).andExpect(status().isOk());

		assertThat(teamRepository.existsById(team.getId())).isFalse();
	}

	@Test
	void deleteTeam_TeamAssigned_shouldReturnConflict() throws Exception {
		var team = teamRepository.findAll().get(1);

		mockMvc.perform(delete("/v1/teams/{id}", team.getId())).andExpect(status().isConflict());
	}

	@Test
	void deleteTeam_nonExistingId_shouldReturnNotFound() throws Exception {
		mockMvc.perform(delete("/v1/teams/{id}", UUID.randomUUID())).andExpect(status().isNotFound());

		verify(teamFacade, times(1)).delete(any());
	}

}
