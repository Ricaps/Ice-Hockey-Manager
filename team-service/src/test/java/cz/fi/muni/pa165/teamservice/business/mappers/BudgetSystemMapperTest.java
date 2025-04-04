package cz.fi.muni.pa165.teamservice.business.mappers;

import cz.fi.muni.pa165.dto.teamService.BudgetSystemCreateDTO;
import cz.fi.muni.pa165.dto.teamService.BudgetSystemDTO;
import cz.fi.muni.pa165.dto.teamService.BudgetSystemUpdateDTO;
import cz.fi.muni.pa165.teamservice.persistence.entities.BudgetSystem;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class BudgetSystemMapperTest {

	@Autowired
	private BudgetSystemMapper mapper;

	@Test
	void toDto() {
		BudgetSystem entity = new BudgetSystem();
		entity.setGuid(UUID.randomUUID());
		entity.setAmount(100000.0);

		BudgetSystemDTO dto = mapper.toDto(entity);

		assertThat(dto.getGuid()).isEqualTo(entity.getGuid());
		assertThat(dto.getAmount()).isEqualTo(entity.getAmount());
	}

	@Test
	void toEntityFromCreateDTO() {
		BudgetSystemCreateDTO createDTO = new BudgetSystemCreateDTO();
		createDTO.setAmount(150000.0);

		BudgetSystem entity = mapper.toEntity(createDTO);

		assertThat(entity.getGuid()).isNull();
		assertThat(entity.getAmount()).isEqualTo(createDTO.getAmount());
	}

	@Test
	void toEntityFromUpdateDTO() {
		UUID id = UUID.randomUUID();
		BudgetSystemUpdateDTO updateDTO = new BudgetSystemUpdateDTO();
		updateDTO.setGuid(id);
		updateDTO.setAmount(200000.0);

		BudgetSystem entity = mapper.toEntity(updateDTO);

		assertThat(entity.getGuid()).isEqualTo(id);
		assertThat(entity.getAmount()).isEqualTo(updateDTO.getAmount());
	}

}