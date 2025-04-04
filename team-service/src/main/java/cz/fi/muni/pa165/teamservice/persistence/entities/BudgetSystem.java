package cz.fi.muni.pa165.teamservice.persistence.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "budget_system")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetSystem {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID guid;

	@Column(nullable = false)
	private double amount;

}
