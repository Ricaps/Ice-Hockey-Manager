package cz.fi.muni.pa165.gameservice.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.fi.muni.pa165.dto.gameService.AssignTeamDto;
import cz.fi.muni.pa165.dto.gameService.CompetitionCreateDto;
import cz.fi.muni.pa165.dto.gameService.CompetitionViewDto;
import cz.fi.muni.pa165.gameservice.api.exception.ResourceNotFoundException;
import cz.fi.muni.pa165.gameservice.business.facades.CompetitionFacade;
import cz.fi.muni.pa165.gameservice.config.ObjectMapperConfig;
import cz.fi.muni.pa165.gameservice.testdata.CompetitionTestData;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CompetitionControllerImpl.class)
@Import(ObjectMapperConfig.class)
class CompetitionControllerMvcTest {

	@Autowired
	MockMvc mockMvc;

	@MockitoBean
	CompetitionFacade competitionFacade;

	@Autowired
	ObjectMapper objectMapper;

	@Test
	void addCompetition_emptyBody_badRequest() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/v1/competition/").contentType(MediaType.APPLICATION_JSON_VALUE))
			.andExpect(status().isBadRequest())
			.andReturn()
			.getResponse()
			.getContentAsString();

		Mockito.verify(competitionFacade, Mockito.never()).addCompetition(Mockito.any());
	}

	@Test
	void addCompetition_validBody_success() throws Exception {
		var testView = CompetitionTestData.getCompetitionView();
		var competitionCreate = new CompetitionCreateDto();
		competitionCreate.setName("test");
		competitionCreate.setStartAt(LocalDate.now());
		competitionCreate.setEndAt(LocalDate.now());

		Mockito.when(competitionFacade.addCompetition(competitionCreate)).thenReturn(testView);

		var responseContent = mockMvc
			.perform(MockMvcRequestBuilders.post("/v1/competition/")
				.content(objectMapper.writeValueAsString(competitionCreate))
				.contentType(MediaType.APPLICATION_JSON_VALUE))
			.andExpect(status().isCreated())
			.andReturn()
			.getResponse()
			.getContentAsString(StandardCharsets.UTF_8);

		assertThat(responseContent).isNotBlank();
		var returnedView = objectMapper.readValue(responseContent, CompetitionViewDto.class);
		assertThat(returnedView).isEqualTo(testView);
	}

	@Test
	void addCompetition_shortName_badRequest() throws Exception {
		var competitionCreate = new CompetitionCreateDto();
		competitionCreate.setName("");
		competitionCreate.setStartAt(LocalDate.now());
		competitionCreate.setEndAt(LocalDate.now());

		var responseContent = mockMvc
			.perform(MockMvcRequestBuilders.post("/v1/competition/")
				.content(objectMapper.writeValueAsString(competitionCreate))
				.contentType(MediaType.APPLICATION_JSON_VALUE))
			.andExpect(status().isBadRequest())
			.andReturn()
			.getResponse()
			.getContentAsString(StandardCharsets.UTF_8);
		assertThat(responseContent).isBlank();

		Mockito.verify(competitionFacade, Mockito.never()).addCompetition(Mockito.any());
	}

	@Test
	void addCompetition_longName_badRequest() throws Exception {
		var competitionCreate = new CompetitionCreateDto();
		competitionCreate.setName("x".repeat(256));
		competitionCreate.setStartAt(LocalDate.now());
		competitionCreate.setEndAt(LocalDate.now());

		var responseContent = mockMvc
			.perform(MockMvcRequestBuilders.post("/v1/competition/")
				.content(objectMapper.writeValueAsString(competitionCreate))
				.contentType(MediaType.APPLICATION_JSON_VALUE))
			.andExpect(status().isBadRequest())
			.andReturn()
			.getResponse()
			.getContentAsString(StandardCharsets.UTF_8);
		assertThat(responseContent).isBlank();

		Mockito.verify(competitionFacade, Mockito.never()).addCompetition(Mockito.any());
	}

	@Test
	void addCompetition_nameOneChar_success() throws Exception {
		var testView = CompetitionTestData.getCompetitionView();
		var competitionCreate = new CompetitionCreateDto();
		competitionCreate.setName("x");
		competitionCreate.setStartAt(LocalDate.now());
		competitionCreate.setEndAt(LocalDate.now());

		Mockito.when(competitionFacade.addCompetition(competitionCreate)).thenReturn(testView);

		var responseContent = mockMvc
			.perform(MockMvcRequestBuilders.post("/v1/competition/")
				.content(objectMapper.writeValueAsString(competitionCreate))
				.contentType(MediaType.APPLICATION_JSON_VALUE))
			.andExpect(status().isCreated())
			.andReturn()
			.getResponse()
			.getContentAsString(StandardCharsets.UTF_8);

		assertThat(responseContent).isNotBlank();
		CompetitionViewDto returnedView = objectMapper.readValue(responseContent, CompetitionViewDto.class);
		assertThat(returnedView).isEqualTo(testView);
	}

	@Test
	void addCompetition_255Char_success() throws Exception {
		var testView = CompetitionTestData.getCompetitionView();
		var competitionCreate = new CompetitionCreateDto();
		competitionCreate.setName("x".repeat(255));
		competitionCreate.setStartAt(LocalDate.now());
		competitionCreate.setEndAt(LocalDate.now());

		Mockito.when(competitionFacade.addCompetition(competitionCreate)).thenReturn(testView);

		var responseContent = mockMvc
			.perform(MockMvcRequestBuilders.post("/v1/competition/")
				.content(objectMapper.writeValueAsString(competitionCreate))
				.contentType(MediaType.APPLICATION_JSON_VALUE))
			.andExpect(status().isCreated())
			.andReturn()
			.getResponse()
			.getContentAsString(StandardCharsets.UTF_8);

		assertThat(responseContent).isNotBlank();
		var returnedView = objectMapper.readValue(responseContent, CompetitionViewDto.class);
		assertThat(returnedView).isEqualTo(testView);
	}

	@Test
	void addCompetition_noStartDate_badRequest() throws Exception {
		var competitionCreate = new CompetitionCreateDto();
		competitionCreate.setName("test");
		competitionCreate.setEndAt(LocalDate.now());

		var responseContent = mockMvc
			.perform(MockMvcRequestBuilders.post("/v1/competition/")
				.content(objectMapper.writeValueAsString(competitionCreate))
				.contentType(MediaType.APPLICATION_JSON_VALUE))
			.andExpect(status().isBadRequest())
			.andReturn()
			.getResponse()
			.getContentAsString(StandardCharsets.UTF_8);

		assertThat(responseContent).isBlank();

		Mockito.verify(competitionFacade, Mockito.never()).addCompetition(Mockito.any());
	}

	@Test
	void addCompetition_noEndDate_badRequest() throws Exception {
		var testView = CompetitionTestData.getCompetitionView();
		testView.setEndAt(null);

		var competitionCreate = new CompetitionCreateDto();
		competitionCreate.setName("test");
		competitionCreate.setStartAt(LocalDate.now());

		Mockito.when(competitionFacade.addCompetition(competitionCreate)).thenReturn(testView);

		mockMvc
			.perform(MockMvcRequestBuilders.post("/v1/competition/")
				.content(objectMapper.writeValueAsString(competitionCreate))
				.contentType(MediaType.APPLICATION_JSON_VALUE))
			.andExpect(status().isBadRequest())
			.andReturn()
			.getResponse()
			.getContentAsString(StandardCharsets.UTF_8);

		Mockito.verify(competitionFacade, Mockito.never()).addCompetition(Mockito.any());
	}

	@Test
	void getCompetition_doesntExist_throwsException() throws Exception {
		var competitionUUID = UUID.randomUUID();
		var message = "Competition with guid %s was not found!".formatted(competitionUUID);

		Mockito.when(competitionFacade.getCompetition(competitionUUID))
			.thenThrow(new ResourceNotFoundException(message));

		mockMvc
			.perform(MockMvcRequestBuilders.get("/v1/competition/{guid}", competitionUUID)
				.contentType(MediaType.APPLICATION_JSON_VALUE))
			.andExpect(status().isNotFound());
	}

	@Test
	void getCompetition_notValidUuid_returns400() throws Exception {
		var invalidUUID = "abcd";
		mockMvc
			.perform(MockMvcRequestBuilders.get("/v1/competition/{guid}", invalidUUID)
				.contentType(MediaType.APPLICATION_JSON_VALUE))
			.andExpect(status().isBadRequest());

		Mockito.verify(competitionFacade, Mockito.never()).getCompetition(Mockito.any());
	}

	@Test
	void getCompetition_existingCompetition_returnsView() throws Exception {
		var competition = CompetitionTestData.getCompetitionView();

		Mockito.when(competitionFacade.getCompetition(competition.getGuid())).thenReturn(competition);

		var response = mockMvc
			.perform(MockMvcRequestBuilders.get("/v1/competition/{guid}", competition.getGuid())
				.contentType(MediaType.APPLICATION_JSON_VALUE))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();
		var returnedCompetition = objectMapper.readValue(response, CompetitionViewDto.class);
		assertThat(returnedCompetition).isEqualTo(competition);
	}

	@Test
	void updateCompetition_emptyBody_badRequest() throws Exception {
		mockMvc
			.perform(MockMvcRequestBuilders.put("/v1/competition/{guid}", UUID.randomUUID())
				.contentType(MediaType.APPLICATION_JSON_VALUE))
			.andExpect(status().isBadRequest())
			.andReturn()
			.getResponse()
			.getContentAsString();

		Mockito.verify(competitionFacade, Mockito.never()).updateCompetition(Mockito.any(), Mockito.any());
	}

	@Test
	void updateCompetition_wrongUUID_badRequest() throws Exception {
		mockMvc
			.perform(MockMvcRequestBuilders.put("/v1/competition/{guid}", "abc")
				.contentType(MediaType.APPLICATION_JSON_VALUE))
			.andExpect(status().isBadRequest());

		Mockito.verify(competitionFacade, Mockito.never()).updateCompetition(Mockito.any(), Mockito.any());
	}

	@Test
	void updateCompetition_validBody_success() throws Exception {
		var testView = CompetitionTestData.getCompetitionView();
		var competitionCreate = new CompetitionCreateDto();
		competitionCreate.setName("test");
		competitionCreate.setStartAt(LocalDate.now());
		competitionCreate.setEndAt(LocalDate.now());

		Mockito.when(competitionFacade.updateCompetition(testView.getGuid(), competitionCreate)).thenReturn(testView);

		var responseContent = mockMvc
			.perform(MockMvcRequestBuilders.put("/v1/competition/{guid}", testView.getGuid())
				.content(objectMapper.writeValueAsString(competitionCreate))
				.contentType(MediaType.APPLICATION_JSON_VALUE))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString(StandardCharsets.UTF_8);

		assertThat(responseContent).isNotBlank();
		var returnedView = objectMapper.readValue(responseContent, CompetitionViewDto.class);
		assertThat(returnedView).isEqualTo(testView);
	}

	@Test
	void updateCompetition_shortName_badRequest() throws Exception {
		var competitionCreate = new CompetitionCreateDto();
		competitionCreate.setName("");
		competitionCreate.setStartAt(LocalDate.now());
		competitionCreate.setEndAt(LocalDate.now());

		var responseContent = mockMvc
			.perform(MockMvcRequestBuilders.put("/v1/competition/{guid}", UUID.randomUUID())
				.content(objectMapper.writeValueAsString(competitionCreate))
				.contentType(MediaType.APPLICATION_JSON_VALUE))
			.andExpect(status().isBadRequest())
			.andReturn()
			.getResponse()
			.getContentAsString(StandardCharsets.UTF_8);
		assertThat(responseContent).isBlank();

		Mockito.verify(competitionFacade, Mockito.never()).updateCompetition(Mockito.any(), Mockito.any());
	}

	@Test
	void updateCompetition_longName_badRequest() throws Exception {
		var competitionCreate = new CompetitionCreateDto();
		competitionCreate.setName("x".repeat(256));
		competitionCreate.setStartAt(LocalDate.now());
		competitionCreate.setEndAt(LocalDate.now());

		var responseContent = mockMvc
			.perform(MockMvcRequestBuilders.put("/v1/competition/{guid}", UUID.randomUUID())
				.content(objectMapper.writeValueAsString(competitionCreate))
				.contentType(MediaType.APPLICATION_JSON_VALUE))
			.andExpect(status().isBadRequest())
			.andReturn()
			.getResponse()
			.getContentAsString(StandardCharsets.UTF_8);
		assertThat(responseContent).isBlank();

		Mockito.verify(competitionFacade, Mockito.never()).updateCompetition(Mockito.any(), Mockito.any());
	}

	@Test
	void updateCompetition_nameOneChar_success() throws Exception {
		var testView = CompetitionTestData.getCompetitionView();
		var competitionCreate = new CompetitionCreateDto();
		competitionCreate.setName("x");
		competitionCreate.setStartAt(LocalDate.now());
		competitionCreate.setEndAt(LocalDate.now());

		Mockito.when(competitionFacade.updateCompetition(testView.getGuid(), competitionCreate)).thenReturn(testView);

		var responseContent = mockMvc
			.perform(MockMvcRequestBuilders.put("/v1/competition/{guid}", testView.getGuid())
				.content(objectMapper.writeValueAsString(competitionCreate))
				.contentType(MediaType.APPLICATION_JSON_VALUE))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString(StandardCharsets.UTF_8);

		assertThat(responseContent).isNotBlank();
		CompetitionViewDto returnedView = objectMapper.readValue(responseContent, CompetitionViewDto.class);
		assertThat(returnedView).isEqualTo(testView);
	}

	@Test
	void updateCompetition_255Char_success() throws Exception {
		var testView = CompetitionTestData.getCompetitionView();
		var competitionCreate = new CompetitionCreateDto();
		competitionCreate.setName("x".repeat(255));
		competitionCreate.setStartAt(LocalDate.now());
		competitionCreate.setEndAt(LocalDate.now());

		Mockito.when(competitionFacade.updateCompetition(testView.getGuid(), competitionCreate)).thenReturn(testView);

		var responseContent = mockMvc
			.perform(MockMvcRequestBuilders.put("/v1/competition/{guid}", testView.getGuid())
				.content(objectMapper.writeValueAsString(competitionCreate))
				.contentType(MediaType.APPLICATION_JSON_VALUE))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString(StandardCharsets.UTF_8);

		assertThat(responseContent).isNotBlank();
		var returnedView = objectMapper.readValue(responseContent, CompetitionViewDto.class);
		assertThat(returnedView).isEqualTo(testView);
	}

	@Test
	void updateCompetition_noStartDate_badRequest() throws Exception {
		var competitionCreate = new CompetitionCreateDto();
		competitionCreate.setName("test");
		competitionCreate.setEndAt(LocalDate.now());

		var responseContent = mockMvc
			.perform(MockMvcRequestBuilders.put("/v1/competition/{guid}", UUID.randomUUID())
				.content(objectMapper.writeValueAsString(competitionCreate))
				.contentType(MediaType.APPLICATION_JSON_VALUE))
			.andExpect(status().isBadRequest())
			.andReturn()
			.getResponse()
			.getContentAsString(StandardCharsets.UTF_8);

		assertThat(responseContent).isBlank();
		Mockito.verify(competitionFacade, Mockito.never()).updateCompetition(Mockito.any(), Mockito.any());
	}

	@Test
	void updateCompetition_noEndDate_success() throws Exception {
		var testView = CompetitionTestData.getCompetitionView();
		testView.setEndAt(null);

		var competitionCreate = new CompetitionCreateDto();
		competitionCreate.setName("test");
		competitionCreate.setStartAt(LocalDate.now());

		mockMvc
			.perform(MockMvcRequestBuilders.put("/v1/competition/{guid}", testView.getGuid())
				.content(objectMapper.writeValueAsString(competitionCreate))
				.contentType(MediaType.APPLICATION_JSON_VALUE))
			.andExpect(status().isBadRequest());

		Mockito.verify(competitionFacade, Mockito.never()).updateCompetition(Mockito.any(), Mockito.any());
	}

	@Test
	void assignTeam_nullAssignTeam_badRequest() throws Exception {
		var assignTeam = AssignTeamDto.builder().assignTeam(null).build();

		mockMvc
			.perform(MockMvcRequestBuilders.post("/v1/competition/{guid}/teams", UUID.randomUUID())
				.content(objectMapper.writeValueAsString(assignTeam))
				.contentType(MediaType.APPLICATION_JSON_VALUE))
			.andExpect(status().isBadRequest());

		Mockito.verify(competitionFacade, Mockito.never()).assignTeam(Mockito.any(), Mockito.any());
	}

	@Test
	void assignTeam_missingAssignTeamDto_badRequest() throws Exception {
		mockMvc
			.perform(MockMvcRequestBuilders.post("/v1/competition/{guid}/teams", UUID.randomUUID())
				.contentType(MediaType.APPLICATION_JSON_VALUE))
			.andExpect(status().isBadRequest());

		Mockito.verify(competitionFacade, Mockito.never()).assignTeam(Mockito.any(), Mockito.any());
	}

	@Test
	void assignTeam_incorrectUUID_badRequest() throws Exception {
		var assignTeam = AssignTeamDto.builder().assignTeam(UUID.randomUUID()).build();

		mockMvc
			.perform(MockMvcRequestBuilders.post("/v1/competition/{guid}/teams", "abcd")
				.content(objectMapper.writeValueAsString(assignTeam))
				.contentType(MediaType.APPLICATION_JSON_VALUE))
			.andExpect(status().isBadRequest());

		Mockito.verify(competitionFacade, Mockito.never()).assignTeam(Mockito.any(), Mockito.any());
	}

	@Test
	void assignTeam_allGood_http201() throws Exception {
		var assignTeam = AssignTeamDto.builder().assignTeam(UUID.randomUUID()).build();
		UUID competitionUUID = UUID.randomUUID();

		Mockito.doNothing().when(competitionFacade).assignTeam(competitionUUID, assignTeam);
		mockMvc
			.perform(MockMvcRequestBuilders.post("/v1/competition/{guid}/teams", competitionUUID)
				.content(objectMapper.writeValueAsString(assignTeam))
				.contentType(MediaType.APPLICATION_JSON_VALUE))
			.andExpect(status().isCreated())
			.andReturn()
			.getResponse()
			.getContentAsString(StandardCharsets.UTF_8);

		Mockito.verify(competitionFacade, Mockito.times(1)).assignTeam(competitionUUID, assignTeam);
	}

}