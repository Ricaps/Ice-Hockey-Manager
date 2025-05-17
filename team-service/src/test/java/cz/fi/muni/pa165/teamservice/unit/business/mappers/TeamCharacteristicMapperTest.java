package cz.fi.muni.pa165.teamservice.unit.business.mappers;

import cz.fi.muni.pa165.dto.teamservice.TeamCharacteristicCreateDTO;
import cz.fi.muni.pa165.dto.teamservice.TeamCharacteristicDTO;
import cz.fi.muni.pa165.dto.teamservice.TeamCharacteristicUpdateDTO;
import cz.fi.muni.pa165.enums.TeamCharacteristicType;
import cz.fi.muni.pa165.teamservice.business.mappers.TeamCharacteristicMapper;
import cz.fi.muni.pa165.teamservice.persistence.entities.TeamCharacteristic;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Jan Martinek
 */
class TeamCharacteristicMapperTest {

	private final TeamCharacteristicMapper mapper = Mappers.getMapper(TeamCharacteristicMapper.class);

	@Test
	void toDto() {
		TeamCharacteristic entity = new TeamCharacteristic();
		entity.setGuid(UUID.randomUUID());
		entity.setTeamId(UUID.randomUUID());
		entity.setCharacteristicType(TeamCharacteristicType.STRENGTH);
		entity.setCharacteristicValue(85);

		TeamCharacteristicDTO dto = mapper.toDto(entity);

		assertThat(dto.getGuid()).isEqualTo(entity.getGuid());
		assertThat(dto.getTeamId()).isEqualTo(entity.getTeamId());
		assertThat(dto.getCharacteristicType()).isEqualTo(entity.getCharacteristicType());
		assertThat(dto.getCharacteristicValue()).isEqualTo(entity.getCharacteristicValue());
	}

	@Test
	void toEntityFromCreateDTO() {
		TeamCharacteristicCreateDTO createDTO = new TeamCharacteristicCreateDTO();
		createDTO.setTeamId(UUID.randomUUID());
		createDTO.setCharacteristicType(TeamCharacteristicType.SPEED);
		createDTO.setCharacteristicValue(90);

		TeamCharacteristic entity = mapper.toEntity(createDTO);

		assertThat(entity.getGuid()).isNull();
		assertThat(entity.getTeamId()).isEqualTo(createDTO.getTeamId());
		assertThat(entity.getCharacteristicType()).isEqualTo(createDTO.getCharacteristicType());
		assertThat(entity.getCharacteristicValue()).isEqualTo(createDTO.getCharacteristicValue());
	}

	@Test
	void toEntityFromUpdateDTO() {
		UUID id = UUID.randomUUID();
		TeamCharacteristicUpdateDTO updateDTO = new TeamCharacteristicUpdateDTO();
		updateDTO.setId(id);
		updateDTO.setCharacteristicType(TeamCharacteristicType.DEFENSE);
		updateDTO.setCharacteristicValue(80);

		TeamCharacteristic entity = mapper.toEntity(updateDTO);

		assertThat(entity.getGuid()).isEqualTo(id);
		assertThat(entity.getCharacteristicType()).isEqualTo(updateDTO.getCharacteristicType());
		assertThat(entity.getCharacteristicValue()).isEqualTo(updateDTO.getCharacteristicValue());
	}

}
