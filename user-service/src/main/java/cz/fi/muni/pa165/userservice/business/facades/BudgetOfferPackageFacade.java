package cz.fi.muni.pa165.userservice.business.facades;

import cz.fi.muni.pa165.dto.userservice.BudgetOfferPackageDto;
import cz.fi.muni.pa165.userservice.business.mappers.BudgetPackageOfferMapper;
import cz.fi.muni.pa165.userservice.business.services.BudgetOfferPackageService;
import cz.fi.muni.pa165.userservice.persistence.entities.BudgetOfferPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class BudgetOfferPackageFacade {

	private final BudgetOfferPackageService budgetOfferPackageService;

	private final BudgetPackageOfferMapper mapper;

	@Autowired
	public BudgetOfferPackageFacade(BudgetPackageOfferMapper mapper,
			BudgetOfferPackageService budgetOfferPackageService) {
		this.mapper = mapper;
		this.budgetOfferPackageService = budgetOfferPackageService;
	}

	public BudgetOfferPackageDto getBudgetPackageOfferById(UUID id) {
		return mapper
			.budgetOfferPackagetoBudgetOfferPackageDto(budgetOfferPackageService.getBudgetOfferPackageById(id));
	}

	public BudgetOfferPackageDto create(BudgetOfferPackageDto dto) {
		final BudgetOfferPackage budgetPackage = mapper.budgetOfferPackageDtoToBudgetOfferPackage(dto);
		return mapper.budgetOfferPackagetoBudgetOfferPackageDto(
				budgetOfferPackageService.createBudgetOfferPackage(budgetPackage));
	}

	public BudgetOfferPackageDto deactivateBudgetOfferPackage(UUID packageId) {
		return mapper.budgetOfferPackagetoBudgetOfferPackageDto(
				budgetOfferPackageService.deactivateBudgetOfferPackage(packageId));
	}

	public BudgetOfferPackageDto activateBudgetOfferPackage(UUID packageId) {
		return mapper
			.budgetOfferPackagetoBudgetOfferPackageDto(budgetOfferPackageService.activateBudgetOfferPackage(packageId));
	}

	public List<BudgetOfferPackageDto> getAllBudgetOfferPackages() {
		final List<BudgetOfferPackage> packages = budgetOfferPackageService.getAllBudgetOfferPackages();
		return packages.stream().map(mapper::budgetOfferPackagetoBudgetOfferPackageDto).toList();
	}

	public List<BudgetOfferPackageDto> getAllAvailableBudgetOfferPackages() {
		final List<BudgetOfferPackage> packages = budgetOfferPackageService.getAllAvailableBudgetOfferPackages();
		return packages.stream().map(mapper::budgetOfferPackagetoBudgetOfferPackageDto).toList();
	}

}
