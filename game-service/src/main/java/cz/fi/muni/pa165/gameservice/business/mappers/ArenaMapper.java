package cz.fi.muni.pa165.gameservice.business.mappers;

import cz.fi.muni.pa165.dto.gameservice.ArenaCreateDto;
import cz.fi.muni.pa165.dto.gameservice.ArenaViewDto;
import cz.fi.muni.pa165.gameservice.persistence.entities.Arena;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ArenaMapper {

	Arena arenaCreateToEntity(ArenaCreateDto arenaCreateDto);

	Arena arenaCreateToEntity(ArenaCreateDto arenaCreateDto, UUID guid);

	ArenaViewDto arenaEntityToView(Arena arena);

	List<ArenaViewDto> arenaEntitiesToArenaDtos(List<Arena> entities);

	default Page<ArenaViewDto> pageableEntitiesToDto(Page<Arena> arenaPage) {
		var arenas = arenaEntitiesToArenaDtos(arenaPage.getContent());

		return new PageImpl<>(arenas, arenaPage.getPageable(), arenaPage.getTotalElements());
	}

}
