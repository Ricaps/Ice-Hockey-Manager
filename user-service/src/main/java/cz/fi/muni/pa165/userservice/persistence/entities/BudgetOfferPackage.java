package cz.fi.muni.pa165.userservice.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.Value;

import java.util.UUID;

@Entity
@Table(name = "budget_offer_package")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class BudgetOfferPackage {

	@Id
	@NotNull
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID guid;

	@Column(name = "price_dollars", nullable = false)
	@Min(value = 0, message = "Value must be positive number or zero.")
	private Integer priceDollars;

	@Column(name = "budget_increase", nullable = false)
	@Min(value = 1, message = "Value must be positive number bigger then 0.")
	private Integer budgetIncrease;

	@Column(name = "description")
	private String description;

	@Column(name = "is_available", nullable = false)
	private Boolean isAvailable;

}
