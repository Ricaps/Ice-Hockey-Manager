package cz.fi.muni.pa165.gameservice.business.mappers;

import cz.fi.muni.pa165.dto.gameService.CompetitionTeamDto;
import cz.fi.muni.pa165.gameservice.persistence.entities.CompetitionHasTeam;
import cz.fi.muni.pa165.gameservice.testdata.CompetitionTestData;
import cz.fi.muni.pa165.gameservice.testdata.TeamsTestData;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class CompetitionMapperTest {

	CompetitionMapper competitionMapper = CompetitionMapper.INSTANCE;

	@Test
	void map_entityToViewDto_success() {
		var entity = CompetitionTestData.getCompetitionEntity();
		var teams = TeamsTestData.getTeamEntities();
		entity.setTeams(teams);
		var dto = competitionMapper.competitionToCompetitionViewDto(entity);

		assertThat(dto.getGuid()).isEqualTo(entity.getGuid());
		assertThat(dto.getName()).isEqualTo(entity.getName());
		assertThat(dto.getStartAt()).isEqualTo(entity.getStartAt());
		assertThat(dto.getEndAt()).isEqualTo(entity.getEndAt());

		var mappedTeams = dto.getTeams();
		var entityGuids = teams.stream().map(CompetitionHasTeam::getTeamUid).collect(Collectors.toSet());
		assertThat(mappedTeams.size()).isEqualTo(teams.size());
		assertThat(mappedTeams.stream().map(CompetitionTeamDto::getTeamUid).collect(Collectors.toSet()))
			.containsAll(entityGuids);
	}

	@Test
	void map_entityToViewDto_null() {
		assertThat(competitionMapper.competitionToCompetitionViewDto(null)).isNull();
	}

	@Test
	void map_createDtoToEntityWithoutUuid_success() {
		var createDto = CompetitionTestData.getCompetitionCreateDto();
		var entity = competitionMapper.competitionCreateDtoToCompetition(createDto);

		assertThat(entity.getName()).isEqualTo(createDto.getName());
		assertThat(entity.getStartAt()).isEqualTo(createDto.getStartAt());
		assertThat(entity.getEndAt()).isEqualTo(createDto.getEndAt());
		assertThat(entity.getTeams()).isEmpty();
		assertThat(entity.getGuid()).isNull();
	}

	@Test
	void map_createDtoToEntityWithUuid_success() {
		var createDto = CompetitionTestData.getCompetitionCreateDto();
		var guid = UUID.randomUUID();
		var entity = competitionMapper.competitionCreateDtoToCompetition(createDto, guid);

		assertThat(entity.getName()).isEqualTo(createDto.getName());
		assertThat(entity.getStartAt()).isEqualTo(createDto.getStartAt());
		assertThat(entity.getEndAt()).isEqualTo(createDto.getEndAt());
		assertThat(entity.getGuid()).isEqualTo(guid);
		assertThat(entity.getTeams()).isEmpty();
	}

	@Test
	void map_competitionTeamsDtoToCompetitionHasTeam() {
		var competition = CompetitionTestData.getCompetitionEntity();
		var assignedTeams = TeamsTestData.getAssignTeamDto(UUID.randomUUID());

		var mappedEntity = competitionMapper.competitionTeamsDtoToCompetitionHasTeam(assignedTeams, competition);
		assertThat(mappedEntity.getCompetition()).isEqualTo(competition);
		assertThat(mappedEntity.getTeamUid()).isEqualTo(assignedTeams.getAssignTeam());
	}

}