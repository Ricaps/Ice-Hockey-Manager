package cz.fi.muni.pa165.teamservice.business.mappers;

import cz.fi.muni.pa165.dto.teamservice.FictiveTeamCreateDTO;
import cz.fi.muni.pa165.dto.teamservice.FictiveTeamDTO;
import cz.fi.muni.pa165.dto.teamservice.FictiveTeamUpdateDTO;
import cz.fi.muni.pa165.teamservice.persistence.entities.FictiveTeam;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * @author Jan Martinek
 */
@Mapper(componentModel = "spring")
public interface FictiveTeamMapper {

	@Mapping(target = "playerIds", source = "playerIDs")
	@Mapping(target = "characteristicTypes", expression = "java(team.getTeamCharacteristics().stream()"
			+ ".map(c -> c.getGuid())" + ".collect(java.util.stream.Collectors.toSet()))")
	FictiveTeamDTO toDto(FictiveTeam team);

	@Mapping(target = "playerIDs", source = "playerIds")
	FictiveTeam toEntity(FictiveTeamCreateDTO dto);

	@Mapping(target = "playerIDs", source = "playerIds")
	FictiveTeam toEntity(FictiveTeamUpdateDTO dto);

}
