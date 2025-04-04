package cz.fi.muni.pa165.gameservice.business.facades;

import cz.fi.muni.pa165.dto.gameService.AssignTeamDto;
import cz.fi.muni.pa165.dto.gameService.CompetitionCreateDto;
import cz.fi.muni.pa165.dto.gameService.CompetitionViewDto;
import cz.fi.muni.pa165.gameservice.api.exception.ActionForbidden;
import cz.fi.muni.pa165.gameservice.api.exception.ValidationHelper;
import cz.fi.muni.pa165.gameservice.business.mappers.CompetitionMapper;
import cz.fi.muni.pa165.gameservice.business.services.CompetitionService;
import cz.fi.muni.pa165.gameservice.business.services.TeamService;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CompetitionFacade {

	private final CompetitionService competitionService;

	private final TeamService teamService;

	private final CompetitionMapper competitionMapper;

	@Autowired
	public CompetitionFacade(CompetitionService competitionService, TeamService teamService,
			CompetitionMapper competitionMapper) {
		this.competitionService = competitionService;
		this.teamService = teamService;
		this.competitionMapper = competitionMapper;
	}

	public CompetitionViewDto addCompetition(@NotNull CompetitionCreateDto competitionCreate) {
		ValidationHelper.requireNonNull(competitionCreate, "Please provide non null competition");

		final var newCompetition = competitionService
			.saveCompetition(competitionMapper.competitionCreateDtoToCompetition(competitionCreate));
		return competitionMapper.competitionToCompetitionViewDto(newCompetition);
	}

	public CompetitionViewDto getCompetition(@NotNull UUID uuid) {
		ValidationHelper.requireNonNull(uuid, "Please provide UUID of competition you want to fetch");

		final var competition = competitionService.getCompetition(uuid);
		return competitionMapper.competitionToCompetitionViewDto(competition);
	}

	public CompetitionViewDto updateCompetition(@NotNull UUID uuid,
			@NotNull CompetitionCreateDto competitionCreateDto) {
		ValidationHelper.requireNonNull(uuid, "Please provide UUID of competition you want to update");
		ValidationHelper.requireNonNull(competitionCreateDto, "Please provide data for competition update");

		final var updatedCompetition = competitionService
			.updateCompetition(competitionMapper.competitionCreateDtoToCompetition(competitionCreateDto, uuid));
		return competitionMapper.competitionToCompetitionViewDto(updatedCompetition);
	}

	/**
	 * Assigns team to the desired competition
	 * @param competitionUUID assigns team to the competition you want to assign the team
	 * @param assignTeamDto DTO with team to assign
	 */
	@Transactional
	public void assignTeam(UUID competitionUUID, AssignTeamDto assignTeamDto) {
		ValidationHelper.requireNonNull(competitionUUID, "Competition UUID cannot be null");
		ValidationHelper.requireNonNull(assignTeamDto, "Team UUID cannot be null");

		final var competition = competitionService.getCompetition(competitionUUID);
		final var isCompetitionRunning = competitionService.isStarted(competition);
		if (isCompetitionRunning) {
			throw new ActionForbidden("You cannot assign team to the competition, that is already running");
		}
		teamService.assignTeamToCompetition(
				competitionMapper.competitionTeamsDtoToCompetitionHasTeam(assignTeamDto, competition));
	}

}
