package cz.fi.muni.pa165.gameservice.business.messages;

import cz.fi.muni.pa165.dto.gameservice.MatchViewDto;
import cz.fi.muni.pa165.gameservice.api.exception.ValidationHelper;
import cz.fi.muni.pa165.gameservice.business.mappers.MatchMapper;
import cz.fi.muni.pa165.gameservice.persistence.entities.Match;
import jakarta.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class MatchMessageResolver {

	private static final Logger LOGGER = LoggerFactory.getLogger(MatchMessageResolver.class);

	private final JmsTemplate jmsTemplate;

	private final MatchMapper matchMapper;

	public MatchMessageResolver(@Qualifier("queueJmsTemplate") JmsTemplate jmsTemplate, MatchMapper matchMapper) {
		this.jmsTemplate = jmsTemplate;
		this.matchMapper = matchMapper;
	}

	public void sendMatchEndedTopic(@Nonnull Match match) {
		ValidationHelper.requireNonNull(match, "Please provide non null match");

		var matchView = matchMapper.matchEntityToMatchViewDto(match);
		jmsTemplate.convertAndSend("game.match.result.queue", matchView);

		LOGGER.debug("Send topic 'game.match.result.queue' for match ID {}", matchView.getGuid());
	}

}
