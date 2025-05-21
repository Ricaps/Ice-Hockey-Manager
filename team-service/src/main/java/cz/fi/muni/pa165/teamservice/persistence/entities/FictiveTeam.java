package cz.fi.muni.pa165.teamservice.persistence.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.*;

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
	private List<UUID> playerIDs = new ArrayList<>();

	@OneToMany(mappedBy = "fictiveTeam", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<TeamCharacteristic> teamCharacteristics = new HashSet<>();

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "budget_system_id", referencedColumnName = "guid")
	private BudgetSystem budgetSystem;

	@Column(name = "owner_id", nullable = false)
	private UUID ownerId;

}
