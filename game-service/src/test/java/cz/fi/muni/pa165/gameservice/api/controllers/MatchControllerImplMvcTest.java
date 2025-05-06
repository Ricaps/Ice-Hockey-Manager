package cz.fi.muni.pa165.gameservice.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.fi.muni.pa165.dto.gameservice.MatchViewDto;
import cz.fi.muni.pa165.gameservice.business.facades.MatchFacade;
import cz.fi.muni.pa165.gameservice.config.DisableSecurityTestConfig;
import cz.fi.muni.pa165.gameservice.config.ObjectMapperConfig;
import cz.fi.muni.pa165.gameservice.testdata.MatchTestData;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MatchControllerImpl.class)
@Import({ ObjectMapperConfig.class, DisableSecurityTestConfig.class })
class MatchControllerImplMvcTest {

	@Autowired
	MockMvc mockMvc;

	@MockitoBean
	MatchFacade matchFacade;

	@Autowired
	ObjectMapper objectMapper;

	@Test
	void generateMatches_wrongUUID_badRequest() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/v1/matches/competition/{guid}", "abc"))
			.andExpect(status().isBadRequest())
			.andReturn()
			.getResponse()
			.getContentAsString();

		Mockito.verify(matchFacade, Mockito.never()).generateMatches(Mockito.any());
	}

	@Test
	void generateMatches_allGood_http201WithContent() throws Exception {
		var competitionUUID = UUID.randomUUID();
		var matchViews = MatchTestData.getMatchesView();
		Mockito.when(matchFacade.generateMatches(competitionUUID)).thenReturn(matchViews);

		var responseContent = mockMvc
			.perform(MockMvcRequestBuilders.post("/v1/matches/competition/{guid}", competitionUUID))
			.andExpect(status().isCreated())
			.andReturn()
			.getResponse()
			.getContentAsString();

		List<MatchViewDto> matchViewsRes = objectMapper.readValue(responseContent,
				objectMapper.getTypeFactory().constructCollectionType(List.class, MatchViewDto.class));

		assertThat(matchViewsRes).containsAll(matchViews);
		Mockito.verify(matchFacade, Mockito.times(1)).generateMatches(Mockito.any());
	}

	@Test
	void getMatchesOfCompetition_defaultIncludeResult_hasFalse() throws Exception {
		var competitionUUID = UUID.randomUUID();
		var matchViews = MatchTestData.getMatchesView();
		Mockito.when(matchFacade.getMatchesOfCompetition(competitionUUID, false)).thenReturn(matchViews);

		var responseContent = mockMvc
			.perform(MockMvcRequestBuilders.get("/v1/matches/competition/{guid}", competitionUUID))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();

		List<MatchViewDto> matchViewsRes = objectMapper.readValue(responseContent,
				objectMapper.getTypeFactory().constructCollectionType(List.class, MatchViewDto.class));

		assertThat(matchViewsRes).containsAll(matchViews);
		Mockito.verify(matchFacade, Mockito.times(1)).getMatchesOfCompetition(Mockito.any(), Mockito.eq(false));
	}

	@Test
	void getMatchesOfCompetition_falseIncludeResult_hasFalse() throws Exception {
		var competitionUUID = UUID.randomUUID();
		var matchViews = MatchTestData.getMatchesView();
		Mockito.when(matchFacade.getMatchesOfCompetition(competitionUUID, false)).thenReturn(matchViews);

		var responseContent = mockMvc
			.perform(MockMvcRequestBuilders.get("/v1/matches/competition/{guid}", competitionUUID)
				.queryParam("results", "false"))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();

		List<MatchViewDto> matchViewsRes = objectMapper.readValue(responseContent,
				objectMapper.getTypeFactory().constructCollectionType(List.class, MatchViewDto.class));

		assertThat(matchViewsRes).containsAll(matchViews);
		Mockito.verify(matchFacade, Mockito.times(1)).getMatchesOfCompetition(Mockito.any(), Mockito.eq(false));
	}

	@Test
	void getMatchesOfCompetition_trueIncludeResult_hasTrue() throws Exception {
		var competitionUUID = UUID.randomUUID();
		var matchViews = MatchTestData.getMatchesView();
		Mockito.when(matchFacade.getMatchesOfCompetition(competitionUUID, true)).thenReturn(matchViews);

		var responseContent = mockMvc
			.perform(MockMvcRequestBuilders.get("/v1/matches/competition/{guid}", competitionUUID)
				.queryParam("results", "true"))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();

		List<MatchViewDto> matchViewsRes = objectMapper.readValue(responseContent,
				objectMapper.getTypeFactory().constructCollectionType(List.class, MatchViewDto.class));

		assertThat(matchViewsRes).containsAll(matchViews);
		Mockito.verify(matchFacade, Mockito.times(1)).getMatchesOfCompetition(Mockito.any(), Mockito.eq(true));
	}

	@Test
	void getMatch_defaultIncludeResult_hasFalse() throws Exception {
		var matchUUID = UUID.randomUUID();
		var matchView = MatchTestData.getMatchesView().getFirst();
		Mockito.when(matchFacade.getMatch(matchUUID, false)).thenReturn(matchView);

		var responseContent = mockMvc.perform(MockMvcRequestBuilders.get("/v1/matches/{guid}", matchUUID))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();

		MatchViewDto matchViewRes = objectMapper.readValue(responseContent, MatchViewDto.class);

		assertThat(matchViewRes).isEqualTo(matchView);
		Mockito.verify(matchFacade, Mockito.times(1)).getMatch(matchUUID, false);
	}

	@Test
	void getMatch_falseIncludeResult_hasFalse() throws Exception {
		var matchUUID = UUID.randomUUID();
		var matchView = MatchTestData.getMatchesView().getFirst();
		Mockito.when(matchFacade.getMatch(matchUUID, false)).thenReturn(matchView);

		var responseContent = mockMvc
			.perform(MockMvcRequestBuilders.get("/v1/matches/{guid}", matchUUID).queryParam("results", "false"))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();

		MatchViewDto matchViewsRes = objectMapper.readValue(responseContent, MatchViewDto.class);

		assertThat(matchViewsRes).isEqualTo(matchView);
		Mockito.verify(matchFacade, Mockito.times(1)).getMatch(matchUUID, false);
	}

	@Test
	void getMatches_trueIncludeResult_hasTrue() throws Exception {
		var matchUUID = UUID.randomUUID();
		var matchView = MatchTestData.getMatchesView().getFirst();
		Mockito.when(matchFacade.getMatch(matchUUID, true)).thenReturn(matchView);

		var responseContent = mockMvc
			.perform(MockMvcRequestBuilders.get("/v1/matches/{guid}", matchUUID).queryParam("results", "true"))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();

		MatchViewDto matchViewsRes = objectMapper.readValue(responseContent, MatchViewDto.class);

		assertThat(matchViewsRes).isEqualTo(matchView);
		Mockito.verify(matchFacade, Mockito.times(1)).getMatch(matchUUID, true);
	}

	@Test
	void createMatch_allGood_matchIsCreated() throws Exception {
		var matchCreate = MatchTestData.getMatchCreateDto();
		var matchView = MatchTestData.getMatchesView().getFirst();
		Mockito.when(matchFacade.createMatch(matchCreate)).thenReturn(matchView);

		var responseContent = mockMvc
			.perform(MockMvcRequestBuilders.post("/v1/matches/")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objectMapper.writeValueAsString(matchCreate)))
			.andExpect(status().isCreated())
			.andReturn()
			.getResponse()
			.getContentAsString();

		MatchViewDto matchViewsRes = objectMapper.readValue(responseContent, MatchViewDto.class);

		assertThat(matchViewsRes).isEqualTo(matchView);
		Mockito.verify(matchFacade, Mockito.times(1)).createMatch(matchCreate);
	}

	@Test
	void createMatch_emptyBody_badRequest() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/v1/matches/").contentType(MediaType.APPLICATION_JSON_VALUE))
			.andExpect(status().isBadRequest());

		Mockito.verify(matchFacade, Mockito.never()).createMatch(Mockito.any());
	}

	@Test
	void createMatch_nullArenaUID_badRequest() throws Exception {
		var matchCreate = MatchTestData.getMatchCreateDto();
		matchCreate.setArenaUid(null);

		mockMvc
			.perform(MockMvcRequestBuilders.post("/v1/matches/")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objectMapper.writeValueAsString(matchCreate)))
			.andExpect(status().isBadRequest());

		Mockito.verify(matchFacade, Mockito.never()).createMatch(matchCreate);
	}

	@Test
	void createMatch_nullStartAt_badRequest() throws Exception {
		var matchCreate = MatchTestData.getMatchCreateDto();
		matchCreate.setStartAt(null);

		mockMvc
			.perform(MockMvcRequestBuilders.post("/v1/matches/")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objectMapper.writeValueAsString(matchCreate)))
			.andExpect(status().isBadRequest());

		Mockito.verify(matchFacade, Mockito.never()).createMatch(matchCreate);
	}

	@Test
	void createMatch_nullHomeTeamUid_badRequest() throws Exception {
		var matchCreate = MatchTestData.getMatchCreateDto();
		matchCreate.setHomeTeamUid(null);

		mockMvc
			.perform(MockMvcRequestBuilders.post("/v1/matches/")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objectMapper.writeValueAsString(matchCreate)))
			.andExpect(status().isBadRequest());

		Mockito.verify(matchFacade, Mockito.never()).createMatch(matchCreate);
	}

	@Test
	void createMatch_nullAwayTeamUid_badRequest() throws Exception {
		var matchCreate = MatchTestData.getMatchCreateDto();
		matchCreate.setAwayTeamUid(null);

		mockMvc
			.perform(MockMvcRequestBuilders.post("/v1/matches/")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objectMapper.writeValueAsString(matchCreate)))
			.andExpect(status().isBadRequest());

		Mockito.verify(matchFacade, Mockito.never()).createMatch(matchCreate);
	}

}