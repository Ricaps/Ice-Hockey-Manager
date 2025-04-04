package cz.fi.muni.pa165.gameservice.testdata;

import cz.fi.muni.pa165.dto.gameService.ArenaViewDto;
import cz.fi.muni.pa165.dto.gameService.MatchCreateDto;
import cz.fi.muni.pa165.dto.gameService.MatchViewDto;
import cz.fi.muni.pa165.gameservice.business.services.seed.ArenaSeed;
import cz.fi.muni.pa165.gameservice.persistence.entities.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

public class MatchTestData {

	public static final ZoneId ZONE_ID = ZoneId.of("Europe/Prague");

	public static Set<Match> getRandomMatches() {
		Set<Match> matches = new HashSet<>();
		Competition competition = CompetitionTestData.getCompetitionEntity();
		Arena arena = ArenaSeed.getTemplateData().getFirst();

		for (int i = 0; i < 20; i++) {
			UUID homeTeam = UUID.randomUUID();
			UUID awayTeam = UUID.randomUUID();

			Match match = Match.builder()
				.guid(UUID.randomUUID())
				.competition(competition)
				.arena(arena)
				.startAt(ZonedDateTime.now(ZONE_ID).plusDays(i))
				.endAt(ZonedDateTime.now(ZONE_ID).plusDays(i).plusHours(2))
				.homeTeamUid(homeTeam)
				.awayTeamUid(awayTeam)
				.matchType(MatchType.GROUP_STAGE)
				.build();

			matches.add(match);
		}
		return matches;
	}

	public static List<MatchViewDto> getMatchesView() {
		List<MatchViewDto> matches = new ArrayList<>();
		Arena arena = ArenaSeed.getTemplateData().getFirst();
		ArenaViewDto arenaView = ArenaViewDto.builder()
			.arenaName(arena.getArenaName())
			.countryCode(arena.getCountryCode())
			.cityName(arena.getCityName())
			.build();

		for (int i = 0; i < 20; i++) {
			UUID homeTeam = UUID.randomUUID();
			UUID awayTeam = UUID.randomUUID();

			var match = MatchViewDto.builder()
				.guid(UUID.randomUUID())
				.arena(arenaView)
				.startAt(ZonedDateTime.now(ZONE_ID).plusDays(i))
				.endAt(ZonedDateTime.now(ZONE_ID).plusDays(i).plusHours(2))
				.homeTeamUid(homeTeam)
				.awayTeamUid(awayTeam)
				.matchType(MatchType.GROUP_STAGE.toString())
				.build();

			matches.add(match);
		}
		return matches;
	}

	public static MatchCreateDto getMatchCreateDto() {
		return MatchCreateDto.builder()
			.arenaUid(UUID.randomUUID())
			.awayTeamUid(UUID.randomUUID())
			.homeTeamUid(UUID.randomUUID())
			.startAt(ZonedDateTime.now(ZONE_ID))
			.build();

	}

	public static Result getResult(UUID matchUUID) {
		return Result.builder()
			.matchGuid(matchUUID)
			.scoreAwayTeam(1)
			.scoreHomeTeam(1)
			.winnerTeam(UUID.randomUUID())
			.build();
	}

}
