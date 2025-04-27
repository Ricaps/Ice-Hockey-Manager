package cz.fi.muni.pa165.teamservice.business.services;

import cz.fi.muni.pa165.dto.gameservice.MatchViewDto;
import cz.fi.muni.pa165.messaging.BudgetChangeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 * @author Jan Martinek
 */
@Component
public class BudgetMessageListener {

	@Autowired
	private BudgetSystemService budgetService;

	@JmsListener(destination = "user.budget.change.amount", containerFactory = "queueListenerFactory")
	public void receiveMessageUser(BudgetChangeMessage budgetChangeMessage) {
		budgetService.processUserBudgetChange(budgetChangeMessage);
	}

	@JmsListener(destination = "game.match.result.queue", containerFactory = "queueListenerFactory")
	public void receiveMessageGame(MatchViewDto match) {
		budgetService.processGameMessage(match);
	}

}
