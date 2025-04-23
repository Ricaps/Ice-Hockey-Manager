package cz.fi.muni.pa165.gameservice.utils;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.FilterType;

import java.lang.annotation.*;

import static org.springframework.context.annotation.ComponentScan.Filter;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@DataJpaTest(showSql = false,
		includeFilters = {
				@Filter(type = FilterType.REGEX, pattern = "cz.fi.muni.pa165.gameservice.business.services.seed.*"),
				@Filter(type = FilterType.REGEX, pattern = "cz.fi.muni.pa165.gameservice.testdata.factory.*"),
				@Filter(type = FilterType.REGEX, pattern = "cz.fi.muni.pa165.gameservice.business.facades.SeedFacade"),
				@Filter(type = FilterType.REGEX, pattern = "cz.fi.muni.pa165.gameservice.config.SeedConfiguration") })
public @interface SeededJpaTest {

}
