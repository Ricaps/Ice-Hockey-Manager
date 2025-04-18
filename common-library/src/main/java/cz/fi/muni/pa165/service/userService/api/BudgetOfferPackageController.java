package cz.fi.muni.pa165.service.userService.api;

import cz.fi.muni.pa165.dto.userService.BudgetOfferPackageDto;

import java.util.List;
import java.util.UUID;

public interface BudgetOfferPackageController {

	BudgetOfferPackageDto getBudgetOfferPackageById(UUID packageId);

	BudgetOfferPackageDto createBudgetOfferPackage(BudgetOfferPackageDto budgetOfferPackageDto);

	BudgetOfferPackageDto deactivateBudgetOfferPackage(UUID packageId);

	BudgetOfferPackageDto activateBudgetOfferPackage(UUID packageId);

	List<BudgetOfferPackageDto> getAllBudgetOfferPackages();

	List<BudgetOfferPackageDto> getAllAvailableBudgetOfferPackages();

}
