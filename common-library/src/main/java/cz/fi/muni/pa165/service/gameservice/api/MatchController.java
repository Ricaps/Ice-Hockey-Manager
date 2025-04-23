package cz.fi.muni.pa165.service.gameservice.api;

import cz.fi.muni.pa165.dto.gameservice.MatchCreateDto;
import cz.fi.muni.pa165.dto.gameservice.MatchViewDto;

import java.util.List;
import java.util.UUID;

public interface MatchController {

	List<MatchViewDto> generateMatches(UUID competitionUUID);

	List<MatchViewDto> getMatchesForCompetition(UUID competitionUUID, boolean includeResults);

	MatchViewDto getSingleMatch(UUID matchUUID, boolean includeResults);

	MatchViewDto createMatch(MatchCreateDto newMatch);

}