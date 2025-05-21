package cz.fi.muni.pa165.teamservice.unit.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.fi.muni.pa165.dto.teamservice.FictiveTeamCreateDTO;
import cz.fi.muni.pa165.dto.teamservice.FictiveTeamDTO;
import cz.fi.muni.pa165.dto.teamservice.FictiveTeamUpdateDTO;
import cz.fi.muni.pa165.teamservice.api.controllers.FictiveTeamController;
import cz.fi.muni.pa165.teamservice.api.exception.ResourceNotFoundException;
import cz.fi.muni.pa165.teamservice.business.facades.FictiveTeamFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.hamcrest.Matchers.hasItems;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Jan Martinek
 */
class FictiveTeamControllerTest {

	private final ObjectMapper objectMapper = new ObjectMapper();

	private final UUID teamId = UUID.randomUUID();

	private MockMvc mockMvc;

	@Mock
	private FictiveTeamFacade fictiveTeamFacade;

	@InjectMocks
	private FictiveTeamController fictiveTeamController;

	private FictiveTeamDTO fictiveTeamDTO;

	private FictiveTeamCreateDTO createDTO;

	private FictiveTeamUpdateDTO updateDTO;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

		mockMvc = MockMvcBuilders.standaloneSetup(fictiveTeamController).build();
		UUID characteristicId1 = UUID.randomUUID();
		UUID characteristicId2 = UUID.randomUUID();
		Set<UUID> characteristics = new HashSet<>(Arrays.asList(characteristicId1, characteristicId2));

		// Setup DTOs
		fictiveTeamDTO = new FictiveTeamDTO();
		fictiveTeamDTO.setGuid(teamId);
		fictiveTeamDTO.setName("Avengers");
		fictiveTeamDTO.setOwnerId(UUID.randomUUID());
		fictiveTeamDTO.setCharacteristicTypes(characteristics);
		fictiveTeamDTO.setBudgetAmount(1000000.00);

		createDTO = new FictiveTeamCreateDTO();
		createDTO.setName("Avengers");
		createDTO.setOwnerId(UUID.randomUUID());
		createDTO.setCharacteristicTypes(characteristics);
		createDTO.setPlayerIds(List.of(UUID.randomUUID()));

		updateDTO = new FictiveTeamUpdateDTO();
		updateDTO.setGuid(teamId);
		updateDTO.setName("Updated Avengers");
		updateDTO.setCharacteristicTypes(characteristics);
	}

	@Test
	void createFictiveTeam_shouldReturn201() throws Exception {
		when(fictiveTeamFacade.createFictiveTeam(any(FictiveTeamCreateDTO.class))).thenReturn(fictiveTeamDTO);

		mockMvc
			.perform(post("/v1/fictive-team/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createDTO)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.guid").value(teamId.toString()))
			.andExpect(jsonPath("$.name").value(fictiveTeamDTO.getName()))
			.andExpect(jsonPath("$.characteristicTypes.length()").value(fictiveTeamDTO.getCharacteristicTypes().size()))
			.andExpect(jsonPath("$.characteristicTypes",
					hasItems(fictiveTeamDTO.getCharacteristicTypes().stream().map(UUID::toString).toArray())));

		verify(fictiveTeamFacade, times(1)).createFictiveTeam(any(FictiveTeamCreateDTO.class));
	}

	@Test
	void getFictiveTeamById_shouldReturn200() throws Exception {
		when(fictiveTeamFacade.findById(teamId)).thenReturn(fictiveTeamDTO);

		mockMvc.perform(get("/v1/fictive-team/{uuid}", teamId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.guid").value(teamId.toString()))
			.andExpect(jsonPath("$.name").value(fictiveTeamDTO.getName()));

		verify(fictiveTeamFacade, times(1)).findById(teamId);
	}

	@Test
	void getFictiveTeamById_notFound_shouldReturn404() throws Exception {
		when(fictiveTeamFacade.findById(teamId)).thenThrow(ResourceNotFoundException.class);

		mockMvc.perform(get("/v1/fictive-team/{uuid}", teamId)).andExpect(status().isNotFound());

		verify(fictiveTeamFacade, times(1)).findById(teamId);
	}

	@Test
	void updateFictiveTeam_mismatchedIds_shouldReturn400() throws Exception {
		updateDTO.setGuid(UUID.randomUUID()); // Different from path ID

		mockMvc
			.perform(put("/v1/fictive-team/{uuid}", teamId).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateDTO)))
			.andExpect(status().isBadRequest());

		verify(fictiveTeamFacade, never()).updateFictiveTeam(any());
	}

	@Test
	void deleteFictiveTeam_shouldReturn204() throws Exception {
		doNothing().when(fictiveTeamFacade).deleteFictiveTeam(teamId);

		mockMvc.perform(delete("/v1/fictive-team/{uuid}", teamId)).andExpect(status().isNoContent());

		verify(fictiveTeamFacade, times(1)).deleteFictiveTeam(teamId);
	}

	@Test
	void getAllFictiveTeams_shouldReturn200() throws Exception {
		when(fictiveTeamFacade.findAll()).thenReturn(Collections.singletonList(fictiveTeamDTO));

		mockMvc.perform(get("/v1/fictive-team/"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].guid").value(teamId.toString()))
			.andExpect(jsonPath("$[0].name").value(fictiveTeamDTO.getName()));

		verify(fictiveTeamFacade, times(1)).findAll();
	}

	@Test
	void createFictiveTeam_invalidInput_shouldReturn400() throws Exception {
		createDTO.setName(""); // Invalid empty name

		mockMvc
			.perform(post("/v1/fictive-team/").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createDTO)))
			.andExpect(status().isBadRequest());

		verify(fictiveTeamFacade, never()).createFictiveTeam(any());
	}

	@Test
	void getFictiveTeamById_invalidUuid_shouldReturn400() throws Exception {
		mockMvc.perform(get("/v1/fictive-team/{uuid}", "invalid-uuid")).andExpect(status().isBadRequest());

		verify(fictiveTeamFacade, never()).findById(any());
	}

}