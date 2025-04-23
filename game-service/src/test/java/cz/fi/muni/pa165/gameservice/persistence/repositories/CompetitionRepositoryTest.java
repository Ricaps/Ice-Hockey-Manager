package cz.fi.muni.pa165.gameservice.persistence.repositories;

import cz.fi.muni.pa165.gameservice.testdata.factory.CompetitionITDataFactory;
import cz.fi.muni.pa165.gameservice.utils.SeededJpaTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SeededJpaTest
class CompetitionRepositoryTest {

	@Autowired
	CompetitionRepository competitionRepository;

	@Autowired
	CompetitionITDataFactory competitionITDataFactory;

	@Test
	void getCompetitionByGuid_existingCompetition_shouldFind() {
		var savedCompetition = competitionITDataFactory.getCompetitionWithMatches();

		var foundCompetition = competitionRepository.getCompetitionByGuid(savedCompetition.getGuid());

		assertThat(foundCompetition).isPresent();
		assertThat(foundCompetition.get()).isEqualTo(savedCompetition);
	}

	@Test
	void getCompetitionByGuid_notExistingCompetition_shouldReturnEmpty() {
		var randomUUID = UUID.randomUUID();

		var competition = competitionRepository.getCompetitionByGuid(randomUUID);

		assertThat(competition).isEmpty();
	}

}