package cz.fi.muni.pa165.worldlistservice.persistence.entities;

import cz.fi.muni.pa165.dto.worldlistservice.interfaces.Identifiable;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerEntity extends BaseEntity implements Identifiable {

	UUID id;

	@Column
	String firstName;

	@Column
	String lastName;

	@Column
	int overallRating;

	@Column
	long marketValue;

	@ManyToOne
	@JoinColumn
	TeamEntity team;

	@OneToMany(mappedBy = "player", fetch = FetchType.LAZY)
	Set<PlayerCharacteristicEntity> playerCharacteristics = new HashSet<>();

}
