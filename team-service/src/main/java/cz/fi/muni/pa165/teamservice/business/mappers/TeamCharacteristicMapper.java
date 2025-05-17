package cz.fi.muni.pa165.teamservice.business.mappers;

import cz.fi.muni.pa165.dto.teamservice.TeamCharacteristicCreateDTO;
import cz.fi.muni.pa165.dto.teamservice.TeamCharacteristicDTO;
import cz.fi.muni.pa165.dto.teamservice.TeamCharacteristicUpdateDTO;
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
