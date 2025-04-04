package cz.fi.muni.pa165.gameservice.business.facades;

import cz.fi.muni.pa165.gameservice.business.services.seed.Seed;
import cz.fi.muni.pa165.gameservice.business.services.seed.SeedDataNotExist;
import cz.fi.muni.pa165.gameservice.config.SeedConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

@Service
public class SeedFacade implements ApplicationRunner {

	private static final Logger LOGGER = LoggerFactory.getLogger(SeedFacade.class);

	private final SeedConfiguration seedConfiguration;

	private final ApplicationContext applicationContext;

	private final TransactionTemplate transactionTemplate;

	private final List<? extends Seed<?>> seeds;

	@Autowired
	public SeedFacade(SeedConfiguration seedConfiguration, ApplicationContext applicationContext,
			List<? extends Seed<?>> seeds, PlatformTransactionManager transactionManager) {
		this.seedConfiguration = seedConfiguration;
		this.applicationContext = applicationContext;
		this.seeds = seeds;
		this.transactionTemplate = new TransactionTemplate(transactionManager);
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
		transactionTemplate.executeWithoutResult(status -> {
			for (var included : seedConfiguration.getIncluded()) {
				LOGGER.info("Running seed for {}", included);
				try {
					var includedBean = applicationContext.getBean(included);
					includedBean.runSeed();
				}
				catch (BeansException e) {
					LOGGER.error("Cannot find bean definition for seed class {}", included, e);
				}
			}
		});
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		runSeed();
	}

}
