package cz.fi.muni.pa165.userservice.business.mappers;

import cz.fi.muni.pa165.dto.userservice.PaymentUpdateCreateDto;
import cz.fi.muni.pa165.dto.userservice.PaymentViewDto;
import cz.fi.muni.pa165.userservice.persistence.entities.BudgetOfferPackage;
import cz.fi.muni.pa165.userservice.persistence.entities.Payment;
import cz.fi.muni.pa165.userservice.persistence.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.UUID;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PaymentMapper {

	PaymentViewDto paymentToPaymentUserViewDto(Payment payment);

	@Mapping(source = "userId", target = "user", qualifiedByName = "mapUuidToUser")
	@Mapping(source = "budgetOfferPackageId", target = "budgetOfferPackage",
			qualifiedByName = "mapUuidToBudgetOfferPackage")
	Payment paymentUpdateCreateDtoToPayment(PaymentUpdateCreateDto paymentUpdateCreateDto);

	@Mapping(source = "user.guid", target = "userId")
	@Mapping(source = "budgetOfferPackage.guid", target = "budgetOfferPackageId")
	PaymentUpdateCreateDto paymentToPaymentUpdateCreateDto(Payment payment);

	@Named("mapUuidToUser")
	default User mapUuidToUser(UUID userId) {
		if (userId == null) {
			return null;
		}

		User user = new User();
		user.setGuid(userId);
		return user;
	}

	@Named("mapUuidToBudgetOfferPackage")
	default BudgetOfferPackage mapUuidToBudgetOfferPackage(UUID packageId) {
		if (packageId == null) {
			return null;
		}

		BudgetOfferPackage pack = new BudgetOfferPackage();
		pack.setGuid(packageId);
		return pack;
	}

}
