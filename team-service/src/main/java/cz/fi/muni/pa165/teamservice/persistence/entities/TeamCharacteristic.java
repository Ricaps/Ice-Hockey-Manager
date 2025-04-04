package cz.fi.muni.pa165.teamservice.persistence.entities;

import cz.fi.muni.pa165.enums.TeamCharacteristicType;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * @author Jan Martinek
 */

@Entity
@Table(name = "team_characteristic")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamCharacteristic {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID guid;

	@Column(name = "team_id", nullable = false)
	private UUID teamId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TeamCharacteristicType characteristicType;

	@Column(nullable = false)
	private int characteristicValue;

}
