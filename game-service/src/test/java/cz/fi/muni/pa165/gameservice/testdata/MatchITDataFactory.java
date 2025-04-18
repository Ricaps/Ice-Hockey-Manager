package cz.fi.muni.pa165.gameservice.testdata;

import cz.fi.muni.pa165.gameservice.persistence.entities.Match;
import cz.fi.muni.pa165.gameservice.persistence.repositories.MatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MatchITDataFactory {

	private final MatchRepository matchRepository;

	@Autowired
	public MatchITDataFactory(MatchRepository matchRepository) {
		this.matchRepository = matchRepository;
	}

	public Match getMatchWithResult() {
		return this.matchRepository.findAll()
			.stream()
			.filter(match -> match.getResult() != null)
			.findAny()
			.orElseThrow();
	}

}
