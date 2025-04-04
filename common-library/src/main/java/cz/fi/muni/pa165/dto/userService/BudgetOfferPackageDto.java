package cz.fi.muni.pa165.dto.userService;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BudgetOfferPackageDto {

	private UUID guid;

	@Min(value = 0)
	private Integer priceDollars;

	@Min(value = 1)
	private Integer budgetIncrease;

	private String description;

	@NotNull
	private Boolean isAvailable;

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof BudgetOfferPackageDto that))
			return false;

		return Objects.equals(guid, that.guid) && Objects.equals(priceDollars, that.priceDollars)
				&& Objects.equals(budgetIncrease, that.budgetIncrease) && Objects.equals(description, that.description);
	}

	@Override
	public int hashCode() {
		return Objects.hash(guid, priceDollars, budgetIncrease, description, isAvailable);
	}

}
