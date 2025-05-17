package cz.fi.muni.pa165.teamservice.unit.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.fi.muni.pa165.dto.teamservice.TeamCharacteristicDTO;
import cz.fi.muni.pa165.dto.teamservice.TeamCharacteristicUpdateDTO;
import cz.fi.muni.pa165.enums.TeamCharacteristicType;
import cz.fi.muni.pa165.teamservice.api.controllers.TeamCharacteristicControllerImpl;
import cz.fi.muni.pa165.teamservice.api.exception.ResourceNotFoundException;
import cz.fi.muni.pa165.teamservice.business.facades.TeamCharacteristicFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Jan Martinek
 */
public class TeamCharacteristicControllerTest {

	private final ObjectMapper objectMapper = new ObjectMapper();

	private final UUID characteristicId = UUID.randomUUID();

	private final UUID teamId = UUID.randomUUID();

	private MockMvc mockMvc;

	@Mock
	private TeamCharacteristicFacade teamCharacteristicFacade;

	@InjectMocks
	private TeamCharacteristicControllerImpl teamCharacteristicController;

	private TeamCharacteristicDTO characteristicDTO;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(teamCharacteristicController).build();

		characteristicDTO = new TeamCharacteristicDTO();
		characteristicDTO.setGuid(characteristicId);
		characteristicDTO.setTeamId(teamId);
		characteristicDTO.setCharacteristicType(TeamCharacteristicType.STRENGTH);
		characteristicDTO.setCharacteristicValue(85);

	}

	@Test
	void getCharacteristicById() throws Exception {
		when(teamCharacteristicFacade.findById(characteristicId)).thenReturn(characteristicDTO);

		mockMvc.perform(get("/api/team-characteristics/{id}", characteristicId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.guid").value(characteristicId.toString()))
			.andExpect(jsonPath("$.characteristicValue").value(characteristicDTO.getCharacteristicValue()));

		verify(teamCharacteristicFacade, times(1)).findById(characteristicId);
	}

	@Test
	void getCharacteristicById_notFound() throws Exception {
		when(teamCharacteristicFacade.findById(characteristicId)).thenThrow(ResourceNotFoundException.class);

		mockMvc.perform(get("/api/team-characteristics/{id}", characteristicId)).andExpect(status().isNotFound());

		verify(teamCharacteristicFacade, times(1)).findById(characteristicId);
	}

	@Test
	void createTeamCharacteristicCharacteristic() throws Exception {
		when(teamCharacteristicFacade.create(any())).thenReturn(characteristicDTO);

		mockMvc
			.perform(post("/api/team-characteristics").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(characteristicDTO)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.guid").value(characteristicId.toString()))
			.andExpect(jsonPath("$.teamId").value(teamId.toString()))
			.andExpect(jsonPath("$.characteristicType").value(characteristicDTO.getCharacteristicType().toString()))
			.andExpect(jsonPath("$.characteristicValue").value(characteristicDTO.getCharacteristicValue()));

		verify(teamCharacteristicFacade, times(1)).create(any());
	}

	@Test
	void updateTeamCharacteristicCharacteristic() throws Exception {
		when(teamCharacteristicFacade.update(any())).thenReturn(characteristicDTO);

		mockMvc
			.perform(put("/api/team-characteristics/{id}", characteristicId).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(characteristicDTO)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.guid").value(characteristicId.toString()));

		verify(teamCharacteristicFacade, times(1)).update(any());
	}

	@Test
	void updateTeamCharacteristicCharacteristic_mismatchedIds() throws Exception {
		TeamCharacteristicUpdateDTO updateDTO = new TeamCharacteristicUpdateDTO();
		updateDTO.setGuid(UUID.randomUUID());
		updateDTO.setCharacteristicType(TeamCharacteristicType.STRENGTH);
		updateDTO.setCharacteristicValue(85);

		mockMvc
			.perform(put("/api/team-characteristics/{id}", characteristicId).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDTO)))
			.andExpect(status().isBadRequest());

		verify(teamCharacteristicFacade, never()).update(any());
	}

	@Test
	void deleteTeamCharacteristicCharacteristic() throws Exception {
		doNothing().when(teamCharacteristicFacade).delete(characteristicId);

		mockMvc.perform(delete("/api/team-characteristics/{id}", characteristicId)).andExpect(status().isNoContent());

		verify(teamCharacteristicFacade, times(1)).delete(characteristicId);
	}

	@Test
	void getCharacteristicsByTeamId() throws Exception {
		when(teamCharacteristicFacade.findByTeamId(teamId)).thenReturn(Collections.singletonList(characteristicDTO));

		mockMvc.perform(get("/api/team-characteristics/team/{teamId}", teamId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].guid").value(characteristicId.toString()))
			.andExpect(jsonPath("$[0].teamId").value(teamId.toString()));

		verify(teamCharacteristicFacade, times(1)).findByTeamId(teamId);
	}

	@Test
	void getCharacteristicsByTeamId_empty() throws Exception {
		when(teamCharacteristicFacade.findByTeamId(teamId)).thenReturn(Collections.emptyList());

		mockMvc.perform(get("/api/team-characteristics/team/{teamId}", teamId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isArray())
			.andExpect(jsonPath("$").isEmpty());

		verify(teamCharacteristicFacade, times(1)).findByTeamId(teamId);
	}

	@Test
	void createTeamCharacteristicCharacteristic_invalidInput() throws Exception {
		TeamCharacteristicDTO invalidDTO = new TeamCharacteristicDTO();
		invalidDTO.setCharacteristicValue(-5); // Invalid value

		mockMvc
			.perform(post("/api/team-characteristics").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidDTO)))
			.andExpect(status().isBadRequest());

		verify(teamCharacteristicFacade, never()).create(any());
	}

	@Test
	void updateTeamCharacteristicCharacteristic_invalidInput() throws Exception {
		characteristicDTO.setCharacteristicValue(-10);

		mockMvc
			.perform(put("/api/team-characteristics/{id}", characteristicId).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(characteristicDTO)))
			.andExpect(status().isBadRequest());

		verify(teamCharacteristicFacade, never()).update(any());
	}

	@Test
	void getCharacteristicById_invalidUuid() throws Exception {
		mockMvc.perform(get("/api/team-characteristics/{id}", "invalid-uuid")).andExpect(status().isBadRequest());

		verify(teamCharacteristicFacade, never()).findById(any());
	}

	@Test
	void getCharacteristicsByTeamId_invalidUuid() throws Exception {
		mockMvc.perform(get("/api/team-characteristics/team/{teamId}", "invalid-uuid"))
			.andExpect(status().isBadRequest());

		verify(teamCharacteristicFacade, never()).findByTeamId(any());
	}

}
