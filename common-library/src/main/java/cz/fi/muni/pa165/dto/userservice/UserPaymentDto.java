package cz.fi.muni.pa165.dto.userservice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserPaymentDto {

	private UUID guid;

	private PaymentBudgetPackageOfferDto budgetOfferPackage;

	private LocalDateTime createdAt;

	private Boolean paid;

}
