package cz.fi.muni.pa165.gameservice.business.mappers;

import cz.fi.muni.pa165.dto.gameservice.ResultViewDto;
import cz.fi.muni.pa165.gameservice.persistence.entities.Result;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ResultMapper {

	ResultViewDto mapEntityToView(Result result);

}
