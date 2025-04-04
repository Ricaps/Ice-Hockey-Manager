package cz.fi.muni.pa165.worldlistservice.persistence.entities;

import cz.fi.muni.pa165.dto.worldlistservice.interfaces.Identifiable;
import cz.fi.muni.pa165.enums.PlayerCharacteristicType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.util.UUID;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerCharacteristicEntity extends BaseEntity implements Identifiable {

	UUID id;

	@Column
	PlayerCharacteristicType type;

	@Column(name = "characteristic_value")
	int value;

	@ManyToOne
	@JoinColumn
	PlayerEntity player;

}
