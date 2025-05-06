package cz.fi.muni.pa165.service.gameservice.api;

import cz.fi.muni.pa165.dto.gameservice.ArenaCreateDto;
import cz.fi.muni.pa165.dto.gameservice.ArenaViewDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ArenaController {

	Page<ArenaViewDto> findAll(Pageable pageable);

	ArenaViewDto createArena(ArenaCreateDto arenaCreateDto);

	ArenaViewDto updateArena(UUID arenaUid, ArenaCreateDto arenaCreateDto);

	void deleteArena(UUID arenaUid);

}
