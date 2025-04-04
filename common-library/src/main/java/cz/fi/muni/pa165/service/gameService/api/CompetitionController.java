package cz.fi.muni.pa165.service.gameService.api;

import cz.fi.muni.pa165.dto.gameService.AssignTeamDto;
import cz.fi.muni.pa165.dto.gameService.CompetitionCreateDto;
import cz.fi.muni.pa165.dto.gameService.CompetitionViewDto;

import java.util.UUID;

public interface CompetitionController {

	CompetitionViewDto addCompetition(CompetitionCreateDto body);

	CompetitionViewDto getCompetition(UUID uuid);

	CompetitionViewDto updateCompetition(UUID uuid, CompetitionCreateDto competitionCreateDto);

	void assignTeam(UUID competitionUUID, AssignTeamDto assignTeamDto);

}
