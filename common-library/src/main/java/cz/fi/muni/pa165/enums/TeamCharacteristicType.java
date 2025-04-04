package cz.fi.muni.pa165.enums;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author Jan Martinek
 */
@Schema(description = "Team characteristic types")
public enum TeamCharacteristicType {

	@Schema(description = "Team collaboration ability")
	COLLABORATION, @Schema(description = "Team speed attribute")
	SPEED, @Schema(description = "Team stamina level")
	STAMINA, @Schema(description = "Physical strength")
	STRENGTH, @Schema(description = "Shooting accuracy")
	SHOOTING, @Schema(description = "Passing precision")
	PASSING, @Schema(description = "Puck/ball control")
	PUCK_CONTROL, @Schema(description = "Defensive capability")
	DEFENSE, @Schema(description = "Checking/blocking skill")
	CHECKING, @Schema(description = "Goalie-specific abilities")
	GOALIE_SKILL, @Schema(description = "Aggression level")
	AGGRESSION

}
