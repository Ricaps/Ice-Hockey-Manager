package cz.fi.muni.pa165.gameservice.business.facades;

import cz.fi.muni.pa165.gameservice.business.services.seed.Seed;
import cz.fi.muni.pa165.gameservice.business.services.seed.SeedDataNotExist;
import cz.fi.muni.pa165.gameservice.config.SeedConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SeedFacade implements ApplicationRunner {

	private static final Logger LOGGER = LoggerFactory.getLogger(SeedFacade.class);

	private final SeedConfiguration seedConfiguration;

	private final List<? extends Seed<?>> seeds;

	@Value("${server.database.clear:false}")
	private boolean shouldClear;

	@Autowired
	public SeedFacade(SeedConfiguration seedConfiguration, List<? extends Seed<?>> seeds) {
		this.seedConfiguration = seedConfiguration;
		this.seeds = seeds;
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> getDataOfSeed(Class<T> entity) {
		for (var seed : seeds) {
			var data = seed.getData();
			if (data == null || data.isEmpty())
				continue;

			if (data.getFirst().getClass() == entity) {
				return (List<T>) data;
			}
		}

		throw new SeedDataNotExist("Seed data for class %s doesn't exist!".formatted(entity));
	}

	private void runSeed() {
		if (!seedConfiguration.isEnabled()) {
			return;
		}
		seeds.sort(AnnotationAwareOrderComparator.INSTANCE);
		for (var seed : seeds) {
			LOGGER.info("Running seed for {}", seed.getClass());
			seed.runSeed(seedConfiguration.isLogData());
		}
	}

	private void clearDatabase() {
		if (!shouldClear) {
			return;
		}
		seeds.reversed().forEach(Seed::clearData);
	}

	@Override
	public void run(ApplicationArguments args) {
		clearDatabase();
		runSeed();
	}

}
