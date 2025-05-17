package cz.fi.muni.pa165.teamservice.unit.business.facades;

import cz.fi.muni.pa165.dto.teamservice.BudgetSystemCreateDTO;
import cz.fi.muni.pa165.dto.teamservice.BudgetSystemDTO;
import cz.fi.muni.pa165.dto.teamservice.BudgetSystemUpdateDTO;
import cz.fi.muni.pa165.teamservice.api.exception.ResourceAlreadyExistsException;
import cz.fi.muni.pa165.teamservice.api.exception.ResourceNotFoundException;
import cz.fi.muni.pa165.teamservice.business.facades.BudgetSystemFacade;
import cz.fi.muni.pa165.teamservice.business.mappers.BudgetSystemMapper;
import cz.fi.muni.pa165.teamservice.business.services.BudgetSystemService;
import cz.fi.muni.pa165.teamservice.persistence.entities.BudgetSystem;
import cz.fi.muni.pa165.teamservice.persistence.repositories.FictiveTeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Jan Martinek
 */
@ExtendWith(MockitoExtension.class)
public class BudgetSystemFacadeTest {

	private final UUID budgetSystemId = UUID.randomUUID();

	@Mock
	private BudgetSystemService service;

	@Mock
	private BudgetSystemMapper mapper;

	@Mock
	private FictiveTeamRepository fictiveTeamRepository;

	@InjectMocks
	private BudgetSystemFacade facade;

	private BudgetSystem budgetSystem;

	private BudgetSystemDTO budgetSystemDTO;

	private BudgetSystemCreateDTO createDTO;

	private BudgetSystemUpdateDTO updateDTO;

	@BeforeEach
	void setUp() {
		budgetSystem = new BudgetSystem();
		budgetSystem.setGuid(budgetSystemId);
		budgetSystem.setAmount(100000.0);

		budgetSystemDTO = new BudgetSystemDTO();
		budgetSystemDTO.setGuid(budgetSystemId);
		budgetSystemDTO.setAmount(100000.0);

		createDTO = new BudgetSystemCreateDTO();
		createDTO.setAmount(100000.0);

		updateDTO = new BudgetSystemUpdateDTO();
		updateDTO.setGuid(budgetSystemId);
		updateDTO.setAmount(150000.0);
	}

	@Test
	void createBudgetSystem() throws ResourceAlreadyExistsException {
		when(fictiveTeamRepository.existsById(createDTO.getTeamId())).thenReturn(true);

		when(mapper.toEntity(createDTO)).thenReturn(budgetSystem);
		when(service.createBudgetSystem(budgetSystem)).thenReturn(budgetSystem);
		when(mapper.toDto(budgetSystem)).thenReturn(budgetSystemDTO);

		BudgetSystemDTO result = facade.createBudgetSystem(createDTO);

		assertThat(result).isEqualTo(budgetSystemDTO);
		verify(service).createBudgetSystem(budgetSystem);
	}

	@Test
	void updateBudgetSystem() throws ResourceNotFoundException {
		when(mapper.toEntity(updateDTO)).thenReturn(budgetSystem);
		when(service.updateBudgetSystem(budgetSystem)).thenReturn(budgetSystem);
		when(mapper.toDto(budgetSystem)).thenReturn(budgetSystemDTO);

		BudgetSystemDTO result = facade.updateBudgetSystem(updateDTO);

		assertThat(result).isEqualTo(budgetSystemDTO);
		verify(service).updateBudgetSystem(budgetSystem);
	}

	@Test
	void deleteBudgetSystem() throws ResourceNotFoundException {
		facade.deleteBudgetSystem(budgetSystemId);
		verify(service).deleteBudgetSystem(budgetSystemId);
	}

	@Test
	void findById() throws ResourceNotFoundException {
		when(service.findById(budgetSystemId)).thenReturn(budgetSystem);
		when(mapper.toDto(budgetSystem)).thenReturn(budgetSystemDTO);

		BudgetSystemDTO result = facade.findById(budgetSystemId);

		assertThat(result).isEqualTo(budgetSystemDTO);
		verify(service).findById(budgetSystemId);
	}

}
