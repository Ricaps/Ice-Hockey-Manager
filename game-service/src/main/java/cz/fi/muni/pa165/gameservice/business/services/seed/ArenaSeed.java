package cz.fi.muni.pa165.gameservice.business.services.seed;

import cz.fi.muni.pa165.gameservice.persistence.entities.Arena;
import cz.fi.muni.pa165.gameservice.persistence.repositories.ArenaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@Order(1)
public class ArenaSeed implements Seed<Arena> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ArenaSeed.class);

	private final ArenaRepository arenaRepository;

	private List<Arena> data;

	@Autowired
	public ArenaSeed(ArenaRepository arenaRepository) {
		this.arenaRepository = arenaRepository;
	}

	@Override
	public void runSeed(boolean logData) {
		if (arenaRepository.count() != 0) {
			LOGGER.info("Arena entities are already seeded. Skipping...");
			return;
		}

		List<Arena> arenas = new ArrayList<>(getTemplateData());
		for (var arena : arenas) {
			arena.setGuid(null);
		}

		data = arenaRepository.saveAll(arenas);
		if (logData) {
			LOGGER.debug("Seeded data: {}", data);
		}
	}

	public static List<Arena> getTemplateData() {
		return List.of(
				Arena.builder()
					.guid(UUID.randomUUID())
					.countryCode("CAN")
					.cityName("Montreal")
					.arenaName("Bell Centre")
					.build(),
				Arena.builder()
					.guid(UUID.randomUUID())
					.countryCode("CAN")
					.cityName("Toronto")
					.arenaName("Scotiabank Arena")
					.build(),
				Arena.builder()
					.guid(UUID.randomUUID())
					.countryCode("USA")
					.cityName("New York")
					.arenaName("Madison Square Garden")
					.build(),
				Arena.builder()
					.guid(UUID.randomUUID())
					.countryCode("USA")
					.cityName("Chicago")
					.arenaName("United Center")
					.build(),
				Arena.builder()
					.guid(UUID.randomUUID())
					.countryCode("USA")
					.cityName("Boston")
					.arenaName("TD Garden")
					.build(),
				Arena.builder()
					.guid(UUID.randomUUID())
					.countryCode("CAN")
					.cityName("Edmonton")
					.arenaName("Rogers Place")
					.build(),
				Arena.builder()
					.guid(UUID.randomUUID())
					.countryCode("USA")
					.cityName("Las Vegas")
					.arenaName("T-Mobile Arena")
					.build(),
				Arena.builder()
					.guid(UUID.randomUUID())
					.countryCode("USA")
					.cityName("Nashville")
					.arenaName("Bridgestone Arena")
					.build(),
				Arena.builder()
					.guid(UUID.randomUUID())
					.countryCode("USA")
					.cityName("Seattle")
					.arenaName("Climate Pledge Arena")
					.build(),
				Arena.builder()
					.guid(UUID.randomUUID())
					.countryCode("USA")
					.cityName("Denver")
					.arenaName("Ball Arena")
					.build(),
				Arena.builder()
					.guid(UUID.randomUUID())
					.countryCode("FIN")
					.cityName("Helsinki")
					.arenaName("Hartwall Arena")
					.build(),
				Arena.builder()
					.guid(UUID.randomUUID())
					.countryCode("CZE")
					.cityName("Prague")
					.arenaName("O2 Arena")
					.build(),
				Arena.builder()
					.guid(UUID.randomUUID())
					.countryCode("SWE")
					.cityName("Stockholm")
					.arenaName("Avicii Arena")
					.build(),
				Arena.builder()
					.guid(UUID.randomUUID())
					.countryCode("GER")
					.cityName("Cologne")
					.arenaName("Lanxess Arena")
					.build(),
				Arena.builder()
					.guid(UUID.randomUUID())
					.countryCode("SUI")
					.cityName("Bern")
					.arenaName("PostFinance Arena")
					.build(),
				Arena.builder()
					.guid(UUID.randomUUID())
					.countryCode("SRB")
					.cityName("Belgrade")
					.arenaName("Štark Arena")
					.build(),
				Arena.builder()
					.guid(UUID.randomUUID())
					.countryCode("CAN")
					.cityName("Calgary")
					.arenaName("Scotiabank Saddledome")
					.build(),
				Arena.builder()
					.guid(UUID.randomUUID())
					.countryCode("RUS")
					.cityName("Moscow")
					.arenaName("Sportpalace Luzhniki")
					.build(),
				Arena.builder()
					.guid(UUID.randomUUID())
					.countryCode("FIN")
					.cityName("Helsinki")
					.arenaName("Helsingin Jäähalli")
					.build(),
				Arena.builder()
					.guid(UUID.randomUUID())
					.countryCode("RUS")
					.cityName("Saint Petersburg")
					.arenaName("Ice Palace")
					.build());
	}

	@Override
	public List<Arena> getData() {
		return this.data;
	}

	@Override
	public void clearData() {
		LOGGER.debug("Cleared data of {}", this.getClass().getSimpleName());
		this.arenaRepository.deleteAll();
	}

}
