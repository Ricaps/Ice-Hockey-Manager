package cz.fi.muni.pa165.teamservice.business.facades;

import cz.fi.muni.pa165.dto.teamService.TeamCharacteristicCreateDTO;
import cz.fi.muni.pa165.dto.teamService.TeamCharacteristicDTO;
import cz.fi.muni.pa165.dto.teamService.TeamCharacteristicUpdateDTO;
import cz.fi.muni.pa165.teamservice.api.exception.ResourceNotFoundException;
import cz.fi.muni.pa165.teamservice.business.mappers.TeamCharacteristicMapper;
import cz.fi.muni.pa165.teamservice.business.services.TeamCharacteristicService;
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
public class TeamCharacteristicFacade {

	private final TeamCharacteristicService service;

	private final TeamCharacteristicMapper mapper;

	@Autowired
	public TeamCharacteristicFacade(TeamCharacteristicService service, TeamCharacteristicMapper mapper) {
		this.service = service;
		this.mapper = mapper;
	}

	public TeamCharacteristicDTO create(TeamCharacteristicCreateDTO createDTO) {
		return mapper.toDto(service.createTeamCharacteristic(mapper.toEntity(createDTO)));
	}

	public TeamCharacteristicDTO update(TeamCharacteristicUpdateDTO updateDTO) throws ResourceNotFoundException {
		return mapper.toDto(service.updateTeamCharacteristic(mapper.toEntity(updateDTO)));
	}

	public void delete(UUID id) throws ResourceNotFoundException {
		service.deleteTeamCharacteristic(id);
	}

	@Transactional(readOnly = true)
	public TeamCharacteristicDTO findById(UUID id) throws ResourceNotFoundException {
		return mapper.toDto(service.findById(id));
	}

	@Transactional(readOnly = true)
	public List<TeamCharacteristicDTO> findByTeamId(UUID teamId) {
		return service.findByTeamId(teamId).stream().map(mapper::toDto).toList();
	}

}
