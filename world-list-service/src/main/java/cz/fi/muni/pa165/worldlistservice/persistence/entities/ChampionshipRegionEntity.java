package cz.fi.muni.pa165.worldlistservice.persistence.entities;

import cz.fi.muni.pa165.dto.worldlistservice.interfaces.Identifiable;
import cz.fi.muni.pa165.enums.ChampionshipRegionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.util.Set;
import java.util.UUID;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChampionshipRegionEntity extends BaseEntity implements Identifiable {

	UUID id;

	@Column
	String name;

	@Column
	ChampionshipRegionType type;

	@OneToMany(mappedBy = "championshipRegion", fetch = FetchType.LAZY)
	Set<ChampionshipEntity> regionChampionships;

}
