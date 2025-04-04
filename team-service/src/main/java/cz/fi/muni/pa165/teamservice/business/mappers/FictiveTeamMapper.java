package cz.fi.muni.pa165.teamservice.business.mappers;

import cz.fi.muni.pa165.dto.teamService.FictiveTeamCreateDTO;
import cz.fi.muni.pa165.dto.teamService.FictiveTeamDTO;
import cz.fi.muni.pa165.dto.teamService.FictiveTeamUpdateDTO;
import cz.fi.muni.pa165.teamservice.persistence.entities.FictiveTeam;
import org.mapstruct.Mapper;

/**
 * @author Jan Martinek
 */
@Mapper(componentModel = "spring")
public abstract class FictiveTeamMapper {

	public abstract FictiveTeamDTO toDto(FictiveTeam team);

	public abstract FictiveTeam toEntity(FictiveTeamCreateDTO dto);

	public abstract FictiveTeam toEntity(FictiveTeamUpdateDTO dto);

}
