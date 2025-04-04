package cz.fi.muni.pa165.teamservice.business.services;

import cz.fi.muni.pa165.teamservice.api.exception.ResourceNotFoundException;
import cz.fi.muni.pa165.teamservice.persistence.entities.TeamCharacteristic;
import cz.fi.muni.pa165.teamservice.persistence.repositories.TeamCharacteristicRepository;
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
public class TeamCharacteristicService {

	private final TeamCharacteristicRepository repository;

	@Autowired
	public TeamCharacteristicService(TeamCharacteristicRepository repository) {
		this.repository = repository;
	}

	public TeamCharacteristic createTeamCharacteristic(TeamCharacteristic characteristic) {
		return repository.save(characteristic);
	}

	public TeamCharacteristic updateTeamCharacteristic(TeamCharacteristic characteristic)
			throws ResourceNotFoundException {
		if (!repository.existsById(characteristic.getGuid())) {
			throw new ResourceNotFoundException("TeamCharacteristic not found");
		}
		return repository.save(characteristic);
	}

	public void deleteTeamCharacteristic(UUID id) throws ResourceNotFoundException {
		if (!repository.existsById(id)) {
			throw new ResourceNotFoundException("TeamCharacteristic not found");
		}
		repository.deleteById(id);
	}

	@Transactional(readOnly = true)
	public TeamCharacteristic findById(UUID id) throws ResourceNotFoundException {
		return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("TeamCharacteristic not found"));
	}

	@Transactional(readOnly = true)
	public List<TeamCharacteristic> findByTeamId(UUID teamId) {
		return repository.findByTeamId(teamId);
	}

}
