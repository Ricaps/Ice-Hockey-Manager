package cz.fi.muni.pa165.worldlistservice.business.mappers;

import cz.fi.muni.pa165.dto.worldlistservice.championship.create.ChampionshipCreateDto;
import cz.fi.muni.pa165.dto.worldlistservice.championship.detail.ChampionshipDetailDto;
import cz.fi.muni.pa165.dto.worldlistservice.championship.list.ChampionshipListDto;
import cz.fi.muni.pa165.dto.worldlistservice.championship.update.ChampionshipUpdateDto;
import cz.fi.muni.pa165.worldlistservice.persistence.entities.ChampionshipEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = { ChampionshipRegionMapper.class },
		unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ChampionshipMapper extends
		GenericMapper<ChampionshipDetailDto, ChampionshipListDto, ChampionshipCreateDto, ChampionshipUpdateDto, ChampionshipEntity> {

}
