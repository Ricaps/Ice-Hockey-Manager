package cz.fi.muni.pa165.dto.userService;

import jakarta.validation.constraints.NotNull;
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
public class PaymentUpdateCreateDto {

	private UUID guid;

	@NotNull
	private UUID userId;

	@NotNull
	private UUID budgetOfferPackageId;

	private LocalDateTime createdAt;

	private Boolean paid;

}
