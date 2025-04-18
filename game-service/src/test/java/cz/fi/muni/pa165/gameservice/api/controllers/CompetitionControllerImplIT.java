package cz.fi.muni.pa165.gameservice.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.fi.muni.pa165.dto.gameService.AssignTeamDto;
import cz.fi.muni.pa165.dto.gameService.CompetitionCreateDto;
import cz.fi.muni.pa165.dto.gameService.CompetitionViewDto;
import cz.fi.muni.pa165.gameservice.api.exception.ActionForbidden;
import cz.fi.muni.pa165.gameservice.api.exception.ResourceAlreadyExists;
import cz.fi.muni.pa165.gameservice.api.exception.ResourceNotFoundException;
import cz.fi.muni.pa165.gameservice.business.services.seed.CompetitionSeed;
import cz.fi.muni.pa165.gameservice.config.ObjectMapperConfig;
import cz.fi.muni.pa165.gameservice.persistence.entities.Competition;
import cz.fi.muni.pa165.gameservice.persistence.entities.CompetitionHasTeam;
import cz.fi.muni.pa165.gameservice.persistence.repositories.CompetitionRepository;
import cz.fi.muni.pa165.gameservice.testdata.CompetitionITDataFactory;
import cz.fi.muni.pa165.gameservice.testdata.CompetitionTestData;
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
@Import(ObjectMapperConfig.class)
class CompetitionControllerImplIT {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	private CompetitionRepository competitionRepository;

	@Autowired
	private CompetitionSeed competitionSeed;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private CompetitionITDataFactory competitionITDataFactory;

	@Test
	void createCompetition_validData_competitionCreated() throws Exception {
		var competitionCreate = CompetitionTestData.getCompetitionCreateDto();

		var response = mockMvc
			.perform(post("/v1/competition/").contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objectMapper.writeValueAsString(competitionCreate)))
			.andExpect(status().isCreated())
			.andReturn()
			.getResponse();

