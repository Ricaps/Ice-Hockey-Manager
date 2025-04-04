package cz.fi.muni.pa165.gameservice.business.services;

import cz.fi.muni.pa165.gameservice.api.exception.ResourceAlreadyExists;
import cz.fi.muni.pa165.gameservice.api.exception.ValueIsMissingException;
import cz.fi.muni.pa165.gameservice.persistence.entities.CompetitionHasTeam;
import cz.fi.muni.pa165.gameservice.persistence.repositories.TeamRepository;
import cz.fi.muni.pa165.gameservice.testdata.CompetitionTestData;
import jakarta.validation.ConstraintViolationException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

	@Mock
	TeamRepository teamRepository;

	@InjectMocks
	TeamService teamService;

	@Test
	void assignTeam_nullEntity_throwsException() {
		Assertions.assertThatThrownBy(() -> teamService.assignTeamToCompetition(null))
			.isInstanceOf(ValueIsMissingException.class)
			.hasMessage("Competition has team entity must be provided");

		Mockito.verify(teamRepository, Mockito.never()).save(Mockito.any());
	}

	@Test
	void assignTeam_alreadyAssigned_throwsException() {
		var competition = CompetitionTestData.getCompetitionEntity();
		var teamUUID = UUID.randomUUID();
		var competitionHasTeam = CompetitionHasTeam.builder().teamUid(teamUUID).competition(competition).build();

		competition.setTeams(Set.of(competitionHasTeam));
		Assertions.assertThatThrownBy(() -> teamService.assignTeamToCompetition(competitionHasTeam))
			.isInstanceOf(ResourceAlreadyExists.class)
			.hasMessage("Team is already assigned to the competition");

		Mockito.verify(teamRepository, Mockito.never()).save(Mockito.any());
	}

	@Test
	void assignTeam_allGood_success() {
		var competition = CompetitionTestData.getCompetitionEntity();
		var teamUUID = UUID.randomUUID();
		var competitionHasTeam = CompetitionHasTeam.builder().teamUid(teamUUID).competition(competition).build();

		teamService.assignTeamToCompetition(competitionHasTeam);

		Mockito.verify(teamRepository, Mockito.times(1)).save(competitionHasTeam);
	}

	@Test
	void assignTeam_teamUUIDNull_throwsException() {
		var competition = CompetitionTestData.getCompetitionEntity();
		var competitionHasTeam = CompetitionHasTeam.builder().teamUid(null).competition(competition).build();

		Assertions.assertThatThrownBy(() -> teamService.assignTeamToCompetition(competitionHasTeam))
			.isInstanceOf(ConstraintViolationException.class);

		Mockito.verify(teamRepository, Mockito.never()).save(Mockito.any());
	}

	@Test
	void assignTeam_CompetitionDNull_throwsException() {
		var teamUUID = UUID.randomUUID();
		var competitionHasTeam = CompetitionHasTeam.builder().teamUid(teamUUID).competition(null).build();

		Assertions.assertThatThrownBy(() -> teamService.assignTeamToCompetition(competitionHasTeam))
			.isInstanceOf(ConstraintViolationException.class);

		Mockito.verify(teamRepository, Mockito.never()).save(Mockito.any());
	}

}