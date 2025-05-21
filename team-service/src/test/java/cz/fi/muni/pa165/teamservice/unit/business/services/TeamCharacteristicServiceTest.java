package cz.fi.muni.pa165.teamservice.unit.business.services;

import cz.fi.muni.pa165.enums.TeamCharacteristicType;
import cz.fi.muni.pa165.teamservice.api.exception.ResourceNotFoundException;
import cz.fi.muni.pa165.teamservice.business.services.TeamCharacteristicService;
import cz.fi.muni.pa165.teamservice.persistence.entities.FictiveTeam;
import cz.fi.muni.pa165.teamservice.persistence.entities.TeamCharacteristic;
import cz.fi.muni.pa165.teamservice.persistence.repositories.TeamCharacteristicRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Jan Martinek
 */

@ExtendWith(MockitoExtension.class)
class TeamCharacteristicServiceTest {

	private final UUID characteristicId = UUID.randomUUID();

	private final UUID teamId = UUID.randomUUID();

	@Mock
	private TeamCharacteristicRepository repository;

	@InjectMocks
	private TeamCharacteristicService service;

	private TeamCharacteristic characteristic;

	@BeforeEach
	void setUp() {
		FictiveTeam team = new FictiveTeam();
		team.setGuid(teamId);
		characteristic = new TeamCharacteristic();
		characteristic.setGuid(characteristicId);
		characteristic.setFictiveTeam(team);
		characteristic.setCharacteristicType(TeamCharacteristicType.STRENGTH);
		characteristic.setCharacteristicValue(85);
	}

	@Test
	void create_TeamCharacteristic_success() {
		when(repository.save(characteristic)).thenReturn(characteristic);

		TeamCharacteristic result = service.createTeamCharacteristic(characteristic);

		assertThat(result).isEqualTo(characteristic);
		verify(repository).save(characteristic);
	}

	@Test
	void update_TeamCharacteristic_success() throws ResourceNotFoundException {
		when(repository.existsById(characteristicId)).thenReturn(true);
		when(repository.save(characteristic)).thenReturn(characteristic);

		TeamCharacteristic result = service.updateTeamCharacteristic(characteristic);

		assertThat(result).isEqualTo(characteristic);
	}

	@Test
	void update_TeamCharacteristic_throwsWhenNotFound() {
		when(repository.existsById(characteristicId)).thenReturn(false);

		assertThatThrownBy(() -> service.updateTeamCharacteristic(characteristic))
			.isInstanceOf(ResourceNotFoundException.class);
	}

	@Test
	void delete_TeamCharacteristic_success() throws ResourceNotFoundException {
		when(repository.existsById(characteristicId)).thenReturn(true);
		service.deleteTeamCharacteristic(characteristicId);
		verify(repository).deleteById(characteristicId);
	}

	@Test
	void delete_TeamCharacteristic_throwsWhenNotFound() {
		when(repository.existsById(characteristicId)).thenReturn(false);

		assertThatThrownBy(() -> service.deleteTeamCharacteristic(characteristicId))
			.isInstanceOf(ResourceNotFoundException.class);
	}

	@Test
	void findById_success() throws ResourceNotFoundException {
		when(repository.findById(characteristicId)).thenReturn(Optional.of(characteristic));

		TeamCharacteristic result = service.findById(characteristicId);

		assertThat(result).isEqualTo(characteristic);
	}

	@Test
	void findById_throwsWhenNotFound() {
		when(repository.findById(characteristicId)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.findById(characteristicId)).isInstanceOf(ResourceNotFoundException.class);
	}

	@Test
	void findByTeamId_success() {
		when(repository.findByFictiveTeamGuid(teamId)).thenReturn(List.of(characteristic));

		List<TeamCharacteristic> result = service.findByTeamId(teamId);

		assertThat(result).containsExactly(characteristic);
	}

}