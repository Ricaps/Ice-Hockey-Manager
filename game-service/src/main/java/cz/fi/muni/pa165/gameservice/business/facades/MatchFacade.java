package cz.fi.muni.pa165.gameservice.business.facades;

import cz.fi.muni.pa165.dto.gameservice.MatchCreateDto;
import cz.fi.muni.pa165.dto.gameservice.MatchViewDto;
import cz.fi.muni.pa165.gameservice.api.exception.ResourceNotFoundException;
import cz.fi.muni.pa165.gameservice.api.exception.ValidationHelper;
import cz.fi.muni.pa165.gameservice.business.mappers.MatchMapper;
import cz.fi.muni.pa165.gameservice.business.services.ArenaService;
import cz.fi.muni.pa165.gameservice.business.services.CompetitionService;
import cz.fi.muni.pa165.gameservice.business.services.MatchService;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class MatchFacade {

	private final MatchService matchService;

	private final MatchMapper matchMapper;

	private final CompetitionService competitionService;

	private final ArenaService arenaService;

	@Autowired
	public MatchFacade(MatchService matchService, MatchMapper matchMapper, CompetitionService competitionService,
			ArenaService arenaService) {
		this.matchService = matchService;
		this.matchMapper = matchMapper;
		this.competitionService = competitionService;
		this.arenaService = arenaService;
	}

	/**
	 * Generates matches for the given competition Matches are generated for all assigned
	 * teams within the defined timeframe (start and end date of the competition) Matches
	 * cannot be generated for already started competitions
	 * @param competitionUUID UUID of the competition to generate matches
	 */
	@Transactional
	public List<MatchViewDto> generateMatches(@NotNull UUID competitionUUID) {
		ValidationHelper.requireNonNull(competitionUUID, "Please provide UUID of the competition");

		final var competition = this.competitionService.getCompetition(competitionUUID);
		final var matches = matchService.generateMatches(competition);
		return matchMapper.listEntitiesToListViews(matchService.saveMatches(matches));
	}

	@Transactional
	public MatchViewDto createMatch(@NotNull MatchCreateDto newMatch) {
		ValidationHelper.requireNonNull(newMatch, "You must provide Match Dto to create new match");

		final var arena = arenaService.getReferenceIfExists(newMatch.getArenaUid());
		final var matchEntity = matchMapper.matchCreateDtoToEntity(newMatch, arena);
		final var savedMatch = matchService.saveMatch(matchEntity);

		return matchMapper.matchEntityToMatchViewDto(savedMatch);
	}

	public List<MatchViewDto> getMatchesOfCompetition(@NotNull UUID competitionUUID, boolean includeResults) {
		ValidationHelper.requireNonNull(competitionUUID, "Please provide UUID of competition");

		if (!competitionService.exists(competitionUUID)) {
			throw new ResourceNotFoundException("Competition with guid %s was not found!".formatted(competitionUUID));
		}

		var matches = matchService.getMatchesOfCompetition(competitionUUID);

		if (includeResults) {
			return matchMapper.listEntitiesToListViews(matches);
		}
		return matchMapper.listEntitiesToListViewsIgnoreResult(matches);
	}

	public MatchViewDto getMatch(@NotNull UUID matchUUID, boolean includeResults) {
		ValidationHelper.requireNonNull(matchUUID, "Please provide UUID of desired match");

		var match = matchService.getMatch(matchUUID);
		if (includeResults) {
			return matchMapper.matchEntityToMatchViewDto(match);
		}

		return matchMapper.matchEntityToMatchViewDtoIgnoreResult(match);
	}

}
