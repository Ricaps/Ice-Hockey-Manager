package cz.fi.muni.pa165.teamservice.business.mappers;

import cz.fi.muni.pa165.dto.teamService.TeamCharacteristicCreateDTO;
import cz.fi.muni.pa165.dto.teamService.TeamCharacteristicDTO;
import cz.fi.muni.pa165.dto.teamService.TeamCharacteristicUpdateDTO;
import cz.fi.muni.pa165.teamservice.persistence.entities.TeamCharacteristic;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * @author Jan Martinek
 */
@Mapper(componentModel = "spring")
public interface TeamCharacteristicMapper {

	TeamCharacteristicDTO toDto(TeamCharacteristic entity);

	@Mapping(target = "guid", ignore = true)
	TeamCharacteristic toEntity(TeamCharacteristicCreateDTO createDTO);

	TeamCharacteristic toEntity(TeamCharacteristicUpdateDTO updateDTO);

}
