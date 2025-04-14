package cz.fi.muni.pa165.worldlistservice.business.mappers;

import cz.fi.muni.pa165.dto.worldlistservice.championshipregion.ChampionshipRegionDto;
import cz.fi.muni.pa165.dto.worldlistservice.championshipregion.create.ChampionshipRegionCreateDto;
import cz.fi.muni.pa165.worldlistservice.persistence.entities.ChampionshipRegionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ChampionshipRegionMapper extends
		GenericMapper<ChampionshipRegionDto, ChampionshipRegionDto, ChampionshipRegionCreateDto, ChampionshipRegionDto, ChampionshipRegionEntity> {

}
