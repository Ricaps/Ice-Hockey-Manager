package cz.fi.muni.pa165.worldlistservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.fi.muni.pa165.dto.worldlistservice.championshipregion.ChampionshipRegionDto;
import cz.fi.muni.pa165.dto.worldlistservice.championshipregion.create.ChampionshipRegionCreateDto;
import cz.fi.muni.pa165.enums.ChampionshipRegionType;
import cz.fi.muni.pa165.worldlistservice.business.facades.interfaces.ChampionshipRegionFacade;
import cz.fi.muni.pa165.worldlistservice.config.DisableSecurityTestConfig;
import cz.fi.muni.pa165.worldlistservice.persistence.repositories.ChampionshipRegionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

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
class ChampionshipRegionControllerIT {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ChampionshipRegionRepository championshipRegionRepository;

	@MockitoSpyBean
	private ChampionshipRegionFacade championshipRegionFacade;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void getAllChampionshipRegions_validPage_shouldReturnRegionsPage() throws Exception {
		mockMvc.perform(get("/v1/championship-regions/").param("page", "0").param("size", "10"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").isArray())
			.andExpect(jsonPath("$.page.size").value(10))
			.andExpect(jsonPath("$.page.totalElements").value(championshipRegionRepository.count()))
			.andExpect(
					jsonPath("$.page.totalPages").value((int) Math.ceil(championshipRegionRepository.count() / 10.0)));
	}

	@Test
	void getAllChampionshipRegions_nonExistentPage_shouldReturnNotFound() throws Exception {
		mockMvc.perform(get("/v1/championship-regions/").param("page", "10").param("size", "10"))
			.andExpect(status().isNotFound());

		verify(championshipRegionFacade, times(1)).findAll(any());
	}

	@Test
	void getChampionshipRegionById_existingId_shouldReturnRegion() throws Exception {
		var existing = championshipRegionRepository.findAll().getFirst();

		mockMvc.perform(get("/v1/championship-regions/{id}", existing.getId()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(existing.getId().toString()))
			.andExpect(jsonPath("$.name").value(existing.getName()))
			.andExpect(jsonPath("$.type").value(existing.getType().toString()));
	}

	@Test
	void getChampionshipRegionById_nonExistingId_shouldReturnNotFound() throws Exception {
		mockMvc.perform(get("/v1/championship-regions/{id}", UUID.randomUUID())).andExpect(status().isNotFound());

		verify(championshipRegionFacade, times(1)).findById(any());
	}

	@Test
	void createChampionshipRegion_validData_shouldCreateRegion() throws Exception {
		var createDto = new ChampionshipRegionCreateDto("TestRegion", ChampionshipRegionType.REGIONAL);

		mockMvc
			.perform(post("/v1/championship-regions/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createDto)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.name").value("TestRegion"))
			.andExpect(jsonPath("$.type").value("REGIONAL"));

		assertThat(championshipRegionRepository.findAll()).anyMatch(region -> region.getName().equals("TestRegion"));
	}

	@Test
	void createChampionshipRegion_nameTooShort_shouldReturnBadRequest() throws Exception {
		var createDto = new ChampionshipRegionCreateDto("Te", ChampionshipRegionType.REGIONAL);

		mockMvc
			.perform(post("/v1/championship-regions/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createDto)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.name").value("Name has to be longer than 3 characters"));
	}

	@Test
	void createChampionshipRegion_nameTooLong_shouldReturnBadRequest() throws Exception {
		var createDto = new ChampionshipRegionCreateDto("N".repeat(300), ChampionshipRegionType.REGIONAL);

		mockMvc
			.perform(post("/v1/championship-regions/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createDto)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.name").value("Name cannot be longer than 200 characters"));
	}

	@Test
	void createChampionshipRegion_invalidRegionType_shouldReturnBadRequest() throws Exception {
		String invalidTypeJson = "{\"name\": \"TestRegion\", \"regionType\": \"InvalidRegionType\"}";

		mockMvc
			.perform(post("/v1/championship-regions/").contentType(MediaType.APPLICATION_JSON).content(invalidTypeJson))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.type").value(anyOf(is("Invalid region type"), is("Region type must be defined"))));
	}

	@Test
	void createChampionshipRegion_invalidJson_shouldReturnBadRequest() throws Exception {
		String invalidJson = "{}";

		mockMvc.perform(post("/v1/championship-regions/").contentType(MediaType.APPLICATION_JSON).content(invalidJson))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.type")
				.value(anyOf(is("Invalid region type"), is("Region type must be defined"), is("Name cannot be null"))));
	}

	@Test
	void updateChampionshipRegion_nameTooShort_shouldReturnBadRequest() throws Exception {
		var existing = championshipRegionRepository.findAll().getFirst();
		var updateDto = new ChampionshipRegionDto(existing.getId(), "Na", existing.getType());

		mockMvc
			.perform(put("/v1/championship-regions/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.name").value("Name has to be longer than 3 characters"));
	}

	@Test
	void updateChampionshipRegion_nameTooLong_shouldReturnBadRequest() throws Exception {
		var existing = championshipRegionRepository.findAll().getFirst();
		var updateDto = new ChampionshipRegionDto(existing.getId(), "N".repeat(300), existing.getType());

		mockMvc
			.perform(put("/v1/championship-regions/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.name").value("Name cannot be longer than 200 characters"));
	}

	@Test
	void updateChampionshipRegion_validData_shouldUpdateRegion() throws Exception {
		var existing = championshipRegionRepository.findAll().getFirst();
		var updateDto = new ChampionshipRegionDto(existing.getId(), "UpdatedName", existing.getType());

		mockMvc
			.perform(put("/v1/championship-regions/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.name").value("UpdatedName"));

		var updated = championshipRegionRepository.findById(existing.getId()).orElseThrow();
		assertThat(updated.getName()).isEqualTo("UpdatedName");
	}

	@Test
	void updateChampionshipRegion_invalidRegionType_shouldReturnBadRequest() throws Exception {
		var existing = championshipRegionRepository.findAll().getFirst();
		String jsonWithInvalidRegionType = String.format(
				"{\"id\": \"%s\", \"name\": \"TestRegion\", \"regionType\": \"InvalidRegionType\"}", existing.getId());

		mockMvc
			.perform(post("/v1/championship-regions/").contentType(MediaType.APPLICATION_JSON)
				.content(jsonWithInvalidRegionType))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.type").value(anyOf(is("Invalid region type"), is("Region type must be defined"))));
	}

	@Test
	void updateChampionshipRegion_nonExistingId_shouldReturnNotFound() throws Exception {
		var updateDto = new ChampionshipRegionDto(UUID.randomUUID(), "UpdatedName", ChampionshipRegionType.REGIONAL);

		mockMvc
			.perform(put("/v1/championship-regions/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
			.andExpect(status().isNotFound());

		verify(championshipRegionFacade, times(1)).update(any());
	}

	@Test
	void deleteChampionshipRegion_regionNotAssigned_shouldDeleteRegion() throws Exception {
		var region = championshipRegionRepository.findAll()
			.stream()
			.filter(r -> "West Branden".equals(r.getName()))
			.findFirst()
			.orElseThrow();

		mockMvc.perform(delete("/v1/championship-regions/{id}", region.getId())).andExpect(status().isOk());

		assertThat(championshipRegionRepository.existsById(region.getId())).isFalse();
	}

	@Test
	void deleteChampionshipRegion_regionAssigned_shouldReturnConflict() throws Exception {
		var region = championshipRegionRepository.findAll()
			.stream()
			.filter(r -> "West Aleasestad".equals(r.getName()))
			.findFirst()
			.orElseThrow();

		mockMvc.perform(delete("/v1/championship-regions/{id}", region.getId())).andExpect(status().isConflict());
	}

	@Test
	void deleteChampionshipRegion_nonExistingId_shouldReturnNotFound() throws Exception {
		mockMvc.perform(delete("/v1/championship-regions/{id}", UUID.randomUUID())).andExpect(status().isNotFound());

		verify(championshipRegionFacade, times(1)).delete(any());
	}

}
