package cz.fi.muni.pa165.gameservice.business.facades;

import cz.fi.muni.pa165.dto.gameservice.ArenaCreateDto;
import cz.fi.muni.pa165.dto.gameservice.ArenaViewDto;
import cz.fi.muni.pa165.gameservice.api.exception.EntityUsedException;
import cz.fi.muni.pa165.gameservice.api.exception.ValidationHelper;
import cz.fi.muni.pa165.gameservice.business.mappers.ArenaMapper;
import cz.fi.muni.pa165.gameservice.business.services.ArenaService;
import cz.fi.muni.pa165.gameservice.business.services.MatchService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ArenaFacade {

	private final ArenaService arenaService;

	private final ArenaMapper arenaMapper;

	private final MatchService matchService;

	public ArenaFacade(ArenaService arenaService, ArenaMapper arenaMapper, MatchService matchService) {
		this.arenaService = arenaService;
		this.arenaMapper = arenaMapper;
		this.matchService = matchService;
	}

	public Page<ArenaViewDto> findAllPageable(Pageable pageable) {
		var entities = arenaService.findAllPageable(pageable);

		return arenaMapper.pageableEntitiesToDto(entities);
	}

	public ArenaViewDto updateArena(UUID arenaUid, ArenaCreateDto arenaCreateDto) {
		ValidationHelper.requireNonNull(arenaUid, "Please provide arena UUID for update");
		ValidationHelper.requireNonNull(arenaCreateDto, "Please provide arena data for update");

		var updatedArena = arenaService.updateArena(arenaMapper.arenaCreateToEntity(arenaCreateDto, arenaUid));

		return arenaMapper.arenaEntityToView(updatedArena);
	}

	public void deleteArena(UUID arenaUid) {
		ValidationHelper.requireNonNull(arenaUid, "Please provide arena UUID for deletion");

		var count = matchService.countMatchesAtArena(arenaUid);
		if (count != 0) {
			throw new EntityUsedException("Arena %s is used, so it cannot be deleted!".formatted(arenaUid));
		}

		arenaService.deleteArena(arenaUid);
	}

	public ArenaViewDto createArena(ArenaCreateDto createDto) {
		ValidationHelper.requireNonNull(createDto, "Please provide arena for creation!");

		var newArena = arenaService.createArena(arenaMapper.arenaCreateToEntity(createDto));

		return arenaMapper.arenaEntityToView(newArena);
	}

}
