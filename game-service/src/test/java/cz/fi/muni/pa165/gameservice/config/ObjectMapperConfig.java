package cz.fi.muni.pa165.gameservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import cz.fi.muni.pa165.gameservice.testdata.MatchTestData;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.TimeZone;

@TestConfiguration
public class ObjectMapperConfig {

	@Bean
	public ObjectMapper objectMapper() {
		var objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		objectMapper.setTimeZone(TimeZone.getTimeZone(MatchTestData.ZONE_ID));

		return objectMapper;
	}

}
