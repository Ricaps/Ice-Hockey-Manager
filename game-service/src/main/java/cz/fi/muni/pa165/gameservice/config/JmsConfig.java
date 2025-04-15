package cz.fi.muni.pa165.gameservice.config;

import jakarta.jms.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

@Configuration
@EnableJms
public class JmsConfig {

	@Value("${spring.artemis.clientId:ihm-game-service}")
	private String clientID;

	@Bean
	@Primary
	public JmsTemplate queueJmsTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
		JmsTemplate template = new JmsTemplate(connectionFactory);
		template.setPubSubDomain(false);
		template.setMessageConverter(messageConverter);
		return template;
	}

	@Bean
	public JmsTemplate topicJmsTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
		JmsTemplate template = new JmsTemplate(connectionFactory);
		template.setPubSubDomain(true);
		template.setMessageConverter(messageConverter);
		return template;
	}

	@Bean
	@Primary
	public DefaultJmsListenerContainerFactory queueListenerFactory(ConnectionFactory connectionFactory,
			MessageConverter messageConverter) {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		factory.setConnectionFactory(connectionFactory);
		factory.setPubSubDomain(false);
		factory.setMessageConverter(messageConverter);
		factory.setSubscriptionDurable(true);
		factory.setClientId(clientID);
		return factory;
	}

	@Bean
	public DefaultJmsListenerContainerFactory topicListenerFactory(ConnectionFactory connectionFactory,
			MessageConverter messageConverter) {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		factory.setConnectionFactory(connectionFactory);
		factory.setPubSubDomain(true);
		factory.setMessageConverter(messageConverter);

		return factory;
	}

	@Bean
	public MessageConverter jacksonJmsMessageConverter() {
		MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
		converter.setTargetType(MessageType.TEXT);
		converter.setTypeIdPropertyName("_json");

		return converter;
	}

}
