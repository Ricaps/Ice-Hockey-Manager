package cz.fi.muni.pa165.dto.userservice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentBudgetPackageOfferDto {

	private UUID guid;

	private Integer priceDollars;

	private Integer budgetIncrease;

	private String description;

	private Boolean isAvailable;

}
