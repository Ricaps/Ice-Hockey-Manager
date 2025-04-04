package cz.fi.muni.pa165.service.gameService.api;

import cz.fi.muni.pa165.dto.gameService.MatchViewDto;

import java.util.List;
import java.util.UUID;

public interface MatchController {

	List<MatchViewDto> generateMatches(UUID competitionUUID);

}
