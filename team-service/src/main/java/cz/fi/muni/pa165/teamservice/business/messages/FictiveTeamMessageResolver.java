package cz.fi.muni.pa165.teamservice.business.messages;

import jakarta.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;

import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * @author Jan Martinek
 */

@Service
public class FictiveTeamMessageResolver {

	private static final Logger LOGGER = LoggerFactory.getLogger(FictiveTeamMessageResolver.class);

	private final JmsTemplate jmsTemplate;

	public FictiveTeamMessageResolver(@Qualifier("queueJmsTemplate") JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}

	public void sendUuidOfAddedPlayer(@Nonnull UUID playerUuid) {
		jmsTemplate.convertAndSend("team.player.added", playerUuid);

		LOGGER.debug("Send player uuid {}", playerUuid);
	}

}
