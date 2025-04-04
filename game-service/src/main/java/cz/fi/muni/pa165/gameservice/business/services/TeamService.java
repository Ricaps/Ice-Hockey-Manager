package cz.fi.muni.pa165.gameservice.business.services;

import cz.fi.muni.pa165.gameservice.api.exception.ResourceAlreadyExists;
import cz.fi.muni.pa165.gameservice.api.exception.ValidationHelper;
import cz.fi.muni.pa165.gameservice.persistence.entities.CompetitionHasTeam;
import cz.fi.muni.pa165.gameservice.persistence.repositories.TeamRepository;
import org.springframework.stereotype.Service;

@Service
public class TeamService {

	private final TeamRepository teamRepository;

	public TeamService(TeamRepository teamRepository) {
		this.teamRepository = teamRepository;
	}

	public void assignTeamToCompetition(CompetitionHasTeam competitionHasTeam) {
		ValidationHelper.requireNonNull(competitionHasTeam, "Competition has team entity must be provided");
		ValidationHelper.validateConstraints(competitionHasTeam);

		final var assignedTeams = competitionHasTeam.getCompetition().getTeams();
		if (assignedTeams != null && assignedTeams.contains(competitionHasTeam)) {
			throw new ResourceAlreadyExists("Team is already assigned to the competition");
		}
		teamRepository.save(competitionHasTeam);
	}

}
