package cz.fi.muni.pa165.teamservice.persistence.entities;

import cz.fi.muni.pa165.enums.TeamCharacteristicType;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "fictive_team")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

/**
 * @author Jan Martinek
 */
public class FictiveTeam {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID guid;

	@Column(nullable = false)
	private String name;

	@ElementCollection
	@CollectionTable(name = "team_players", joinColumns = @JoinColumn(name = "team_id"))
	@Column(name = "player_id")
	private List<UUID> playerIDs;

	@Enumerated(EnumType.STRING)
	@Column(name = "characteristic_type")
	private TeamCharacteristicType characteristicType;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "budget_system_id", referencedColumnName = "guid")
	private BudgetSystem budgetSystem;

}
