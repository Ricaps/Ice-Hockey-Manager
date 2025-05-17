package cz.fi.muni.pa165.teamservice.unit.business.mappers;

import cz.fi.muni.pa165.dto.teamservice.FictiveTeamCreateDTO;
import cz.fi.muni.pa165.dto.teamservice.FictiveTeamDTO;
import cz.fi.muni.pa165.dto.teamservice.FictiveTeamUpdateDTO;
import cz.fi.muni.pa165.teamservice.business.mappers.FictiveTeamMapper;
import cz.fi.muni.pa165.teamservice.persistence.entities.FictiveTeam;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Jan Martinek
 */
class FictiveTeamMapperTest {

	private final FictiveTeamMapper mapper = Mappers.getMapper(FictiveTeamMapper.class);

	@Test
	void toDto() {
		FictiveTeam entity = new FictiveTeam();
		entity.setGuid(UUID.randomUUID());
		entity.setName("Avengers");

		FictiveTeamDTO dto = mapper.toDto(entity);

		assertThat(dto.getGuid()).isEqualTo(entity.getGuid());
		assertThat(dto.getName()).isEqualTo(entity.getName());
	}

	@Test
	void toEntityFromCreateDTO() {
		FictiveTeamCreateDTO createDTO = new FictiveTeamCreateDTO();
		createDTO.setName("Avengers");

		FictiveTeam entity = mapper.toEntity(createDTO);

		assertThat(entity.getGuid()).isNull(); // Should be ignored
		assertThat(entity.getName()).isEqualTo(createDTO.getName());
	}

	@Test
	void toEntityFromUpdateDTO() {
		UUID id = UUID.randomUUID();
		FictiveTeamUpdateDTO updateDTO = new FictiveTeamUpdateDTO();
		updateDTO.setGuid(id);
		updateDTO.setName("Avengers Updated");

		FictiveTeam entity = mapper.toEntity(updateDTO);

		assertThat(entity.getGuid()).isEqualTo(id);
		assertThat(entity.getName()).isEqualTo(updateDTO.getName());
	}

}