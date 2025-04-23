package cz.fi.muni.pa165.gameservice.business.mappers;

import cz.fi.muni.pa165.gameservice.business.services.seed.ArenaSeed;
import cz.fi.muni.pa165.gameservice.persistence.entities.MatchType;
import cz.fi.muni.pa165.gameservice.testdata.MatchTestData;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class MatchMapperTest {

	private final MatchMapper matchMapper = Mappers.getMapper(MatchMapper.class);

	@Test
	void matchCreateToMatchEntity() {
		var createDto = MatchTestData.getMatchCreateDto();
		var arena = ArenaSeed.getTemplateData().getFirst();

		var matchEntity = matchMapper.matchCreateDtoToEntity(createDto, arena);
		assertThat(matchEntity.getMatchType()).isEqualTo(MatchType.FRIENDLY);
		assertThat(matchEntity.getStartAt()).isEqualTo(createDto.getStartAt());
		assertThat(matchEntity.getEndAt()).isNull();
		assertThat(matchEntity.getHomeTeamUid()).isEqualTo(createDto.getHomeTeamUid());
		assertThat(matchEntity.getAwayTeamUid()).isEqualTo(createDto.getAwayTeamUid());
		assertThat(matchEntity.getArena()).isEqualTo(arena);
	}

	@Test
	void matchEntityToMatchView() {
		var matchEntity = MatchTestData.getRandomMatches().stream().toList().getFirst();
		var matchViewDto = matchMapper.matchEntityToMatchViewDto(matchEntity);
		var result = MatchTestData.getResult(matchEntity.getGuid());
		matchEntity.setResult(result);

		assertThat(matchViewDto.getGuid()).isEqualTo(matchEntity.getGuid());
		assertThat(matchViewDto.getStartAt()).isEqualTo(matchEntity.getStartAt());
		assertThat(matchViewDto.getEndAt()).isEqualTo(matchEntity.getEndAt());
		assertThat(matchViewDto.getHomeTeamUid()).isEqualTo(matchEntity.getHomeTeamUid());
		assertThat(matchViewDto.getAwayTeamUid()).isEqualTo(matchEntity.getAwayTeamUid());
		assertThat(matchViewDto.getMatchType()).isEqualTo(matchEntity.getMatchType().toString());

		var arenaEntity = matchEntity.getArena();
		var arenaDto = matchViewDto.getArena();

		assertThat(arenaDto.getGuid()).isEqualTo(arenaEntity.getGuid());
		assertThat(arenaDto.getArenaName()).isEqualTo(arenaEntity.getArenaName());
		assertThat(arenaDto.getCityName()).isEqualTo(arenaEntity.getCityName());
		assertThat(arenaDto.getCountryCode()).isEqualTo(arenaEntity.getCountryCode());

		var resultDto = matchEntity.getResult();
		assertThat(resultDto.getMatchGuid()).isEqualTo(result.getMatchGuid());
		assertThat(resultDto.getScoreAwayTeam()).isEqualTo(result.getScoreAwayTeam());
		assertThat(resultDto.getScoreHomeTeam()).isEqualTo(result.getScoreHomeTeam());
		assertThat(resultDto.getWinnerTeam()).isEqualTo(result.getWinnerTeam());
	}

	@Test
	void matchListEntityToMatchListView() {
		var matchEntities = MatchTestData.getRandomMatches().stream().toList();
		var matchViewsDto = matchMapper.listEntitiesToListViews(matchEntities);

		assertThat(matchViewsDto).hasSize(matchEntities.size());
	}

	@Test
	void matchListEntityToView_ignoreResult() {
		var matches = MatchTestData.getRandomMatches().stream().toList();

		for (var match : matches) {
			var result = MatchTestData.getResult(match.getGuid());
			match.setResult(result);
		}

		var mappedDtos = matchMapper.listEntitiesToListViewsIgnoreResult(matches);
		for (var dto : mappedDtos) {
			assertThat(dto.getResult()).isNull();
		}
	}

	@Test
	void matchEntityToView_ignoreResults() {
		var match = MatchTestData.getRandomMatches().stream().toList().getFirst();
		var result = MatchTestData.getResult(match.getGuid());
		match.setResult(result);

		var mappedDto = matchMapper.matchEntityToMatchViewDtoIgnoreResult(match);

		assertThat(mappedDto.getResult()).isNull();
		assertThat(mappedDto.getGuid()).isEqualTo(match.getGuid());
		assertThat(mappedDto.getMatchType()).isEqualTo(match.getMatchType().toString());
		assertThat(mappedDto.getHomeTeamUid()).isEqualTo(match.getHomeTeamUid());
		assertThat(mappedDto.getAwayTeamUid()).isEqualTo(match.getAwayTeamUid());
		assertThat(mappedDto.getArena().getGuid()).isEqualTo(match.getArena().getGuid());
		assertThat(mappedDto.getStartAt()).isEqualTo(match.getStartAt());
		assertThat(mappedDto.getEndAt()).isEqualTo(match.getEndAt());
	}

}