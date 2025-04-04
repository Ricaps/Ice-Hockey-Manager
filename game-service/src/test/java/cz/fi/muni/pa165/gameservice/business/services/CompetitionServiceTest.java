package cz.fi.muni.pa165.gameservice.business.services;

import cz.fi.muni.pa165.gameservice.api.exception.ResourceNotFoundException;
import cz.fi.muni.pa165.gameservice.api.exception.ValueIsMissingException;
import cz.fi.muni.pa165.gameservice.persistence.repositories.CompetitionRepository;
import cz.fi.muni.pa165.gameservice.testdata.CompetitionTestData;
import jakarta.validation.ValidationException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class CompetitionServiceTest {

	@Mock
	CompetitionRepository competitionRepository;

	@InjectMocks
	CompetitionService competitionService;

	@Test
	void newCompetition_null_throwException() {
		Assertions.assertThatThrownBy(() -> competitionService.saveCompetition(null))
			.isInstanceOf(ValueIsMissingException.class)
			.hasMessage("New competition cannot be null");
	}

	@Test
	void newCompetition_saveCompetition_success() {
		var competition = CompetitionTestData.getCompetitionEntity();
		Mockito.when(competitionRepository.save(competition)).thenReturn(competition);

		var newCompetition = competitionService.saveCompetition(competition);

		assertThat(newCompetition).isEqualTo(competition);
		Mockito.verify(competitionRepository, Mockito.times(1)).save(competition);
	}

	@Test
	void newCompetition_endBeforeStart_throwException() {
		var competition = CompetitionTestData.getCompetitionEntity();
		competition.setEndAt(competition.getStartAt().minusDays(1));

		assertThatThrownBy(() -> competitionService.saveCompetition(competition))
			.isInstanceOf(ValidationException.class)
			.hasMessage("Start at must be later than end at!");
		Mockito.verify(competitionRepository, Mockito.never()).save(competition);
	}

	@Test
	void newCompetition_endEqualsStart_throwException() {
		var competition = CompetitionTestData.getCompetitionEntity();
		competition.setEndAt(competition.getStartAt());

		assertThatThrownBy(() -> competitionService.saveCompetition(competition))
			.isInstanceOf(ValidationException.class)
			.hasMessage("Start at must be later than end at!");
		Mockito.verify(competitionRepository, Mockito.never()).save(competition);
	}

	@Test
	void newCompetition_nullArgument_throwException() {
		assertThatThrownBy(() -> competitionService.saveCompetition(null)).isInstanceOf(ValueIsMissingException.class)
			.hasMessage("New competition cannot be null");

		Mockito.verify(competitionRepository, Mockito.never()).save(null);
	}

	@Test
	void getCompetition_nullUuid_throwsException() {
		assertThatThrownBy(() -> competitionService.getCompetition(null)).isInstanceOf(ValueIsMissingException.class)
			.hasMessage("You must provide valid UUID");

		Mockito.verify(competitionRepository, Mockito.never()).getCompetitionByGuid(null);
	}

	@Test
	void getCompetition_notExist_throwsException() {
		final var uuid = UUID.randomUUID();

		Mockito.when(competitionRepository.getCompetitionByGuid(uuid)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> competitionService.getCompetition(uuid)).isInstanceOf(ResourceNotFoundException.class)
			.hasMessage("Competition with guid %s was not found!".formatted(uuid));
	}

	@Test
	void getCompetition_exists_returnsCompetition() {
		var competition = CompetitionTestData.getCompetitionEntity();

		Mockito.when(competitionRepository.getCompetitionByGuid(competition.getGuid()))
			.thenReturn(Optional.of(competition));

		var optional = competitionService.getCompetition(competition.getGuid());
		assertThat(optional).isEqualTo(competition);
	}

	@Test
	void updateCompetition_null_throwException() {
		Assertions.assertThatThrownBy(() -> competitionService.updateCompetition(null))
			.isInstanceOf(ValueIsMissingException.class)
			.hasMessage("You must provide competition for update");
	}

	@Test
	void updateCompetition_saveCompetition_success() {
		var competition = CompetitionTestData.getCompetitionEntity();

		Mockito.when(competitionRepository.save(competition)).thenReturn(competition);

		var updatedCompetition = competitionService.updateCompetition(competition);

		assertThat(updatedCompetition).isEqualTo(competition);
		Mockito.verify(competitionRepository, Mockito.times(1)).save(competition);
	}

	@Test
	void updateCompetition_endBeforeStart_throwException() {
		var competition = CompetitionTestData.getCompetitionEntity();
		competition.setEndAt(competition.getStartAt().minusDays(1));

		assertThatThrownBy(() -> competitionService.updateCompetition(competition))
			.isInstanceOf(ValidationException.class)
			.hasMessage("Start at must be later than end at!");
		Mockito.verify(competitionRepository, Mockito.never()).save(competition);
	}

	@Test
	void updateCompetition_endEqualsStart_throwException() {
		var competition = CompetitionTestData.getCompetitionEntity();
		competition.setEndAt(competition.getStartAt());

		assertThatThrownBy(() -> competitionService.updateCompetition(competition))
			.isInstanceOf(ValidationException.class)
			.hasMessage("Start at must be later than end at!");
		Mockito.verify(competitionRepository, Mockito.never()).save(competition);
	}

	@Test
	void updateCompetition_nullArgument_throwException() {
		assertThatThrownBy(() -> competitionService.updateCompetition(null)).isInstanceOf(ValueIsMissingException.class)
			.hasMessage("You must provide competition for update");

		Mockito.verify(competitionRepository, Mockito.never()).save(null);
	}

	@Test
	void updateCompetition_nullUuid_throwsException() {
		var competition = CompetitionTestData.getCompetitionEntity();
		competition.setGuid(null);

		assertThatThrownBy(() -> competitionService.updateCompetition(competition))
			.isInstanceOf(ValueIsMissingException.class)
			.hasMessage("Competition's guid cannot be empty");

		Mockito.verify(competitionRepository, Mockito.never()).getCompetitionByGuid(null);
	}

	@Test
	void isStarted_startDateAfterCurrentDate_returnsFalse() {
		var competition = CompetitionTestData.getCompetitionEntity();
		competition.setStartAt(LocalDate.now().plusDays(1));

		var result = competitionService.isStarted(competition);
		assertThat(result).isFalse();
	}

	@Test
	void isStarted_startDateBeforeCurrentDate_returnsTrue() {
		var competition = CompetitionTestData.getCompetitionEntity();
		competition.setStartAt(LocalDate.now().minusDays(1));

		var result = competitionService.isStarted(competition);
		assertThat(result).isTrue();
	}

	@Test
	void isStarted_startDateEqualsCurrentDate_returnsTrue() {
		var competition = CompetitionTestData.getCompetitionEntity();
		competition.setStartAt(LocalDate.now());

		var result = competitionService.isStarted(competition);
		assertThat(result).isTrue();
	}

}