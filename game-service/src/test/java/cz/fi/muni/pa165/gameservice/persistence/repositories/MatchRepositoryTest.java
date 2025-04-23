package cz.fi.muni.pa165.gameservice.persistence.repositories;

import cz.fi.muni.pa165.gameservice.persistence.entities.Match;
import cz.fi.muni.pa165.gameservice.persistence.entities.MatchType;
import cz.fi.muni.pa165.gameservice.persistence.entities.Result;
import cz.fi.muni.pa165.gameservice.testdata.factory.CompetitionITDataFactory;
import cz.fi.muni.pa165.gameservice.testdata.factory.MatchITDataFactory;
import cz.fi.muni.pa165.gameservice.utils.SeededJpaTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;

@SeededJpaTest
class MatchRepositoryTest {

	@Autowired
	TestEntityManager testEntityManager;

	@Autowired
	MatchRepository matchRepository;

	@Autowired
	CompetitionITDataFactory competitionITDataFactory;

	@Autowired
	MatchITDataFactory matchITDataFactory;

	private static List<Match> filterMatches(HashMap<Integer, Match> matches, int minutesInclude,
			Predicate<Integer> predicate) {
		return matches.keySet()
			.stream()
			.filter(minutes -> minutes <= minutesInclude)
			.filter(predicate != null ? predicate : key -> true)
			.map(matches::get)
			.toList();
	}

	@Test
	void getMatchesByCompetitionGuid_existingMatches_shouldReturnList() {
		var competition = competitionITDataFactory.getCompetitionWithMatches();
		var matches = competition.getMatches();

		var foundMatches = matchRepository.getMatchesByCompetition_Guid(competition.getGuid());

		assertThat(foundMatches).size().isEqualTo(matches.size());
		assertThat(foundMatches).containsAll(matches);
	}

	@Test
	void getMatchesByCompetitionGuid_competitionWithoutMatch_shouldReturnEmpty() {
		var competition = competitionITDataFactory.getCompetitionWithoutMatches();

		var foundMatches = matchRepository.getMatchesByCompetition_Guid(competition.getGuid());

		assertThat(foundMatches).isEmpty();
	}

	@Test
	void getMatchByGuid_nonExistingMatch_shouldReturnEmpty() {
		var randomUUID = UUID.randomUUID();

		var foundMatch = matchRepository.getMatchByGuid(randomUUID);

		assertThat(foundMatch).isEmpty();
	}

	@Test
	void getMatchByGuid_ExistingMatch_shouldReturnMatch() {
		var match = matchITDataFactory.getMatch();

		var foundMatch = matchRepository.getMatchByGuid(match.getGuid());

		assertThat(foundMatch).isPresent();
		assertThat(foundMatch.get()).isEqualTo(match);
	}

	@ParameterizedTest
	@ValueSource(ints = { 60, 90 })
	void getMatchesForScheduling_nextHour_shouldReturnMatches(int plusMinutes) {
		var matches = createMatchForScheduling();
		var nextHourTime = OffsetDateTime.now().plusMinutes(plusMinutes);
		var filteredMatches = filterMatches(matches, plusMinutes, null);

		var foundMatches = matchRepository.getMatchesForScheduling(nextHourTime);

		assertThat(foundMatches).size().isEqualTo(filteredMatches.size());
		assertThat(foundMatches).containsAll(filteredMatches);
	}

	@Test
	void getMatchesForScheduling_withResult_shouldReturnMatchesWithoutResult() {
		var matches = createMatchForScheduling();
		addResult(matches.get(30));
		var nextHourTime = OffsetDateTime.now().plusMinutes(60);
		var filteredMatches = filterMatches(matches, 60, key -> matches.get(key).getResult() == null);

		var foundMatches = matchRepository.getMatchesForScheduling(nextHourTime);

		assertThat(foundMatches).size().isEqualTo(filteredMatches.size());
		assertThat(foundMatches).containsAll(filteredMatches);
	}

	@Test
	void getMatchesForScheduling_withEndAt_shouldReturnMatchesWithoutEndAt() {
		var matches = createMatchForScheduling();
		addEndAt(matches.get(-1));
		var nextHourTime = OffsetDateTime.now().plusMinutes(60);
		var filteredMatches = filterMatches(matches, 60, key -> matches.get(key).getEndAt() == null);

		var foundMatches = matchRepository.getMatchesForScheduling(nextHourTime);

		assertThat(foundMatches).size().isEqualTo(filteredMatches.size());
		assertThat(foundMatches).containsAll(filteredMatches);
	}

	private HashMap<Integer, Match> createMatchForScheduling() {
		var referenceMatch = matchITDataFactory.getMatch();

		var minutes = new Integer[] { -1, 30, 60, 90 };

		var persistedMatches = new HashMap<Integer, Match>();
		for (var minute : minutes) {
			var forSchedulingMatch = Match.builder()
				.matchType(MatchType.FRIENDLY)
				.arena(referenceMatch.getArena())
				.startAt(OffsetDateTime.now().plusMinutes(minute))
				.homeTeamUid(referenceMatch.getHomeTeamUid())
				.awayTeamUid(referenceMatch.getAwayTeamUid())
				.build();
			persistedMatches.put(minute, testEntityManager.persist(forSchedulingMatch));
		}

		testEntityManager.flush();
		return persistedMatches;
	}

	private void addResult(Match match) {
		var result = Result.builder()
			.match(match)
			.matchGuid(match.getGuid())
			.winnerTeam(UUID.randomUUID())
			.scoreAwayTeam(1)
			.scoreHomeTeam(2)
			.build();

		testEntityManager.persist(result);
		testEntityManager.merge(match).setResult(result);
		testEntityManager.flush();
	}

	private void addEndAt(Match match) {
		var endAt = match.getStartAt().plusMinutes(50);

		testEntityManager.merge(match).setEndAt(endAt);
		testEntityManager.flush();
	}

}