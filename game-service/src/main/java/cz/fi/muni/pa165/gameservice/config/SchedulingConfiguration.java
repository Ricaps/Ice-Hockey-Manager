package cz.fi.muni.pa165.gameservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@Getter
@Setter
public class SchedulingConfiguration {

	/**
	 * Defines in hour when the match will be loaded from the database and scheduled
	 */
	@Value("${tasks.schedule.offset.match-schedule-offset:1}")
	private int matchScheduleOffset;

	@Value("${tasks.schedule.placeholder.match:5000}")
	private int matchSleepPlaceholder;

}
