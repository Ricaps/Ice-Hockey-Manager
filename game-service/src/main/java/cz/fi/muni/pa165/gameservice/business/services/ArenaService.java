package cz.fi.muni.pa165.gameservice.business.services;

import cz.fi.muni.pa165.gameservice.api.exception.ResourceNotFoundException;
import cz.fi.muni.pa165.gameservice.persistence.entities.Arena;
import cz.fi.muni.pa165.gameservice.persistence.repositories.ArenaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ArenaService {

	private final ArenaRepository arenaRepository;

	public ArenaService(ArenaRepository arenaRepository) {
		this.arenaRepository = arenaRepository;
	}

	public List<Arena> findAllArenas() {
		return this.arenaRepository.findAll();
	}

	public Arena getReferenceIfExists(UUID uuid) {
		if (!arenaRepository.existsById(uuid)) {
			throw new ResourceNotFoundException("Desired arena was not found in the system");
		}
		return arenaRepository.getReferenceById(uuid);
	}

}
