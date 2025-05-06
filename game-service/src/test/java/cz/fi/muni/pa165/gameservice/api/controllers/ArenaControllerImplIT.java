package cz.fi.muni.pa165.gameservice.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.fi.muni.pa165.dto.gameservice.ArenaCreateDto;
import cz.fi.muni.pa165.dto.gameservice.ArenaViewDto;
import cz.fi.muni.pa165.gameservice.api.exception.ResourceNotFoundException;
import cz.fi.muni.pa165.gameservice.config.DisableSecurityTestConfig;
import cz.fi.muni.pa165.gameservice.config.ObjectMapperConfig;
import cz.fi.muni.pa165.gameservice.persistence.entities.Arena;
import cz.fi.muni.pa165.gameservice.persistence.repositories.ArenaRepository;
import cz.fi.muni.pa165.gameservice.testdata.ArenaTestData;
import cz.fi.muni.pa165.gameservice.testdata.factory.ArenaITDataFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static cz.fi.muni.pa165.gameservice.utils.Assertions.exception;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Import({ ObjectMapperConfig.class, DisableSecurityTestConfig.class })
class ArenaControllerImplIT {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ArenaRepository arenaRepository;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private ArenaITDataFactory arenaITDataFactory;

	@Test
	void createArena_validData_arenaCreated() throws Exception {
		var arenaCreate = ArenaTestData.getArenaForCreate();

		var response = mockMvc
			.perform(post("/v1/arena/").contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objectMapper.writeValueAsString(arenaCreate)))
			.andExpect(status().isCreated())
			.andReturn()
			.getResponse()
			.getContentAsString();

		var arena = objectMapper.readValue(response, ArenaViewDto.class);
		assertArena(arena, arenaCreate);

		var arenaEntity = arenaRepository.findById(arena.getGuid());
		assertThat(arenaEntity).isPresent();
		assertArena(arenaEntity.get(), arenaCreate);
	}

	@Test
	void updateArena_validData_arenaUpdated() throws Exception {
		var existingArena = arenaITDataFactory.getArena();
		var arenaUpdate = ArenaTestData.getArenaForCreate();

		var response = mockMvc
			.perform(put("/v1/arena/{arenaUUID}", existingArena.getGuid()).contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objectMapper.writeValueAsString(arenaUpdate)))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();

		var arena = objectMapper.readValue(response, ArenaViewDto.class);
		assertArena(arena, arenaUpdate);

		var arenaEntity = arenaRepository.findById(existingArena.getGuid());
		assertThat(arenaEntity).isPresent();
		assertArena(arenaEntity.get(), arenaUpdate);
	}

	@Test
	void updateArena_notExists_returns404() throws Exception {
		var arenaUpdate = ArenaTestData.getArenaForCreate();

		mockMvc
			.perform(put("/v1/arena/{arenaUUID}", UUID.randomUUID()).contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objectMapper.writeValueAsString(arenaUpdate)))
			.andExpect(status().isNotFound())
			.andExpect(exception().isInstanceOf(ResourceNotFoundException.class));
	}

	@Test
	void deleteArena_notExists_returns404() throws Exception {
		mockMvc.perform(delete("/v1/arena/{arenaUUID}", UUID.randomUUID()))
			.andExpect(status().isNotFound())
			.andExpect(exception().isInstanceOf(ResourceNotFoundException.class));
	}

	@Test
	void deleteArena_arenaUsed_notDeleted() throws Exception {
		var existingArena = arenaITDataFactory.getUsedArena();

		mockMvc.perform(delete("/v1/arena/{arenaUUID}", existingArena.getGuid())).andExpect(status().isConflict());

		var arenaExists = arenaRepository.existsById(existingArena.getGuid());
		assertThat(arenaExists).isTrue();
	}

	@Test
	void deleteArena_arenaNotUsed_deleted() throws Exception {
		var existingArena = arenaRepository.save(ArenaTestData.getArena());

		mockMvc.perform(delete("/v1/arena/{arenaUUID}", existingArena.getGuid())).andExpect(status().isNoContent());

		var arenaExists = arenaRepository.existsById(existingArena.getGuid());
		assertThat(arenaExists).isFalse();
	}

	private void assertArena(ArenaViewDto arenaViewDto, ArenaCreateDto arenaCreateDto) {
		assertThat(arenaViewDto.getArenaName()).isEqualTo(arenaCreateDto.getArenaName());
		assertThat(arenaViewDto.getCountryCode()).isEqualTo(arenaCreateDto.getCountryCode());
		assertThat(arenaViewDto.getCityName()).isEqualTo(arenaCreateDto.getCityName());
	}

	private void assertArena(Arena arenaEntity, ArenaCreateDto arenaCreateDto) {
		assertThat(arenaEntity.getArenaName()).isEqualTo(arenaCreateDto.getArenaName());
		assertThat(arenaEntity.getCountryCode()).isEqualTo(arenaCreateDto.getCountryCode());
		assertThat(arenaEntity.getCityName()).isEqualTo(arenaCreateDto.getCityName());
	}

}