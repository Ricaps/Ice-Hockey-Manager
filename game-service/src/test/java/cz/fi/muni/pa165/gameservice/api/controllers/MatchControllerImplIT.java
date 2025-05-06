package cz.fi.muni.pa165.gameservice.api.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.fi.muni.pa165.dto.gameservice.MatchViewDto;
import cz.fi.muni.pa165.gameservice.api.exception.ResourceNotFoundException;
import cz.fi.muni.pa165.gameservice.api.exception.ValueIsMissingException;
import cz.fi.muni.pa165.gameservice.business.mappers.MatchMapper;
import cz.fi.muni.pa165.gameservice.business.mappers.ResultMapper;
import cz.fi.muni.pa165.gameservice.config.ObjectMapperConfig;
import cz.fi.muni.pa165.gameservice.persistence.entities.MatchType;
import cz.fi.muni.pa165.gameservice.persistence.repositories.ArenaRepository;
import cz.fi.muni.pa165.gameservice.persistence.repositories.CompetitionRepository;
import cz.fi.muni.pa165.gameservice.persistence.repositories.MatchRepository;
import cz.fi.muni.pa165.gameservice.testdata.MatchTestData;
import cz.fi.muni.pa165.gameservice.testdata.factory.CompetitionITDataFactory;
import cz.fi.muni.pa165.gameservice.testdata.factory.MatchITDataFactory;
import org.apache.commons.math3.util.CombinatoricsUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.UUID;

import static cz.fi.muni.pa165.gameservice.utils.Assertions.exception;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(ObjectMapperConfig.class)
@Transactional
class MatchControllerImplIT {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	CompetitionRepository competitionRepository;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	MatchMapper matchMapper;

	@Autowired
	private MatchRepository matchRepository;

	@Autowired
	private MatchITDataFactory matchITDataFactory;

	@Autowired
	private CompetitionITDataFactory competitionITDataFactory;

	@Autowired
	private ResultMapper resultMapper;

	@Autowired
	private ArenaRepository arenaRepository;

	@Test
	void generateMatches_notExistingCompetition_returns404() throws Exception {
		var randomUUID = UUID.randomUUID();

		mockMvc.perform(post("/v1/matches/competition/{guid}", randomUUID))
			.andExpect(status().isNotFound())
			.andExpect(exception().isInstanceOf(ResourceNotFoundException.class)
				.hasMessage("Competition with guid %s was not found!", randomUUID));
	}

	@Test
	void generateMatches_noTeamAssigned_returnsBadRequest() throws Exception {
		var competition = competitionITDataFactory.getCompetitionWithoutTeams();

		mockMvc.perform(post("/v1/matches/competition/{guid}", competition.getGuid()))
			.andExpect(status().isBadRequest())
			.andExpect(exception().isInstanceOf(ValueIsMissingException.class)
				.hasMessage("Please assign some teams to the competition"));
	}

	@Test
	void generateMatches_alreadyGenerated_returnsTheSameList() throws Exception {
		var competition = competitionITDataFactory.getCompetitionWithMatches();

		var response = mockMvc.perform(post("/v1/matches/competition/{guid}", competition.getGuid()))
			.andExpect(status().isCreated())
			.andReturn()
			.getResponse();

		List<MatchViewDto> matches = mapResponseToMatchesList(response);

		assertThat(matches).size().isEqualTo(competition.getMatches().size());
	}

	@Test
	void generateMatches_validData_generate() throws Exception {
		var competition = competitionITDataFactory.getCompetitionWithoutMatches();
		var numberOfTeams = competition.getTeams();

		assertThat(numberOfTeams).isNotEmpty();

		var response = mockMvc.perform(post("/v1/matches/competition/{guid}", competition.getGuid()))
			.andExpect(status().isCreated())
			.andReturn()
			.getResponse();

		List<MatchViewDto> generatedMatches = mapResponseToMatchesList(response);

		assertThat(generatedMatches).size().isEqualTo(CombinatoricsUtils.binomialCoefficient(numberOfTeams.size(), 2));
		assertThat(generatedMatches)
			.containsAll(matchMapper.listEntitiesToListViews(competition.getMatches().stream().toList()));

		var competitionDb = competitionRepository.getCompetitionByGuid(competition.getGuid()).orElseThrow();
		assertThat(competitionDb.getMatches()).size()
			.isEqualTo(CombinatoricsUtils.binomialCoefficient(numberOfTeams.size(), 2));
	}