		var competitionView = objectMapper.readValue(response.getContentAsString(), CompetitionViewDto.class);
		checkCreatedCompetition(competitionView, competitionCreate);
		var savedCompetition = competitionRepository.getCompetitionByGuid(competitionView.getGuid());
		assertThat(savedCompetition).isPresent();
		checkCreatedCompetition(savedCompetition.get(), competitionCreate);
	}

	@Test
	void getCompetition_existingCompetition_shouldBeReturned() throws Exception {
		var competitionCreate = competitionSeed.getData().getFirst();

		var response = mockMvc.perform(get("/v1/competition/{guid}", competitionCreate.getGuid()))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse();

		var competitionView = objectMapper.readValue(response.getContentAsString(), CompetitionViewDto.class);
		checkGetCompetition(competitionView, competitionCreate);
	}

	@Test
	void getCompetition_nonExistingCompetition_returns404() throws Exception {
		mockMvc.perform(get("/v1/competition/{guid}", UUID.randomUUID()))
			.andExpect(status().isNotFound())
			.andExpect(exception().isInstanceOf(ResourceNotFoundException.class))
			.andReturn();
	}

	@Test
	void updateCompetition_nonExistingCompetition_returns404() throws Exception {
		var competitionForUpdate = CompetitionTestData.getCompetitionCreateDto();

		mockMvc
			.perform(put("/v1/competition/{guid}", UUID.randomUUID()).contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objectMapper.writeValueAsString(competitionForUpdate)))
			.andExpect(status().isNotFound())
			.andExpect(exception().isInstanceOf(ResourceNotFoundException.class))
			.andReturn();
	}

	@Test
	void updateCompetition_isUpdated_returnsUpdated() throws Exception {
		var competition = competitionSeed.getData().getFirst();
		var competitionForUpdate = CompetitionTestData.getCompetitionCreateDto();

		var response = mockMvc
			.perform(put("/v1/competition/{guid}", competition.getGuid()).contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objectMapper.writeValueAsString(competitionForUpdate)))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse();

		var updatedCompetition = objectMapper.readValue(response.getContentAsString(), CompetitionViewDto.class);
		checkCreatedCompetition(updatedCompetition, competitionForUpdate);

		assertThat(competition.getGuid()).isEqualTo(updatedCompetition.getGuid());
		var savedCompetition = competitionRepository.getCompetitionByGuid(competition.getGuid());

		assertThat(savedCompetition).isPresent();
		checkCreatedCompetition(savedCompetition.get(), competitionForUpdate);
	}

	@Test
	void assignTeams_alreadyAssigned_returnsConflict() throws Exception {
		var competition = competitionITDataFactory.getCompetitionWithTeams();
		var assignedTeamGuid = competition.getTeams().stream().toList().getFirst().getTeamUid();
		var assignedTeams = AssignTeamDto.builder().assignTeam(assignedTeamGuid).build();

		mockMvc
			.perform(post("/v1/competition/{guid}/teams", competition.getGuid())
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objectMapper.writeValueAsString(assignedTeams)))
			.andExpect(status().isConflict())
			.andExpect(exception().isInstanceOf(ResourceAlreadyExists.class)
				.hasMessage("Team is already assigned to the competition"));
	}

	@Test
	void assignTeams_competitionDoesntExist_returnsNotFound() throws Exception {
		var assignedTeams = AssignTeamDto.builder().assignTeam(UUID.randomUUID()).build();

		mockMvc
			.perform(post("/v1/competition/{guid}/teams", UUID.randomUUID())
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objectMapper.writeValueAsString(assignedTeams)))
			.andExpect(status().isNotFound())
			.andExpect(exception().isInstanceOf(ResourceNotFoundException.class));
	}

	@Test
	void assignTeams_competitionRunning_actionForbidden() throws Exception {
		var competition = competitionITDataFactory.getRunningCompetition();
		var assignedTeams = AssignTeamDto.builder().assignTeam(UUID.randomUUID()).build();

		mockMvc
			.perform(post("/v1/competition/{guid}/teams", competition.getGuid())
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objectMapper.writeValueAsString(assignedTeams)))
			.andExpect(status().isForbidden())
			.andExpect(exception().isInstanceOf(ActionForbidden.class)
				.hasMessage("You cannot assign team to the competition, that is already running"));
	}

	@Test
	void assignTeams_validData_teamIsAssigned() throws Exception {
		var competition = competitionSeed.getData().getFirst();
		var assignedTeams = AssignTeamDto.builder().assignTeam(UUID.randomUUID()).build();

		mockMvc
			.perform(post("/v1/competition/{guid}/teams", competition.getGuid())
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(objectMapper.writeValueAsString(assignedTeams)))
			.andExpect(status().isCreated());

		var expectedCompetitionHasTeam = CompetitionHasTeam.builder()
			.competition(competition)
			.teamUid(assignedTeams.getAssignTeam())
			.build();

		var competitionDB = competitionRepository.getCompetitionByGuid(competition.getGuid()).orElseThrow();
		assertThat(competitionDB.getTeams().size()).isEqualTo(competition.getTeams().size() + 1);
		assertThat(competitionDB.getTeams()).contains(expectedCompetitionHasTeam);
	}

	private void checkCreatedCompetition(CompetitionViewDto competitionViewDto,
			CompetitionCreateDto competitionCreateDto) {
		assertThat(competitionViewDto.getName()).isEqualTo(competitionCreateDto.getName());
		assertThat(competitionViewDto.getStartAt()).isEqualTo(competitionCreateDto.getStartAt());
		assertThat(competitionViewDto.getEndAt()).isEqualTo(competitionCreateDto.getEndAt());
	}

	private void checkCreatedCompetition(Competition competition, CompetitionCreateDto competitionCreateDto) {
		assertThat(competition.getName()).isEqualTo(competitionCreateDto.getName());
		assertThat(competition.getStartAt()).isEqualTo(competitionCreateDto.getStartAt());
		assertThat(competition.getEndAt()).isEqualTo(competitionCreateDto.getEndAt());
	}

	private void checkGetCompetition(CompetitionViewDto competitionCreateDto, Competition competition) {
		assertThat(competitionCreateDto.getName()).isEqualTo(competition.getName());
		assertThat(competitionCreateDto.getStartAt()).isEqualTo(competition.getStartAt());
		assertThat(competitionCreateDto.getEndAt()).isEqualTo(competition.getEndAt());
	}

}