package cz.fi.muni.pa165.service.gameService.api;

import cz.fi.muni.pa165.dto.gameService.MatchCreateDto;
import cz.fi.muni.pa165.dto.gameService.MatchViewDto;

import java.util.List;
import java.util.UUID;

public interface MatchController {

	List<MatchViewDto> generateMatches(UUID competitionUUID);

	List<MatchViewDto> getMatchesForCompetition(UUID competitionUUID, boolean includeResults);

	MatchViewDto getSingleMatch(UUID matchUUID, boolean includeResults);

	MatchViewDto createMatch(MatchCreateDto newMatch);

}