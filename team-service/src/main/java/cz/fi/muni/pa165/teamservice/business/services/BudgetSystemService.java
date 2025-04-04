package cz.fi.muni.pa165.teamservice.business.services;

import cz.fi.muni.pa165.teamservice.api.exception.ResourceAlreadyExistsException;
import cz.fi.muni.pa165.teamservice.api.exception.ResourceNotFoundException;
import cz.fi.muni.pa165.teamservice.persistence.entities.BudgetSystem;
import cz.fi.muni.pa165.teamservice.persistence.repositories.BudgetSystemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * @author Jan Martinek
 */
@Service
@Transactional
public class BudgetSystemService {

	private final BudgetSystemRepository budgetSystemRepository;

	@Autowired
	public BudgetSystemService(BudgetSystemRepository budgetSystemRepository) {
		this.budgetSystemRepository = budgetSystemRepository;
	}

	public BudgetSystem createBudgetSystem(BudgetSystem budgetSystem) throws ResourceAlreadyExistsException {
		if (budgetSystem.getGuid() != null && budgetSystemRepository.existsById(budgetSystem.getGuid())) {
			throw new ResourceAlreadyExistsException("BudgetSystem already exists");
		}
		return budgetSystemRepository.save(budgetSystem);
	}

	public BudgetSystem updateBudgetSystem(BudgetSystem budgetSystem) throws ResourceNotFoundException {
		if (!budgetSystemRepository.existsById(budgetSystem.getGuid())) {
			throw new ResourceNotFoundException("BudgetSystem not found");
		}
		return budgetSystemRepository.save(budgetSystem);
	}

	public void deleteBudgetSystem(UUID id) throws ResourceNotFoundException {
		if (!budgetSystemRepository.existsById(id)) {
			throw new ResourceNotFoundException("BudgetSystem not found");
		}
		budgetSystemRepository.deleteById(id);
	}

	@Transactional(readOnly = true)
	public BudgetSystem findById(UUID id) throws ResourceNotFoundException {
		return budgetSystemRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("BudgetSystem not found"));
	}

}
