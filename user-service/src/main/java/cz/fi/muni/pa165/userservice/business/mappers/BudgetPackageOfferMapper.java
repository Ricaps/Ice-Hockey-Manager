package cz.fi.muni.pa165.userservice.business.mappers;

import cz.fi.muni.pa165.dto.userService.BudgetOfferPackageDto;
import cz.fi.muni.pa165.userservice.persistence.entities.BudgetOfferPackage;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BudgetPackageOfferMapper {

	BudgetOfferPackageDto budgetOfferPackagetoBudgetOfferPackageDto(BudgetOfferPackage budgetOfferPackage);

	BudgetOfferPackage budgetOfferPackageDtoToBudgetOfferPackage(BudgetOfferPackageDto budgetOfferPackage);

}
