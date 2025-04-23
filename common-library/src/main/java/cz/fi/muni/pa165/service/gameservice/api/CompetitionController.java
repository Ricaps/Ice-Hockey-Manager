package cz.fi.muni.pa165.service.gameservice.api;

import cz.fi.muni.pa165.dto.gameservice.AssignTeamDto;
import cz.fi.muni.pa165.dto.gameservice.CompetitionCreateDto;
import cz.fi.muni.pa165.dto.gameservice.CompetitionViewDto;

import java.util.UUID;

public interface CompetitionController {

	CompetitionViewDto addCompetition(CompetitionCreateDto body);

	CompetitionViewDto getCompetition(UUID uuid);

	CompetitionViewDto updateCompetition(UUID uuid, CompetitionCreateDto competitionCreateDto);

	void assignTeam(UUID competitionUUID, AssignTeamDto assignTeamDto);

}
