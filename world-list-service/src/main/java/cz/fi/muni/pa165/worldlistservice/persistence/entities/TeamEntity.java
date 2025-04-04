package cz.fi.muni.pa165.worldlistservice.persistence.entities;

import cz.fi.muni.pa165.dto.worldlistservice.interfaces.Identifiable;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;
import java.util.UUID;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamEntity extends BaseEntity implements Identifiable {

	UUID id;

	@Column
	String name;

	@ManyToOne
	@JoinColumn
	ChampionshipEntity championship;

	@OneToMany(mappedBy = "team", fetch = FetchType.LAZY)
	Set<PlayerEntity> teamPlayers;

}
