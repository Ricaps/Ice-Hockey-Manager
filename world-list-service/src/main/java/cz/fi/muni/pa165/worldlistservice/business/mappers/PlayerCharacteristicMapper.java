package cz.fi.muni.pa165.worldlistservice.business.mappers;

import cz.fi.muni.pa165.dto.worldlistservice.playercharacteristic.PlayerCharacteristicDto;
import cz.fi.muni.pa165.dto.worldlistservice.playercharacteristic.create.PlayerCharacteristicCreateDto;
import cz.fi.muni.pa165.worldlistservice.persistence.entities.PlayerCharacteristicEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PlayerCharacteristicMapper extends
		GenericMapper<PlayerCharacteristicDto, PlayerCharacteristicDto, PlayerCharacteristicCreateDto, PlayerCharacteristicDto, PlayerCharacteristicEntity> {

}
