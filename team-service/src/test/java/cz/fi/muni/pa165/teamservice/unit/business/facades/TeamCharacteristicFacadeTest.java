package cz.fi.muni.pa165.teamservice.unit.business.facades;

import cz.fi.muni.pa165.dto.teamService.TeamCharacteristicCreateDTO;
import cz.fi.muni.pa165.dto.teamService.TeamCharacteristicDTO;
import cz.fi.muni.pa165.dto.teamService.TeamCharacteristicUpdateDTO;
import cz.fi.muni.pa165.enums.TeamCharacteristicType;
import cz.fi.muni.pa165.teamservice.api.exception.ResourceNotFoundException;
import cz.fi.muni.pa165.teamservice.business.facades.TeamCharacteristicFacade;
import cz.fi.muni.pa165.teamservice.business.mappers.TeamCharacteristicMapper;
import cz.fi.muni.pa165.teamservice.business.services.TeamCharacteristicService;
import cz.fi.muni.pa165.teamservice.persistence.entities.TeamCharacteristic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Jan Martinek
 */
@ExtendWith(MockitoExtension.class)
public class TeamCharacteristicFacadeTest {

	private final UUID characteristicId = UUID.randomUUID();

	private final UUID teamId = UUID.randomUUID();

	@Mock
	private TeamCharacteristicService service;

	@Mock
	private TeamCharacteristicMapper mapper;

	@InjectMocks
	private TeamCharacteristicFacade facade;

	private TeamCharacteristic characteristic;

	private TeamCharacteristicDTO characteristicDTO;

	private TeamCharacteristicCreateDTO createDTO;

	private TeamCharacteristicUpdateDTO updateDTO;

	@BeforeEach
	void setUp() {
		characteristic = new TeamCharacteristic();
		characteristic.setGuid(characteristicId);
		characteristic.setTeamId(teamId);
		characteristic.setCharacteristicType(TeamCharacteristicType.STRENGTH);
		characteristic.setCharacteristicValue(85);

		characteristicDTO = new TeamCharacteristicDTO();
		characteristicDTO.setGuid(characteristicId);
		characteristicDTO.setTeamId(teamId);
		characteristicDTO.setCharacteristicType(TeamCharacteristicType.STRENGTH);
		characteristicDTO.setCharacteristicValue(85);

		createDTO = new TeamCharacteristicCreateDTO();
		createDTO.setTeamId(teamId);
		createDTO.setCharacteristicType(TeamCharacteristicType.STRENGTH);
		createDTO.setCharacteristicValue(85);

		updateDTO = new TeamCharacteristicUpdateDTO();
		updateDTO.setId(characteristicId);
		updateDTO.setCharacteristicType(TeamCharacteristicType.SPEED);
		updateDTO.setCharacteristicValue(90);
	}

	@Test
	void create() {
		when(mapper.toEntity(createDTO)).thenReturn(characteristic);
		when(service.createTeamCharacteristic(characteristic)).thenReturn(characteristic);
		when(mapper.toDto(characteristic)).thenReturn(characteristicDTO);

		TeamCharacteristicDTO result = facade.create(createDTO);

		assertThat(result).isEqualTo(characteristicDTO);
		verify(service).createTeamCharacteristic(characteristic);
	}

	@Test
	void update() throws ResourceNotFoundException {
		when(mapper.toEntity(updateDTO)).thenReturn(characteristic);
		when(service.updateTeamCharacteristic(characteristic)).thenReturn(characteristic);
		when(mapper.toDto(characteristic)).thenReturn(characteristicDTO);

		TeamCharacteristicDTO result = facade.update(updateDTO);

		assertThat(result).isEqualTo(characteristicDTO);
		verify(service).updateTeamCharacteristic(characteristic);
	}

	@Test
	void delete() throws ResourceNotFoundException {
		facade.delete(characteristicId);
		verify(service).deleteTeamCharacteristic(characteristicId);
	}

	@Test
	void findById() throws ResourceNotFoundException {
		when(service.findById(characteristicId)).thenReturn(characteristic);
		when(mapper.toDto(characteristic)).thenReturn(characteristicDTO);

		TeamCharacteristicDTO result = facade.findById(characteristicId);

		assertThat(result).isEqualTo(characteristicDTO);
		verify(service).findById(characteristicId);
	}

	@Test
	void findByTeamId() {
		when(service.findByTeamId(teamId)).thenReturn(List.of(characteristic));
		when(mapper.toDto(characteristic)).thenReturn(characteristicDTO);

		List<TeamCharacteristicDTO> result = facade.findByTeamId(teamId);

		assertThat(result).containsExactly(characteristicDTO);
		verify(service).findByTeamId(teamId);
	}

}
