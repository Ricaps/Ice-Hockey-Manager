package cz.fi.muni.pa165.userservice.config;

import jakarta.jms.ConnectionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

@Configuration
@EnableJms
public class JmsConfig {

	@Bean
	@Primary
	public JmsTemplate queueJmsTemplate(@Qualifier("jmsConnectionFactory") ConnectionFactory connectionFactory,
			MessageConverter messageConverter) {
		JmsTemplate template = new JmsTemplate(connectionFactory);
		template.setPubSubDomain(false);
		template.setMessageConverter(messageConverter);
		return template;
	}

	@Bean
	public MessageConverter messageConverter() {
		MappingJackson2MessageConverter messageConverter = new MappingJackson2MessageConverter();
		messageConverter.setTargetType(MessageType.TEXT);
		messageConverter.setTypeIdPropertyName("_json");
		return messageConverter;
	}

}
