package cz.fi.muni.pa165.gameservice.business.services.seed;

import cz.fi.muni.pa165.gameservice.persistence.entities.Competition;
import cz.fi.muni.pa165.gameservice.persistence.repositories.CompetitionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@Order(1)
public class CompetitionSeed implements Seed<Competition> {

	private static final Logger LOGGER = LoggerFactory.getLogger(CompetitionSeed.class);

	private final CompetitionRepository competitionRepository;

	private List<Competition> data;

	@Autowired
	public CompetitionSeed(CompetitionRepository competitionRepository) {
		this.competitionRepository = competitionRepository;
	}

	@Override
	public void runSeed(boolean logData) {
		if (competitionRepository.count() != 0) {
			LOGGER.info("Competition entities are already seeded. Skipping...");
			return;
		}

		List<Competition> competitions = new ArrayList<>(getTemplateData());

		data = competitionRepository.saveAllAndFlush(competitions);
		if (logData) {
			LOGGER.debug("Seeded data: {}", data);
		}
	}

	private List<Competition> getTemplateData() {
		return List.of(
				Competition.builder()
					.name("National Hockey League (NHL)")
					.startAt(LocalDate.now().plusDays(5))
					.endAt(LocalDate.now().plusDays(180))
					.build(),

				Competition.builder()
					.name("Kontinental Hockey League (KHL)")
					.startAt(LocalDate.now().plusDays(10))
					.endAt(LocalDate.now().plusDays(190))
					.build(),

				Competition.builder()
					.name("Swedish Hockey League (SHL)")
					.startAt(LocalDate.now().plusDays(3))
					.endAt(LocalDate.now().plusDays(170))
					.build(),

				Competition.builder()
					.name("Liiga (Finland)")
					.startAt(LocalDate.now().plusDays(8))
					.endAt(LocalDate.now().plusDays(160))
					.build(),

				Competition.builder()
					.name("Czech Extraliga (Tipsport Extraliga)")
					.startAt(LocalDate.now().plusDays(6))
					.endAt(LocalDate.now().plusDays(150))
					.build(),

				Competition.builder()
					.name("Swiss National League")
					.startAt(LocalDate.now().plusDays(12))
					.endAt(LocalDate.now().plusDays(165))
					.build(),

				Competition.builder()
					.name("Deutsche Eishockey Liga (DEL)")
					.startAt(LocalDate.now().plusDays(4))
					.endAt(LocalDate.now().plusDays(155))
					.build(),

				Competition.builder()
					.name("Austrian ICE Hockey League")
					.startAt(LocalDate.now().plusDays(9))
					.endAt(LocalDate.now().plusDays(145))
					.build(),

				Competition.builder()
					.name("British Elite Ice Hockey League (EIHL)")
					.startAt(LocalDate.now().plusDays(7))
					.endAt(LocalDate.now().plusDays(140))
					.build(),

				Competition.builder()
					.name("Norwegian Fjordkraft-ligaen")
					.startAt(LocalDate.now().plusDays(6))
					.endAt(LocalDate.now().plusDays(143))
					.build(),

				Competition.builder()
					.name("Danish Metal Ligaen")
					.startAt(LocalDate.now().plusDays(5))
					.endAt(LocalDate.now().plusDays(137))
					.build(),

				Competition.builder()
					.name("Slovak Extraliga (Tipos Extraliga)")
					.startAt(LocalDate.now().plusDays(8))
					.endAt(LocalDate.now().plusDays(146))
					.build(),

				Competition.builder()
					.name("French Ligue Magnus")
					.startAt(LocalDate.now().plusDays(7))
					.endAt(LocalDate.now().plusDays(133))
					.build(),

				Competition.builder()
					.name("Polska Hokej Liga (PHL)")
					.startAt(LocalDate.now().plusDays(11))
					.endAt(LocalDate.now().plusDays(139))
					.build(),

				Competition.builder()
					.name("Asia League Ice Hockey")
					.startAt(LocalDate.now().plusDays(4))
					.endAt(LocalDate.now().plusDays(120))
					.build(),

				Competition.builder()
					.name("Alps Hockey League")
					.startAt(LocalDate.now().plusDays(6))
					.endAt(LocalDate.now().plusDays(125))
					.build(),

				Competition.builder()
					.name("SPHL (Southern Professional Hockey League)")
					.startAt(LocalDate.now().plusDays(5))
					.endAt(LocalDate.now().plusDays(118))
					.build(),

				Competition.builder()
					.name("ECHL (East Coast Hockey League)")
					.startAt(LocalDate.now().plusDays(3))
					.endAt(LocalDate.now().plusDays(135))
					.build(),

				Competition.builder()
					.name("AHL (American Hockey League)")
					.startAt(LocalDate.now().plusDays(10))
					.endAt(LocalDate.now().plusDays(165))
					.build(),

				Competition.builder()
					.name("Romanian National League")
					.startAt(LocalDate.now().plusDays(6))
					.endAt(LocalDate.now().plusDays(112))
					.build(),

				Competition.builder()
					.name("Belarusian Extraleague")
					.startAt(LocalDate.now().plusDays(7))
					.endAt(LocalDate.now().plusDays(122))
					.build(),

				Competition.builder()
					.name("Latvian Hockey Higher League")
					.startAt(LocalDate.now().plusDays(9))
					.endAt(LocalDate.now().plusDays(127))
					.build(),

				Competition.builder()
					.name("Ukrainian Hockey League")
					.startAt(LocalDate.now().plusDays(8))
					.endAt(LocalDate.now().plusDays(119))
					.build(),

				Competition.builder()
					.name("Slovenian Ice Hockey League")
					.startAt(LocalDate.now().plusDays(5))
					.endAt(LocalDate.now().plusDays(115))
					.build(),

				Competition.builder()
					.name("Japanese Asia League Team Cup")
					.startAt(LocalDate.now().plusDays(4))
					.endAt(LocalDate.now().plusDays(113))
					.build(),

				Competition.builder()
					.name("Australian Ice Hockey League (AIHL)")
					.startAt(LocalDate.now().plusDays(10))
					.endAt(LocalDate.now().plusDays(110))
					.build(),

				Competition.builder()
					.name("Chinese Ice Hockey Championship")
					.startAt(LocalDate.now().plusDays(12))
					.endAt(LocalDate.now().plusDays(130))
					.build(),

				Competition.builder()
					.name("World Junior Ice Hockey Championship")
					.startAt(LocalDate.now().plusDays(2))
					.endAt(LocalDate.now().plusDays(25))
					.build(),

				Competition.builder()
					.name("IIHF World Championship")
					.startAt(LocalDate.now().plusDays(1))
					.endAt(LocalDate.now().plusDays(21))
					.build(),

				Competition.builder()
					.name("Olympic Ice Hockey Tournament")
					.startAt(LocalDate.now().plusDays(15))
					.endAt(LocalDate.now().plusDays(35))
					.build());
	}

	@Override
	public List<Competition> getData() {
		return this.data;
	}

}
