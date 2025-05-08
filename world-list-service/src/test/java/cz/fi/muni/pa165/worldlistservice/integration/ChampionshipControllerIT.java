package cz.fi.muni.pa165.worldlistservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.fi.muni.pa165.dto.worldlistservice.championship.create.ChampionshipCreateDto;
import cz.fi.muni.pa165.dto.worldlistservice.championship.update.ChampionshipUpdateDto;
import cz.fi.muni.pa165.worldlistservice.business.facades.interfaces.ChampionshipFacade;
import cz.fi.muni.pa165.worldlistservice.business.mappers.ChampionshipMapper;
import cz.fi.muni.pa165.worldlistservice.config.DisableSecurityTestConfig;
import cz.fi.muni.pa165.worldlistservice.persistence.entities.TeamEntity;
import cz.fi.muni.pa165.worldlistservice.persistence.repositories.ChampionshipRegionRepository;
import cz.fi.muni.pa165.worldlistservice.persistence.repositories.ChampionshipRepository;
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
class ChampionshipControllerIT {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ChampionshipRepository championshipRepository;

	@Autowired
	private ChampionshipRegionRepository championshipRegionRepository;

	@Autowired
	private TeamRepository teamRepository;

	@Autowired
	private ChampionshipMapper championshipMapper;

