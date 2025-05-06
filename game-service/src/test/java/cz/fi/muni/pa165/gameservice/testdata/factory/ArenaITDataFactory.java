package cz.fi.muni.pa165.gameservice.testdata.factory;

import cz.fi.muni.pa165.gameservice.persistence.entities.Arena;
import cz.fi.muni.pa165.gameservice.persistence.repositories.ArenaRepository;
import cz.fi.muni.pa165.gameservice.persistence.repositories.MatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ArenaITDataFactory {

	private final ArenaRepository arenaRepository;

	private final MatchRepository matchRepository;

	@Autowired
	public ArenaITDataFactory(ArenaRepository arenaRepository, MatchRepository matchRepository) {
		this.arenaRepository = arenaRepository;
		this.matchRepository = matchRepository;
	}

	public Arena getArena() {
		return this.arenaRepository.findAll().getFirst();
	}

	public Arena getUsedArena() {
		for (var match : matchRepository.findAll()) {
			if (match.getArena() != null) {
				return match.getArena();
			}
		}

		return null;
	}

}
