package cz.fi.muni.pa165.teamservice.business.services;

import cz.fi.muni.pa165.teamservice.api.exception.ResourceAlreadyExistsException;
import cz.fi.muni.pa165.teamservice.api.exception.ResourceNotFoundException;
import cz.fi.muni.pa165.teamservice.business.messages.FictiveTeamMessageResolver;
import cz.fi.muni.pa165.teamservice.persistence.entities.FictiveTeam;
import cz.fi.muni.pa165.teamservice.persistence.repositories.FictiveTeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * @author Jan Martinek
 */
@Service
@Transactional
public class FictiveTeamService {

	private final FictiveTeamRepository teamRepository;

	private final FictiveTeamMessageResolver fictiveTeamMessageResolver;

	@Autowired
	public FictiveTeamService(FictiveTeamRepository teamRepository,
			FictiveTeamMessageResolver fictiveTeamMessageResolver) {

		this.teamRepository = teamRepository;
		this.fictiveTeamMessageResolver = fictiveTeamMessageResolver;

	}

	public FictiveTeam createTeam(FictiveTeam team) throws ResourceAlreadyExistsException {
		if (team.getGuid() != null && teamRepository.existsById(team.getGuid())) {
			throw new ResourceAlreadyExistsException("Team already exists");
		}

		if (team.getPlayerIDs() != null && !team.getPlayerIDs().isEmpty()) {
			team.getPlayerIDs().forEach(fictiveTeamMessageResolver::sendUuidOfAddedPlayer);
		}

		return teamRepository.save(team);
	}

	public FictiveTeam updateTeam(FictiveTeam team) throws ResourceNotFoundException {
		if (!teamRepository.existsById(team.getGuid())) {
			throw new ResourceNotFoundException("Team not found");
		}
		FictiveTeam existingTeam = teamRepository.findById(team.getGuid())
			.orElseThrow(() -> new ResourceNotFoundException("Team not found"));

		List<UUID> newPlayers = team.getPlayerIDs()
			.stream()
			.filter(playerId -> !existingTeam.getPlayerIDs().contains(playerId))
			.toList();

		if (!newPlayers.isEmpty()) {
			newPlayers.forEach(fictiveTeamMessageResolver::sendUuidOfAddedPlayer);
		}
		return teamRepository.save(team);
	}

	public void deleteTeam(UUID teamId) throws ResourceNotFoundException {
		if (!teamRepository.existsById(teamId)) {
			throw new ResourceNotFoundException("Team not found");
		}
		teamRepository.deleteById(teamId);
	}

	@Transactional(readOnly = true)
	public FictiveTeam findById(UUID teamId) throws ResourceNotFoundException {
		return teamRepository.findById(teamId).orElseThrow(() -> new ResourceNotFoundException("Team not found"));
	}

	@Transactional(readOnly = true)
	public List<FictiveTeam> findAll() {
		return teamRepository.findAll();
	}

	@Transactional(readOnly = true)
	public List<FictiveTeam> findByOwnerId(UUID ownerId) {
		return teamRepository.findByOwnerId(ownerId);
	}

}
