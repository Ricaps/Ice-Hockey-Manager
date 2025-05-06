package cz.fi.muni.pa165.gameservice.business.services;

import cz.fi.muni.pa165.gameservice.api.exception.ResourceNotFoundException;
import cz.fi.muni.pa165.gameservice.api.exception.ValidationHelper;
import cz.fi.muni.pa165.gameservice.persistence.entities.Arena;
import cz.fi.muni.pa165.gameservice.persistence.repositories.ArenaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

	public Page<Arena> findAllPageable(Pageable pageable) {
		return this.arenaRepository.findAll(pageable);
	}

	public Arena createArena(Arena arena) {
		ValidationHelper.requireNonNull(arena, "Please provide non-null arena for creation!");

		return arenaRepository.save(arena);
	}

	public Arena updateArena(Arena arena) {
		ValidationHelper.requireNonNull(arena, "Please provide non-null arena for update!");

		assertArenaExists(arena.getGuid());

		return arenaRepository.save(arena);
	}

	public void deleteArena(UUID arenaUid) {
		ValidationHelper.requireNonNull(arenaUid, "Please provide ID of the arena you want to delete");

		assertArenaExists(arenaUid);

		arenaRepository.deleteById(arenaUid);
	}

	public Arena getReferenceIfExists(UUID uuid) {
		assertArenaExists(uuid);

		return arenaRepository.getReferenceById(uuid);
	}

	private void assertArenaExists(UUID arenaUid) throws ResourceNotFoundException {
		if (!arenaRepository.existsById(arenaUid)) {
			throw new ResourceNotFoundException("Arena with id %s doesn't exists!".formatted(arenaUid));
		}

	}

}
