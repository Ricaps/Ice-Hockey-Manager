package cz.fi.muni.pa165.userservice.business.messages;

import cz.fi.muni.pa165.messaging.BudgetChangeMessage;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class BudgetUpdateMessageResolver {

	private final JmsTemplate jmsTemplate;

	private final Validator validator;

	@Autowired
	public BudgetUpdateMessageResolver(JmsTemplate jmsTemplate, Validator validator) {
		this.jmsTemplate = jmsTemplate;
		this.validator = validator;
	}

	public void sendBudgetChangeMessage(UUID userId, int amount) {
		BudgetChangeMessage message = new BudgetChangeMessage(userId, amount);
		validator.validate(message);

		jmsTemplate.convertAndSend("user.budget.change.amount", message);

		log.debug("Sent budget increase message 'user.budget.increase.amount'. Message: {}", message);
	}

}
