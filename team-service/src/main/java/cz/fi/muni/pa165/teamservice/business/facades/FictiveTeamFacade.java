package cz.fi.muni.pa165.teamservice.business.facades;

import cz.fi.muni.pa165.dto.teamservice.FictiveTeamCreateDTO;
import cz.fi.muni.pa165.dto.teamservice.FictiveTeamDTO;
import cz.fi.muni.pa165.dto.teamservice.FictiveTeamUpdateDTO;
import cz.fi.muni.pa165.teamservice.api.exception.ResourceAlreadyExistsException;
import cz.fi.muni.pa165.teamservice.api.exception.ResourceNotFoundException;
import cz.fi.muni.pa165.teamservice.business.mappers.FictiveTeamMapper;
import cz.fi.muni.pa165.teamservice.business.services.FictiveTeamService;
import cz.fi.muni.pa165.teamservice.business.services.TeamCharacteristicService;
import cz.fi.muni.pa165.teamservice.persistence.entities.FictiveTeam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author Jan Martinek
 */
@Service
@Transactional
public class FictiveTeamFacade {

	private final FictiveTeamService fictiveTeamService;

	private final FictiveTeamMapper fictiveTeamMapper;

	private final TeamCharacteristicService teamCharacteristicService;

	@Autowired
	public FictiveTeamFacade(FictiveTeamService fictiveTeamService, FictiveTeamMapper fictiveTeamMapper,
			TeamCharacteristicService teamCharacteristicService) {
		this.teamCharacteristicService = teamCharacteristicService;
		this.fictiveTeamService = fictiveTeamService;
		this.fictiveTeamMapper = fictiveTeamMapper;
	}

	public FictiveTeamDTO createFictiveTeam(FictiveTeamCreateDTO dto) throws ResourceAlreadyExistsException {
		var characteristicEntities = dto.getCharacteristicTypes();
		FictiveTeam fictiveTeam = fictiveTeamMapper.toEntity(dto);
		if (characteristicEntities != null) {
			fictiveTeam.setTeamCharacteristics(characteristicEntities.stream()
				.map(teamCharacteristicService::findById)
				.collect(Collectors.toSet()));
		}
		FictiveTeam created = fictiveTeamService.createTeam(fictiveTeam);
		return fictiveTeamMapper.toDto(created);
	}

	public FictiveTeamDTO updateFictiveTeam(FictiveTeamUpdateDTO dto) throws ResourceNotFoundException {
		var characteristicEntities = dto.getCharacteristicTypes();
		FictiveTeam fictiveTeam = fictiveTeamMapper.toEntity(dto);
		if (characteristicEntities != null) {
			fictiveTeam.setTeamCharacteristics(characteristicEntities.stream()
				.map(teamCharacteristicService::findById)
				.collect(Collectors.toSet()));
		}
		FictiveTeam updated = fictiveTeamService.updateTeam(fictiveTeam);
		return fictiveTeamMapper.toDto(updated);
	}

	public void deleteFictiveTeam(UUID fictiveTeamId) throws ResourceNotFoundException {
		fictiveTeamService.deleteTeam(fictiveTeamId);
	}

	@Transactional(readOnly = true)
	public FictiveTeamDTO findById(UUID fictiveTeamId) throws ResourceNotFoundException {
		return fictiveTeamMapper.toDto(fictiveTeamService.findById(fictiveTeamId));
	}

	@Transactional(readOnly = true)
	public List<FictiveTeamDTO> findAll() {
		return fictiveTeamService.findAll().stream().map(fictiveTeamMapper::toDto).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<FictiveTeamDTO> findByOwnerId(UUID ownerId) {
		return fictiveTeamService.findByOwnerId(ownerId)
			.stream()
			.map(fictiveTeamMapper::toDto)
			.collect(Collectors.toList());
	}

}
