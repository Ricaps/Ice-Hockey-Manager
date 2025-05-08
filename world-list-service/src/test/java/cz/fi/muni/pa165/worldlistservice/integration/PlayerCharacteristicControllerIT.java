package cz.fi.muni.pa165.worldlistservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.fi.muni.pa165.dto.worldlistservice.playercharacteristic.PlayerCharacteristicDto;
import cz.fi.muni.pa165.dto.worldlistservice.playercharacteristic.create.PlayerCharacteristicCreateDto;
import cz.fi.muni.pa165.enums.PlayerCharacteristicType;
import cz.fi.muni.pa165.worldlistservice.business.facades.interfaces.PlayerCharacteristicFacade;
import cz.fi.muni.pa165.worldlistservice.config.DisableSecurityTestConfig;
import cz.fi.muni.pa165.worldlistservice.persistence.repositories.PlayerCharacteristicRepository;
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
class PlayerCharacteristicControllerIT {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private PlayerCharacteristicRepository playerCharacteristicRepository;

	@MockitoSpyBean
	private PlayerCharacteristicFacade playerCharacteristicFacade;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void getAllPlayerCharacteristics_validPage_shouldReturnRegionsPage() throws Exception {
		mockMvc.perform(get("/v1/player-characteristics/").param("page", "0").param("size", "10"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").isArray())
			.andExpect(jsonPath("$.page.size").value(10))
			.andExpect(jsonPath("$.page.totalElements").value(playerCharacteristicRepository.count()))
			.andExpect(jsonPath("$.page.totalPages")
				.value((int) Math.ceil(playerCharacteristicRepository.count() / 10.0)));
	}

	@Test
	void getAllPlayerCharacteristics_nonExistentPage_shouldReturnNotFound() throws Exception {
		mockMvc.perform(get("/v1/player-characteristics/").param("page", "10").param("size", "10"))
			.andExpect(status().isNotFound());

		verify(playerCharacteristicFacade, times(1)).findAll(any());
	}

	@Test
	void getPlayerCharacteristicById_existingId_shouldReturnRegion() throws Exception {
		var existing = playerCharacteristicRepository.findAll().getFirst();

		mockMvc.perform(get("/v1/player-characteristics/{id}", existing.getId()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(existing.getId().toString()))
			.andExpect(jsonPath("$.type").value(existing.getType().toString()))
			.andExpect(jsonPath("$.value").value(existing.getValue()));
	}

	@Test
	void getPlayerCharacteristicById_nonExistingId_shouldReturnNotFound() throws Exception {
		mockMvc.perform(get("/v1/player-characteristics/{id}", UUID.randomUUID())).andExpect(status().isNotFound());

		verify(playerCharacteristicFacade, times(1)).findById(any());
	}

	@Test
	void createPlayerCharacteristic_validData_shouldCreatePlayerCharacteristic() throws Exception {
		var createDto = new PlayerCharacteristicCreateDto(PlayerCharacteristicType.SPEED, 10);

		mockMvc
			.perform(post("/v1/player-characteristics/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createDto)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.value").value(10))
			.andExpect(jsonPath("$.type").value(PlayerCharacteristicType.SPEED.toString()));

		assertThat(playerCharacteristicRepository.findAll()).anyMatch(characteristic -> characteristic.getValue() == 10
				&& characteristic.getType() == PlayerCharacteristicType.SPEED);
	}

	@Test
	void createPlayerCharacteristic_valueTooLow_shouldReturnBadRequest() throws Exception {
		var createDto = new PlayerCharacteristicCreateDto(PlayerCharacteristicType.SPEED, 0);

		mockMvc
			.perform(post("/v1/player-characteristics/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createDto)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.value").value("Value of player characteristic has to be greater than 0"));
	}

	@Test
	void createPlayerCharacteristic_invalidJson_shouldReturnBadRequest() throws Exception {
		String invalidJson = "{}";

		mockMvc
			.perform(post("/v1/player-characteristics/").contentType(MediaType.APPLICATION_JSON).content(invalidJson))
			.andExpect(status().isBadRequest());
	}

	@Test
	void updateCPlayerCharacteristic_valueTooLow_shouldReturnBadRequest() throws Exception {
		var existing = playerCharacteristicRepository.findAll().getFirst();
		var updateDto = new PlayerCharacteristicDto(existing.getId(), PlayerCharacteristicType.SPEED, 0);

		mockMvc
			.perform(put("/v1/player-characteristics/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.value").value("Value of player characteristic has to be greater than 0"));
	}

	@Test
	void updatePlayerCharacteristic_validData_shouldUpdatePlayerCharacteristic() throws Exception {
		var existing = playerCharacteristicRepository.findAll().getFirst();
		var updateDto = new PlayerCharacteristicDto(existing.getId(), PlayerCharacteristicType.SPEED, 10);

		mockMvc
			.perform(put("/v1/player-characteristics/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.value").value(updateDto.getValue()))
			.andExpect(jsonPath("$.type").value(updateDto.getType().toString()));

		var updated = playerCharacteristicRepository.findById(existing.getId()).orElseThrow();
		assertThat(updated.getValue()).isEqualTo(updateDto.getValue());
		assertThat(updated.getType()).isEqualTo(updateDto.getType());
	}

	@Test
	void updatePlayerCharacteristic_nonExistingId_shouldReturnNotFound() throws Exception {
		var updateDto = new PlayerCharacteristicDto(UUID.randomUUID(), PlayerCharacteristicType.SPEED, 10);

		mockMvc
			.perform(put("/v1/player-characteristics/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDto)))
			.andExpect(status().isNotFound());

		verify(playerCharacteristicFacade, times(1)).update(any());
	}

	@Test
	void deletePlayerCharacteristic_regionNotAssigned_shouldDeletePlayerCharacteristic() throws Exception {
		var playerCharacteristic = playerCharacteristicRepository.findAll().getFirst();

		mockMvc.perform(delete("/v1/player-characteristics/{id}", playerCharacteristic.getId()))
			.andExpect(status().isOk());

		assertThat(playerCharacteristicRepository.existsById(playerCharacteristic.getId())).isFalse();
	}

	@Test
	void deletePlayerCharacteristic_playerAssigned_shouldReturnConflict() throws Exception {
		var playerCharacteristic = playerCharacteristicRepository.findAll().get(1);

		mockMvc.perform(delete("/v1/player-characteristics/{id}", playerCharacteristic.getId()))
			.andExpect(status().isConflict());
	}

	@Test
	void deletePlayerCharacteristic_nonExistingId_shouldReturnNotFound() throws Exception {
		mockMvc.perform(delete("/v1/player-characteristics/{id}", UUID.randomUUID())).andExpect(status().isNotFound());

		verify(playerCharacteristicFacade, times(1)).delete(any());
	}

}
