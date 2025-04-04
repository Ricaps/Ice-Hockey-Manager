package cz.fi.muni.pa165.dto.worldlistservice.playercharacteristic;

import cz.fi.muni.pa165.dto.worldlistservice.BaseDto;
import cz.fi.muni.pa165.dto.worldlistservice.interfaces.Identifiable;
import cz.fi.muni.pa165.enums.PlayerCharacteristicType;
import cz.fi.muni.pa165.enums.validators.ValidEnum;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerCharacteristicDto extends BaseDto implements Identifiable {

	UUID id;

	@ValidEnum(enumClass = PlayerCharacteristicType.class, message = "Invalid player characteristic type")
	@NotNull(message = "Player characteristic has to be defined")
	PlayerCharacteristicType type;

	@Min(value = 1, message = "Value of player characteristic has to be greater than 0")
	int value;

}
