package cz.fi.muni.pa165.gameservice.business.mappers;

import cz.fi.muni.pa165.dto.gameService.ResultViewDto;
import cz.fi.muni.pa165.gameservice.persistence.entities.Result;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ResultMapper {

	ResultMapper INSTANCE = Mappers.getMapper(ResultMapper.class);

	ResultViewDto mapEntityToView(Result result);

}
