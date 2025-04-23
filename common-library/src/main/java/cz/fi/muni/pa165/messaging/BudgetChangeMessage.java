package cz.fi.muni.pa165.messaging;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BudgetChangeMessage {

	@NotNull
	private UUID userId;

	@NotNull
	private int amount;

	@Override
	public String toString() {
		return "BudgetChangeMessage [userId=" + userId + ", amount=" + amount + "]";
	}

}