	@Test
	void getMatchesForCompetition_notExistingCompetition_returns404() throws Exception {
		var randomUUID = UUID.randomUUID();

		mockMvc.perform(get("/v1/matches/competition/{guid}", randomUUID))
			.andExpect(status().isNotFound())
			.andExpect(exception().isInstanceOf(ResourceNotFoundException.class)
				.hasMessage("Competition with guid %s was not found!", randomUUID));
	}

	@Test
	void getMatchesForCompetition_existingMatch_returnsMatch() throws Exception {
		var competition = competitionITDataFactory.getCompetitionWithMatches();

		var response = mockMvc.perform(get("/v1/matches/competition/{guid}", competition.getGuid()))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse();

		List<MatchViewDto> returnedMatches = mapResponseToMatchesList(response);

		var matchesDB = matchRepository.getMatchesByCompetition_Guid(competition.getGuid());

		assertThat(returnedMatches).containsAll(matchMapper.listEntitiesToListViews(matchesDB.stream().toList()));
	}

	@Test
	void getSingleMatch_nonExisting_returns404() throws Exception {
		var randomUUID = UUID.randomUUID();

		mockMvc.perform(get("/v1/matches/{guid}", randomUUID))
			.andExpect(status().isNotFound())
			.andExpect(exception().isInstanceOf(ResourceNotFoundException.class)
				.hasMessage("Match with UUID %s was not found", randomUUID));
	}

	@Test
	void getSingleMatch_withResult_returnsMatchWithResult() throws Exception {
		var match = matchITDataFactory.getMatchWithResult();

		var response = mockMvc.perform(get("/v1/matches/{guid}", match.getGuid()).queryParam("results", "true"))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse();

		MatchViewDto matchView = objectMapper.readValue(response.getContentAsString(), MatchViewDto.class);

		assertThat(matchView).isEqualTo(matchMapper.matchEntityToMatchViewDto(match));
		assertThat(matchView.getResult()).isNotNull();
		assertThat(matchView.getResult()).isEqualTo(resultMapper.mapEntityToView(match.getResult()));
	}

	@Test
	void getSingleMatch_withoutResult_returnsMatchWithResult() throws Exception {
		var match = matchITDataFactory.getMatchWithResult();

		var response = mockMvc.perform(get("/v1/matches/{guid}", match.getGuid()))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse();

		MatchViewDto matchView = objectMapper.readValue(response.getContentAsString(), MatchViewDto.class);

		assertThat(matchView).isEqualTo(matchMapper.matchEntityToMatchViewDtoIgnoreResult(match));
		assertThat(matchView.getResult()).isNull();
	}

	@Test
	void createMatch_notExistingArena_returns404() throws Exception {
		var match = MatchTestData.getMatchCreateDto();

		mockMvc
			.perform(post("/v1/matches/").contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objectMapper.writeValueAsString(match)))
			.andExpect(status().isNotFound())
			.andExpect(exception().isInstanceOf(ResourceNotFoundException.class)
				.hasMessage("Arena with id %s doesn't exists!".formatted(match.getArenaUid())));
	}

	@Test
	void createMatch_validData_returnsNewMatch() throws Exception {
		var matchCreate = MatchTestData.getMatchCreateDto();
		var arena = arenaRepository.findAll().getFirst();
		matchCreate.setArenaUid(arena.getGuid());

		var response = mockMvc
			.perform(post("/v1/matches/").contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objectMapper.writeValueAsString(matchCreate)))
			.andExpect(status().isCreated())
			.andReturn()
			.getResponse();

		MatchViewDto newMatch = objectMapper.readValue(response.getContentAsString(), MatchViewDto.class);

		assertThat(newMatch.getStartAt()).isEqualTo(matchCreate.getStartAt());
		assertThat(newMatch.getEndAt()).isNull();
		assertThat(newMatch.getMatchType()).isEqualTo(MatchType.FRIENDLY.toString());
		assertThat(newMatch.getResult()).isNull();
		assertThat(newMatch.getArena().getGuid()).isEqualTo(arena.getGuid());
		assertThat(newMatch.getHomeTeamUid()).isEqualTo(matchCreate.getHomeTeamUid());
		assertThat(newMatch.getAwayTeamUid()).isEqualTo(matchCreate.getAwayTeamUid());
	}

	private List<MatchViewDto> mapResponseToMatchesList(MockHttpServletResponse response)
			throws JsonProcessingException, UnsupportedEncodingException {
		return objectMapper.readValue(response.getContentAsString(),
				objectMapper.getTypeFactory().constructCollectionType(List.class, MatchViewDto.class));
	}

}