	@MockitoSpyBean
	private ChampionshipFacade championshipFacade;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void getAllChampionships_validPage_shouldReturnChampionshipsPage() throws Exception {
		mockMvc.perform(get("/v1/championships/").param("page", "0").param("size", "10"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").isArray())
			.andExpect(jsonPath("$.page.size").value(10))
			.andExpect(jsonPath("$.page.totalElements").value(championshipRepository.count()))
			.andExpect(jsonPath("$.page.totalPages").value((int) Math.ceil(championshipRepository.count() / 10.0)));
	}

	@Test
	void getAllChampionships_nonExistentPage_shouldReturnNotFound() throws Exception {
		mockMvc.perform(get("/v1/championships/").param("page", "10").param("size", "10"))
			.andExpect(status().isNotFound());

		verify(championshipFacade, times(1)).findAll(any());
	}

	@Test
	void getChampionshipById_existingId_shouldReturnChampionship() throws Exception {
		var existing = championshipMapper.toDetailModel(championshipRepository.findAll().get(1));

		mockMvc.perform(get("/v1/championships/{id}", existing.getId()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(existing.getId().toString()))
			.andExpect(jsonPath("$.name").value(existing.getName()))
			.andExpect(jsonPath("$.championshipRegion.id").value(existing.getChampionshipRegion().getId().toString()))
			.andExpect(jsonPath("$.championshipRegion.name").value(existing.getChampionshipRegion().getName()))
			.andExpect(
					jsonPath("$.championshipRegion.type").value(existing.getChampionshipRegion().getType().toString()))
			.andExpect(jsonPath("$.championshipTeams").isArray())
			.andExpect(jsonPath("$.championshipTeams.length()").value(existing.getChampionshipTeams().size()));
	}

	@Test
	void getChampionshipById_nonExistingId_shouldReturnNotFound() throws Exception {
		mockMvc.perform(get("/v1/championships/{id}", UUID.randomUUID())).andExpect(status().isNotFound());
		verify(championshipFacade, times(1)).findById(any());
	}

	@Test
	void createChampionship_validData_shouldCreateChampionship() throws Exception {
		var championshipRegion = championshipRegionRepository.findAll().getFirst();
		var championshipTeam = teamRepository.findAll().getFirst();
		var createDto = new ChampionshipCreateDto("TestRegion", championshipRegion.getId(),
				Set.of(championshipTeam.getId()));

		mockMvc
			.perform(post("/v1/championships/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createDto)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.name").value(createDto.getName()))
			.andExpect(jsonPath("$.championshipRegion.id").value(championshipRegion.getId().toString()))
			.andExpect(jsonPath("$.championshipRegion.name").value(championshipRegion.getName()))
			.andExpect(jsonPath("$.championshipRegion.type").value(championshipRegion.getType().toString()))
			.andExpect(jsonPath("$.championshipTeams").isArray())
			.andExpect(jsonPath("$.championshipTeams.length()").value(createDto.getChampionshipTeamsIds().size()))
			.andExpect(jsonPath("$.championshipTeams[0].id").value(championshipTeam.getId().toString()));

		assertThat(championshipRepository.findAll()).anyMatch(region -> region.getName().equals("TestRegion"));
	}

	@Test
	void createChampionship_nonExistentRegion_shouldReturnNotFound() throws Exception {
		var championshipRegion = championshipRegionRepository.findAll().getFirst();
		var createDto = new ChampionshipCreateDto("TestRegion", championshipRegion.getId(), Set.of(UUID.randomUUID()));

		mockMvc
			.perform(post("/v1/championships/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createDto)))
			.andExpect(status().isNotFound());

		verify(championshipFacade, times(1)).create(any());
	}

	@Test
	void createChampionship_nonExistentTeam_shouldReturnNotFound() throws Exception {
		var championshipTeam = teamRepository.findAll().getFirst();
		var createDto = new ChampionshipCreateDto("TestRegion", UUID.randomUUID(), Set.of(championshipTeam.getId()));

		mockMvc
			.perform(post("/v1/championships/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createDto)))
			.andExpect(status().isNotFound());

		verify(championshipFacade, times(1)).create(any());
	}

	@Test
	void createChampionship_nameTooShort_shouldReturnBadRequest() throws Exception {
		var championshipRegion = championshipRegionRepository.findAll().getFirst();
		var championshipTeam = teamRepository.findAll().getFirst();
		var createDto = new ChampionshipCreateDto("Te", championshipRegion.getId(), Set.of(championshipTeam.getId()));

		mockMvc
			.perform(post("/v1/championships/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createDto)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.name").value("Name has to be longer than 3 characters"));
	}

	@Test
	void createChampionship_nameTooLong_shouldReturnBadRequest() throws Exception {
		var championshipRegion = championshipRegionRepository.findAll().getFirst();
		var championshipTeam = teamRepository.findAll().getFirst();
		var createDto = new ChampionshipCreateDto("N".repeat(300), championshipRegion.getId(),
				Set.of(championshipTeam.getId()));

		mockMvc
			.perform(post("/v1/championships/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createDto)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.name").value("Name cannot be longer than 200 characters"));
	}

	@Test
	void createChampionship_regionNull_shouldReturnBadRequest() throws Exception {
		var championshipTeam = teamRepository.findAll().getFirst();
		var createDto = new ChampionshipCreateDto("TestRegion", null, Set.of(championshipTeam.getId()));

		mockMvc
			.perform(post("/v1/championships/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createDto)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.championshipRegionId").value("Championship has to have region assigned"));
	}

	@Test
	void createChampionship_teamsNull_shouldReturnBadRequest() throws Exception {
		var championshipRegion = championshipRegionRepository.findAll().getFirst();
		var createDto = new ChampionshipCreateDto("TestRegion", championshipRegion.getId(), null);

		mockMvc
			.perform(post("/v1/championships/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createDto)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.championshipTeamsIds").value("Championship cannot have empty team list"));
	}

	@Test
	void createChampionship_invalidJson_shouldReturnBadRequest() throws Exception {
		String invalidJson = "{}";

		mockMvc.perform(post("/v1/championships/").contentType(MediaType.APPLICATION_JSON).content(invalidJson))
			.andExpect(status().isBadRequest());
	}

	@Test
	void updateChampionship_validData_shouldCreateChampionship() throws Exception {
		var existingChampionship = championshipRepository.findAll().get(1);

		var updateDto = new ChampionshipUpdateDto(existingChampionship.getId(), "TestRegion",
				existingChampionship.getChampionshipRegion().getId(),
				existingChampionship.getChampionshipTeams()
					.stream()
					.map(TeamEntity::getId)
					.collect(Collectors.toSet()));

		mockMvc
			.perform(put("/v1/championships/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(existingChampionship.getId().toString()))
			.andExpect(jsonPath("$.name").value(updateDto.getName()))
			.andExpect(jsonPath("$.championshipRegion.id")
				.value(existingChampionship.getChampionshipRegion().getId().toString()))
			.andExpect(
					jsonPath("$.championshipRegion.name").value(existingChampionship.getChampionshipRegion().getName()))
			.andExpect(jsonPath("$.championshipRegion.type")
				.value(existingChampionship.getChampionshipRegion().getType().toString()))
			.andExpect(jsonPath("$.championshipTeams").isArray())
			.andExpect(
					jsonPath("$.championshipTeams.length()").value(existingChampionship.getChampionshipTeams().size()));

		assertThat(championshipRepository.findAll()).anyMatch(region -> region.getName().equals("TestRegion"));
	}

	@Test
	void updateChampionship_nonExistingId_shouldReturnNotFound() throws Exception {
		var existingChampionship = championshipRepository.findAll().get(1);

		var updateDto = new ChampionshipUpdateDto(UUID.randomUUID(), "TestRegion",
				existingChampionship.getChampionshipRegion().getId(),
				existingChampionship.getChampionshipTeams()
					.stream()
					.map(TeamEntity::getId)
					.collect(Collectors.toSet()));

		mockMvc
			.perform(put("/v1/championships/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
			.andExpect(status().isNotFound());

		verify(championshipFacade, times(1)).update(any());
	}

	@Test
	void updateChampionship_nonExistentRegion_shouldReturnNotFound() throws Exception {
		var existingChampionship = championshipRepository.findAll().get(1);

		var updateDto = new ChampionshipUpdateDto(existingChampionship.getId(), existingChampionship.getName(),
				UUID.randomUUID(),
				existingChampionship.getChampionshipTeams()
					.stream()
					.map(TeamEntity::getId)
					.collect(Collectors.toSet()));

		mockMvc
			.perform(put("/v1/championships/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
			.andExpect(status().isNotFound());

		verify(championshipFacade, times(1)).update(any());
	}

	@Test
	void updateChampionship_nonExistentTeam_shouldReturnNotFound() throws Exception {
		var existingChampionship = championshipRepository.findAll().get(1);

		var updateDto = new ChampionshipUpdateDto(existingChampionship.getId(), existingChampionship.getName(),
				existingChampionship.getChampionshipRegion().getId(), Set.of(UUID.randomUUID()));

		mockMvc
			.perform(put("/v1/championships/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
			.andExpect(status().isNotFound());

		verify(championshipFacade, times(1)).update(any());
	}

	@Test
	void updateChampionship_nameTooShort_shouldReturnBadRequest() throws Exception {
		var existingChampionship = championshipRepository.findAll().get(1);

		var updateDto = new ChampionshipUpdateDto(existingChampionship.getId(), "Na",
				existingChampionship.getChampionshipRegion().getId(),
				existingChampionship.getChampionshipTeams()
					.stream()
					.map(TeamEntity::getId)
					.collect(Collectors.toSet()));

		mockMvc
			.perform(put("/v1/championships/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.name").value("Name has to be longer than 3 characters"));
	}

	@Test
	void updateChampionship_nameTooLong_shouldReturnBadRequest() throws Exception {
		var existingChampionship = championshipRepository.findAll().get(1);

		var updateDto = new ChampionshipUpdateDto(existingChampionship.getId(), "N".repeat(300),
				existingChampionship.getChampionshipRegion().getId(),
				existingChampionship.getChampionshipTeams()
					.stream()
					.map(TeamEntity::getId)
					.collect(Collectors.toSet()));

		mockMvc
			.perform(put("/v1/championships/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.name").value("Name cannot be longer than 200 characters"));
	}

	@Test
	void updateChampionship_regionNull_shouldReturnBadRequest() throws Exception {
		var existingChampionship = championshipRepository.findAll().get(1);

		var updateDto = new ChampionshipUpdateDto(existingChampionship.getId(), "Name", null,
				existingChampionship.getChampionshipTeams()
					.stream()
					.map(TeamEntity::getId)
					.collect(Collectors.toSet()));

		mockMvc
			.perform(put("/v1/championships/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.championshipRegionId").value("Championship has to have region assigned"));
	}

	@Test
	void updateChampionship_teamsNull_shouldReturnBadRequest() throws Exception {
		var existingChampionship = championshipRepository.findAll().get(1);

		var updateDto = new ChampionshipUpdateDto(existingChampionship.getId(), "Na",
				existingChampionship.getChampionshipRegion().getId(), null);

		mockMvc
			.perform(put("/v1/championships/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.championshipTeamsIds").value("Championship cannot have empty team list"));
	}

	@Test
	void updateChampionship_invalidJson_shouldReturnBadRequest() throws Exception {
		String invalidJson = "{}";

		mockMvc.perform(put("/v1/championships/").contentType(MediaType.APPLICATION_JSON).content(invalidJson))
			.andExpect(status().isBadRequest());
	}

	@Test
	void deleteChampionship_championshipNotAssigned_shouldDeleteChampionship() throws Exception {
		var championship = championshipRepository.findAll().getFirst();

		mockMvc.perform(delete("/v1/championships/{id}", championship.getId())).andExpect(status().isOk());

		assertThat(championshipRepository.existsById(championship.getId())).isFalse();
	}

	@Test
	void deleteChampionship_TeamAssigned_shouldReturnConflict() throws Exception {
		var championship = championshipRepository.findAll().get(1);

		mockMvc.perform(delete("/v1/championships/{id}", championship.getId())).andExpect(status().isConflict());
	}

	@Test
	void deleteChampionship_nonExistingId_shouldReturnNotFound() throws Exception {
		mockMvc.perform(delete("/v1/championships/{id}", UUID.randomUUID())).andExpect(status().isNotFound());
		verify(championshipFacade, times(1)).delete(any());
	}

}
