package cz.fi.muni.pa165.teamservice.business.services;

import cz.fi.muni.pa165.dto.gameservice.MatchViewDto;
import cz.fi.muni.pa165.messaging.BudgetChangeMessage;
import cz.fi.muni.pa165.teamservice.api.exception.ResourceAlreadyExistsException;
import cz.fi.muni.pa165.teamservice.api.exception.ResourceNotFoundException;
import cz.fi.muni.pa165.teamservice.persistence.entities.BudgetSystem;
import cz.fi.muni.pa165.teamservice.persistence.repositories.BudgetSystemRepository;
import cz.fi.muni.pa165.teamservice.persistence.repositories.FictiveTeamRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
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

	private final FictiveTeamRepository fictiveTeamRepository;

	private static final Logger LOGGER = LoggerFactory.getLogger(BudgetSystemService.class);

	private static final double WINNER_PRIZE = 1000.0;

	private static final double LOSER_PRIZE = 500.0;

	private static final double DRAW_PRIZE = 750.0;

	@Autowired
	public BudgetSystemService(BudgetSystemRepository budgetSystemRepository,
			FictiveTeamRepository fictiveTeamRepository) {
		this.budgetSystemRepository = budgetSystemRepository;
		this.fictiveTeamRepository = fictiveTeamRepository;
	}

	public BudgetSystem createBudgetSystem(BudgetSystem budgetSystem) throws ResourceAlreadyExistsException {
		if (budgetSystem.getGuid() != null && budgetSystemRepository.existsById(budgetSystem.getGuid())) {
			throw new ResourceAlreadyExistsException("BudgetSystem already exists");
		}
		if (budgetSystem.getTeam() == null || !fictiveTeamRepository.existsById(budgetSystem.getTeam().getGuid())) {
			throw new ResourceNotFoundException("FictiveTeam not found");
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

	@Transactional
	public void processGameMessage(MatchViewDto match) {
		BudgetSystem homeBudget = getTeamBudget(match.getHomeTeamUid());
		BudgetSystem awayBudget = getTeamBudget(match.getAwayTeamUid());

		if (isDraw(match)) {
			updateBudgets(homeBudget, awayBudget, DRAW_PRIZE, DRAW_PRIZE);
		}
		else if (homeTeamWon(match)) {
			updateBudgets(homeBudget, awayBudget, WINNER_PRIZE, LOSER_PRIZE);
		}
		else {
			updateBudgets(awayBudget, homeBudget, WINNER_PRIZE, LOSER_PRIZE);
		}
	}

	private BudgetSystem getTeamBudget(UUID teamId) {
		return budgetSystemRepository.findByTeamGuid(teamId).orElse(null);
	}

	private boolean isDraw(MatchViewDto match) {
		return match.getResult() == null
				|| match.getResult().getScoreHomeTeam() == match.getResult().getScoreAwayTeam();
	}

	private boolean homeTeamWon(MatchViewDto match) {
		return match.getResult().getScoreHomeTeam() > match.getResult().getScoreAwayTeam();
	}

	private void updateBudgets(BudgetSystem firstTeam, BudgetSystem secondTeam, double firstAmount,
			double secondAmount) {
		updateBudget(firstTeam, firstAmount);
		updateBudget(secondTeam, secondAmount);
	}

	private void updateBudget(BudgetSystem budget, double amount) {
		if (budget != null) {
			double oldAmount = budget.getAmount();
			budget.setAmount(oldAmount + amount);
			budgetSystemRepository.save(budget);
			LOGGER.debug("Updated budget: {} -> {}", oldAmount, budget.getAmount());
		}
	}

	@Transactional

	public void processUserBudgetChange(BudgetChangeMessage budgetChangeMessage) {

		UUID ownerId = budgetChangeMessage.getUserId();
		int amountToAdd = budgetChangeMessage.getAmount();

		var teams = fictiveTeamRepository.findByOwnerId(ownerId);

		for (var team : teams) {
			BudgetSystem budget = team.getBudgetSystem();
			if (budget == null) {
				continue;
			}
			double oldAmount = budget.getAmount();
			budget.setAmount(oldAmount + amountToAdd);
			budgetSystemRepository.save(budget);
			LOGGER.debug("Updated budget for team {}: {} -> {}", team.getGuid(), oldAmount, budget.getAmount());

		}
	}

}
