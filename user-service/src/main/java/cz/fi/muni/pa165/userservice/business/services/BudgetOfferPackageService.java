package cz.fi.muni.pa165.userservice.business.services;

import cz.fi.muni.pa165.userservice.api.exception.UnauthorizedException;
import cz.fi.muni.pa165.userservice.api.exception.ValidationUtil;
import cz.fi.muni.pa165.userservice.persistence.entities.BudgetOfferPackage;
import cz.fi.muni.pa165.userservice.persistence.repositories.BudgetOfferPackageRepository;
import cz.fi.muni.pa165.userservice.util.AuthUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class BudgetOfferPackageService extends EntityServiceBase<BudgetOfferPackage> {

	private final BudgetOfferPackageRepository budgetOfferPackageRepository;

	private final AuthUtil authUtil;

	@Autowired
	public BudgetOfferPackageService(BudgetOfferPackageRepository budgetOfferPackageRepository, AuthUtil authUtil) {
		this.budgetOfferPackageRepository = budgetOfferPackageRepository;
		this.authUtil = authUtil;
	}

	@Transactional
	public BudgetOfferPackage getBudgetOfferPackageById(UUID packageId) {
		ValidationUtil.requireNotNull(packageId, "You must provide a package packageId, provided package ID is NULL!");

		final var budgetOfferPackage = budgetOfferPackageRepository.findById(packageId);
		return budgetOfferPackage.orElseThrow(() -> new EntityNotFoundException(
				"Budget package with packageId: %s was not found!".formatted(packageId)));
	}

	@Transactional
	public BudgetOfferPackage createBudgetOfferPackage(@Valid BudgetOfferPackage budgetOfferPackage) {
		ValidationUtil.requireNotNull(budgetOfferPackage, "You must provide a valid package provided package is NULL!");
		ValidationUtil.requireNull(budgetOfferPackage.getGuid(), "ID must be null when creating a new package!");
		validateLoggedUserIsAdmin();

		return budgetOfferPackageRepository.save(budgetOfferPackage);
	}

	@Transactional
	public BudgetOfferPackage deactivateBudgetOfferPackage(UUID packageId) {
		ValidationUtil.requireNotNull(packageId, "You must provide a package id, provided package ID is NULL!");
		validateLoggedUserIsAdmin();

		BudgetOfferPackage budgetOfferPackage = budgetOfferPackageRepository.findById(packageId)
			.orElseThrow(
					() -> new EntityNotFoundException("Budget offer package with ID " + packageId + " was not found!"));

		budgetOfferPackage.setIsAvailable(false);
		return budgetOfferPackageRepository.save(budgetOfferPackage);
	}

	@Transactional
	public BudgetOfferPackage activateBudgetOfferPackage(UUID packageId) {
		ValidationUtil.requireNotNull(packageId, "You must provide a package id, provided package ID is NULL!");
		validateLoggedUserIsAdmin();

		BudgetOfferPackage budgetOfferPackage = budgetOfferPackageRepository.findById(packageId)
			.orElseThrow(
					() -> new EntityNotFoundException("Budget offer package with ID " + packageId + " was not found!"));

		budgetOfferPackage.setIsAvailable(true);
		return budgetOfferPackageRepository.save(budgetOfferPackage);
	}

	@Transactional
	public List<BudgetOfferPackage> getAllBudgetOfferPackages() {
		return budgetOfferPackageRepository.findAll();
	}

	@Transactional
	public List<BudgetOfferPackage> getAllAvailableBudgetOfferPackages() {
		return budgetOfferPackageRepository.findAllActiveBudgetOfferPackages();
	}

	private void validateLoggedUserIsAdmin() {
		if (!authUtil.isAuthenticatedUserAdmin())
			throw new UnauthorizedException("To manage packages, you have to be admin!");
	}

}
