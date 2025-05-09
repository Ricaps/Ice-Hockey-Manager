package cz.fi.muni.pa165.gameservice.business.services.seed;

import cz.fi.muni.pa165.gameservice.persistence.entities.Match;
import cz.fi.muni.pa165.gameservice.persistence.entities.Result;
import cz.fi.muni.pa165.gameservice.persistence.repositories.MatchRepository;
import cz.fi.muni.pa165.gameservice.persistence.repositories.ResultRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Component
@Order(4)
public class ResultSeed implements Seed<Result> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ResultSeed.class);

	private final MatchRepository matchRepository;

	private final ResultRepository resultRepository;

	private List<Result> data;

	@Autowired
	public ResultSeed(MatchRepository matchRepository, ResultRepository resultRepository) {
		this.matchRepository = matchRepository;
		this.resultRepository = resultRepository;
	}

	@Override
	public void runSeed(boolean logData) {
		if (resultRepository.count() != 0) {
			LOGGER.info("Result entities are already seeded. Skipping...");
			return;
		}

		List<Result> results = new ArrayList<>(getTemplateData());

		data = resultRepository.saveAllAndFlush(results);
		var matchesToSave = new ArrayList<Match>();

		// Save back reference
		for (var result : data) {
			if (result == null) {
				continue;
			}
			var matchOptional = matchRepository.getMatchByGuid(result.getMatchGuid());
			matchOptional.ifPresent(match -> {
				match.setResult(result);
				matchesToSave.add(match);
			});
		}

		matchRepository.saveAll(matchesToSave);
		if (logData) {
			LOGGER.debug("Seeded data: {}", data);
		}
	}

	private List<Result> getTemplateData() {
		var matches = matchRepository.findAll();

		return matches.stream()
			.filter(match -> match.getEndAt() != null && match.getEndAt().isBefore(OffsetDateTime.now()))
			.map(match -> {
				int scoreHome = ThreadLocalRandom.current().nextInt(0, 6);
				int scoreAway = ThreadLocalRandom.current().nextInt(0, 6);

				UUID winner = null;
				if (scoreHome > scoreAway) {
					winner = match.getHomeTeamUid();
				}
				else if (scoreAway > scoreHome) {
					winner = match.getAwayTeamUid();
				} // draw = null winner

				return Result.builder()
					.matchGuid(match.getGuid())
					.winnerTeam(winner)
					.scoreHomeTeam(scoreHome)
					.scoreAwayTeam(scoreAway)
					.build();
			})
			.toList();
	}

	@Override
	public List<Result> getData() {
		return this.data;
	}

	@Override
	public void clearData() {
		LOGGER.debug("Cleared data of {}", this.getClass().getSimpleName());
		this.resultRepository.deleteAll();
	}

}
