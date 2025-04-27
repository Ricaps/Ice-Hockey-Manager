package cz.fi.muni.pa165.teamservice.unit.business.services;

import cz.fi.muni.pa165.teamservice.api.exception.ResourceAlreadyExistsException;
import cz.fi.muni.pa165.teamservice.api.exception.ResourceNotFoundException;
import cz.fi.muni.pa165.teamservice.business.services.BudgetSystemService;
import cz.fi.muni.pa165.teamservice.persistence.entities.BudgetSystem;
import cz.fi.muni.pa165.teamservice.persistence.entities.FictiveTeam;
import cz.fi.muni.pa165.teamservice.persistence.repositories.BudgetSystemRepository;
import cz.fi.muni.pa165.teamservice.persistence.repositories.FictiveTeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
class BudgetSystemServiceTest {

	private final UUID budgetSystemId = UUID.randomUUID();

	@Mock
	private BudgetSystemRepository repository;

	@Mock
	private FictiveTeamRepository fictiveTeamRepository;

	@InjectMocks
	private BudgetSystemService service;

	private BudgetSystem budgetSystem;

	private FictiveTeam team;

	@BeforeEach
	void setUp() {
		team = new FictiveTeam();
		UUID teamId = UUID.randomUUID();
		team.setGuid(teamId);
		team.setName("Test Team");

		budgetSystem = new BudgetSystem();
		budgetSystem.setGuid(budgetSystemId);
		budgetSystem.setAmount(100000.0);
		budgetSystem.setTeam(team);
	}

	@Test
	void createBudgetSystem_success() throws ResourceAlreadyExistsException {
		when(repository.existsById(budgetSystemId)).thenReturn(false);
		when(fictiveTeamRepository.existsById(team.getGuid())).thenReturn(true);
		when(repository.save(budgetSystem)).thenReturn(budgetSystem);

		BudgetSystem result = service.createBudgetSystem(budgetSystem);

		assertThat(result).isEqualTo(budgetSystem);
		verify(repository).existsById(budgetSystemId);
		verify(fictiveTeamRepository).existsById(team.getGuid());
		verify(repository).save(budgetSystem);
	}

	@Test
	void createBudgetSystem_throwsWhenExists() {
		when(repository.existsById(budgetSystemId)).thenReturn(true);

		assertThatThrownBy(() -> service.createBudgetSystem(budgetSystem))
			.isInstanceOf(ResourceAlreadyExistsException.class);
	}

	@Test
	void updateBudgetSystem_success() throws ResourceNotFoundException {
		when(repository.existsById(budgetSystemId)).thenReturn(true);
		when(repository.save(budgetSystem)).thenReturn(budgetSystem);

		BudgetSystem result = service.updateBudgetSystem(budgetSystem);

		assertThat(result).isEqualTo(budgetSystem);
	}

	@Test
	void updateBudgetSystem_throwsWhenNotFound() {
		when(repository.existsById(budgetSystemId)).thenReturn(false);

		assertThatThrownBy(() -> service.updateBudgetSystem(budgetSystem))
			.isInstanceOf(ResourceNotFoundException.class);
	}

	@Test
	void deleteBudgetSystem_success() throws ResourceNotFoundException {
		when(repository.existsById(budgetSystemId)).thenReturn(true);
		service.deleteBudgetSystem(budgetSystemId);
		verify(repository).deleteById(budgetSystemId);
	}

	@Test
	void deleteBudgetSystem_throwsWhenNotFound() {
		when(repository.existsById(budgetSystemId)).thenReturn(false);

		assertThatThrownBy(() -> service.deleteBudgetSystem(budgetSystemId))
			.isInstanceOf(ResourceNotFoundException.class);
	}

	@Test
	void findById_success() throws ResourceNotFoundException {
		when(repository.findById(budgetSystemId)).thenReturn(Optional.of(budgetSystem));

		BudgetSystem result = service.findById(budgetSystemId);

		assertThat(result).isEqualTo(budgetSystem);
	}

	@Test
	void findById_throwsWhenNotFound() {
		when(repository.findById(budgetSystemId)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.findById(budgetSystemId)).isInstanceOf(ResourceNotFoundException.class);
	}

}