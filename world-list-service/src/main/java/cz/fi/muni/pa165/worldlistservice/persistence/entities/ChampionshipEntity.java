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
public class ChampionshipEntity extends BaseEntity implements Identifiable {

	UUID id;

	@Column
	String name;

	@ManyToOne
	@JoinColumn
	ChampionshipRegionEntity championshipRegion;

	@OneToMany(mappedBy = "championship", fetch = FetchType.LAZY)
	Set<TeamEntity> championshipTeams = new HashSet<>();

}
