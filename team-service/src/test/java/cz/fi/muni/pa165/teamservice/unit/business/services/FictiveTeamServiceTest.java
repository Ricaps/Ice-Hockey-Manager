package cz.fi.muni.pa165.teamservice.unit.business.services;

import cz.fi.muni.pa165.teamservice.api.exception.ResourceAlreadyExistsException;
import cz.fi.muni.pa165.teamservice.api.exception.ResourceNotFoundException;
import cz.fi.muni.pa165.teamservice.business.services.FictiveTeamService;
import cz.fi.muni.pa165.teamservice.persistence.entities.FictiveTeam;
import cz.fi.muni.pa165.teamservice.persistence.repositories.FictiveTeamRepository;
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
class FictiveTeamServiceTest {

	private final UUID teamId = UUID.randomUUID();

	@Mock
	private FictiveTeamRepository teamRepository;

	@InjectMocks
	private FictiveTeamService teamService;

	private FictiveTeam team;

	@BeforeEach
	void setUp() {
		team = new FictiveTeam();
		team.setGuid(teamId);
		team.setName("Avengers");
	}

	@Test
	void createFictiveTeam_success() throws ResourceAlreadyExistsException {
		when(teamRepository.existsById(teamId)).thenReturn(false);
		when(teamRepository.save(team)).thenReturn(team);

		FictiveTeam result = teamService.createTeam(team);

		assertThat(result).isEqualTo(team);
		verify(teamRepository).save(team);
	}

	@Test
	void createFictiveTeam_throwsWhenExists() {
		when(teamRepository.existsById(teamId)).thenReturn(true);

		assertThatThrownBy(() -> teamService.createTeam(team)).isInstanceOf(ResourceAlreadyExistsException.class)
			.hasMessageContaining("already exists");
	}

	@Test
	void updateFictiveTeam_success() throws ResourceNotFoundException {
		when(teamRepository.existsById(teamId)).thenReturn(true);
		when(teamRepository.save(team)).thenReturn(team);

		FictiveTeam result = teamService.updateTeam(team);

		assertThat(result).isEqualTo(team);
		verify(teamRepository).save(team);
	}

	@Test
	void updateFictiveTeam_throwsWhenNotFound() {
		when(teamRepository.existsById(teamId)).thenReturn(false);

		assertThatThrownBy(() -> teamService.updateTeam(team)).isInstanceOf(ResourceNotFoundException.class);
	}

	@Test
	void deleteFictiveTeam_success() throws ResourceNotFoundException {
		when(teamRepository.existsById(teamId)).thenReturn(true);
		teamService.deleteTeam(teamId);
		verify(teamRepository).deleteById(teamId);
	}

	@Test
	void deleteFictiveTeam_throwsWhenNotFound() {
		when(teamRepository.existsById(teamId)).thenReturn(false);

		assertThatThrownBy(() -> teamService.deleteTeam(teamId)).isInstanceOf(ResourceNotFoundException.class);
	}

	@Test
	void findById_success() throws ResourceNotFoundException {
		when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));

		FictiveTeam result = teamService.findById(teamId);

		assertThat(result).isEqualTo(team);
	}

	@Test
	void findById_throwsWhenNotFound() {
		when(teamRepository.findById(teamId)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> teamService.findById(teamId)).isInstanceOf(ResourceNotFoundException.class);
	}

	@Test
	void findAll_success() {
		when(teamRepository.findAll()).thenReturn(List.of(team));

		List<FictiveTeam> result = teamService.findAll();

		assertThat(result).containsExactly(team);
	}

}
