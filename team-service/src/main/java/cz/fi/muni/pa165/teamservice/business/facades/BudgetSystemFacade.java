package cz.fi.muni.pa165.teamservice.business.facades;

import cz.fi.muni.pa165.dto.teamService.BudgetSystemCreateDTO;
import cz.fi.muni.pa165.dto.teamService.BudgetSystemDTO;
import cz.fi.muni.pa165.dto.teamService.BudgetSystemUpdateDTO;
import cz.fi.muni.pa165.teamservice.api.exception.ResourceAlreadyExistsException;
import cz.fi.muni.pa165.teamservice.api.exception.ResourceNotFoundException;
import cz.fi.muni.pa165.teamservice.business.mappers.BudgetSystemMapper;
import cz.fi.muni.pa165.teamservice.business.services.BudgetSystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * @author Jan Martinek
 */
@Service
@Transactional
public class BudgetSystemFacade {

	private final BudgetSystemService budgetSystemService;

	private final BudgetSystemMapper budgetSystemMapper;

	@Autowired
	public BudgetSystemFacade(BudgetSystemService budgetSystemService, BudgetSystemMapper budgetSystemMapper) {
		this.budgetSystemService = budgetSystemService;
		this.budgetSystemMapper = budgetSystemMapper;
	}

	public BudgetSystemDTO createBudgetSystem(BudgetSystemCreateDTO createDTO) throws ResourceAlreadyExistsException {
		return budgetSystemMapper.toDto(budgetSystemService.createBudgetSystem(budgetSystemMapper.toEntity(createDTO)));
	}

	public BudgetSystemDTO updateBudgetSystem(BudgetSystemUpdateDTO updateDTO) throws ResourceNotFoundException {
		return budgetSystemMapper.toDto(budgetSystemService.updateBudgetSystem(budgetSystemMapper.toEntity(updateDTO)));
	}

	public void deleteBudgetSystem(UUID id) throws ResourceNotFoundException {
		budgetSystemService.deleteBudgetSystem(id);
	}

	@Transactional(readOnly = true)
	public BudgetSystemDTO findById(UUID id) throws ResourceNotFoundException {
		return budgetSystemMapper.toDto(budgetSystemService.findById(id));
	}

}
