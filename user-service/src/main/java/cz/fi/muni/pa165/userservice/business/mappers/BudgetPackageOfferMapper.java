package cz.fi.muni.pa165.userservice.business.mappers;

import cz.fi.muni.pa165.dto.userservice.BudgetOfferPackageDto;
import cz.fi.muni.pa165.userservice.persistence.entities.BudgetOfferPackage;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BudgetPackageOfferMapper {

	BudgetOfferPackageDto budgetOfferPackagetoBudgetOfferPackageDto(BudgetOfferPackage budgetOfferPackage);

	BudgetOfferPackage budgetOfferPackageDtoToBudgetOfferPackage(BudgetOfferPackageDto budgetOfferPackage);

}
