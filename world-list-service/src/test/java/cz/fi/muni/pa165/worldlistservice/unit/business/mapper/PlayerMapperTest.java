package cz.fi.muni.pa165.worldlistservice.unit.business.mapper;

import cz.fi.muni.pa165.dto.worldlistservice.player.create.PlayerCreateDto;
import cz.fi.muni.pa165.dto.worldlistservice.player.detail.PlayerDetailDto;
import cz.fi.muni.pa165.dto.worldlistservice.player.list.PlayerListDto;
import cz.fi.muni.pa165.dto.worldlistservice.player.update.PlayerUpdateDto;
import cz.fi.muni.pa165.worldlistservice.business.mappers.PlayerMapper;
import cz.fi.muni.pa165.worldlistservice.business.mappers.TeamMapper;
import cz.fi.muni.pa165.worldlistservice.persistence.entities.PlayerCharacteristicEntity;
import cz.fi.muni.pa165.worldlistservice.persistence.entities.PlayerEntity;
import cz.fi.muni.pa165.worldlistservice.persistence.entities.TeamEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class PlayerMapperTest {

	@InjectMocks
	private PlayerMapper mapper = Mappers.getMapper(PlayerMapper.class);

	@Mock
	private TeamMapper teamMapper;

	private PlayerEntity playerEntity;

	private PlayerCreateDto playerCreateDto;

	private PlayerUpdateDto playerUpdateDto;

	@BeforeEach
	public void setUp() {
		TeamEntity teamEntity = TeamEntity.builder().name("FC Awesome").build();
		PlayerCharacteristicEntity characteristicEntity = PlayerCharacteristicEntity.builder().value(80).build();

		playerEntity = PlayerEntity.builder()
			.firstName("John")
			.lastName("Doe")
			.overallRating(85)
			.team(teamEntity)
			.playerCharacteristics(Set.of(characteristicEntity))
			.build();

		PlayerDetailDto.builder().firstName("John").lastName("Doe").overallRating(85).build();

		playerUpdateDto = PlayerUpdateDto.builder().firstName("John").lastName("Doe").overallRating(85).build();

		playerCreateDto = PlayerCreateDto.builder().firstName("John").lastName("Doe").overallRating(85).build();
	}

	@Test
	public void toDetailModel_validEntity_returnsDetailDto() {
		// Act
		PlayerDetailDto result = mapper.toDetailModel(playerEntity);

		// Assert
		assertNotNull(result);
		assertEquals(playerEntity.getFirstName(), result.getFirstName());
		assertEquals(playerEntity.getLastName(), result.getLastName());
		assertEquals(playerEntity.getOverallRating(), result.getOverallRating());
	}

	@Test
	public void toListModel_validEntity_returnsListDto() {
		// Act
		PlayerListDto result = mapper.toListModel(playerEntity);

		// Assert
		assertNotNull(result);
		assertEquals(playerEntity.getFirstName(), result.getFirstName());
		assertEquals(playerEntity.getLastName(), result.getLastName());
		assertEquals(playerEntity.getOverallRating(), result.getOverallRating());
	}

	@Test
	public void toEntity_validCreateDto_returnsEntity() {
		// Act
		PlayerEntity result = mapper.toEntityFromCreateModel(playerCreateDto);

		// Assert
		assertNotNull(result);
		assertEquals(playerCreateDto.getFirstName(), result.getFirstName());
		assertEquals(playerCreateDto.getLastName(), result.getLastName());
		assertEquals(playerCreateDto.getOverallRating(), result.getOverallRating());
	}

	@Test
	public void toEntity_validUpdateDto_returnsEntity() {
		// Act
		PlayerEntity result = mapper.toEntityFromUpdateModel(playerUpdateDto);

		// Assert
		assertNotNull(result);
		assertEquals(playerUpdateDto.getFirstName(), result.getFirstName());
		assertEquals(playerUpdateDto.getLastName(), result.getLastName());
		assertEquals(playerUpdateDto.getOverallRating(), result.getOverallRating());
	}

	@Test
	public void toModelList_validList_returnsDtoList() {
		// Act
		List<PlayerListDto> result = mapper.toModelList(Collections.singletonList(playerEntity));

		// Assert
		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals(playerEntity.getFirstName(), result.getFirst().getFirstName());
	}

	@Test
	public void toPageModel_validPage_returnsDtoPage() {
		// Act
		Page<PlayerListDto> result = mapper.toPageModel(new PageImpl<>(Collections.singletonList(playerEntity)));

		// Assert
		assertNotNull(result);
		assertEquals(1, result.getContent().size());
		assertEquals(playerEntity.getFirstName(), result.getContent().getFirst().getFirstName());
	}

	@Test
	public void toDetailModel_nullNames_returnsDtoWithNullNames() {
		// Arrange
		playerEntity.setFirstName(null);
		playerEntity.setLastName(null);

		// Act
		PlayerDetailDto result = mapper.toDetailModel(playerEntity);

		// Assert
		assertNotNull(result);
		assertNull(result.getFirstName());
		assertNull(result.getLastName());
	}

	@Test
	public void toEntityFromCreateDto_nullNames_returnsEntityWithNullNames() {
		// Arrange
		playerCreateDto.setFirstName(null);
		playerCreateDto.setLastName(null);

		// Act
		PlayerEntity result = mapper.toEntityFromCreateModel(playerCreateDto);

		// Assert
		assertNotNull(result);
		assertNull(result.getFirstName());
		assertNull(result.getLastName());
	}

	@Test
	public void toEntityFromUpdateDto_nullNames_returnsEntityWithNullNames() {
		// Arrange
		playerUpdateDto.setFirstName(null);
		playerUpdateDto.setLastName(null);

		// Act
		PlayerEntity result = mapper.toEntityFromUpdateModel(playerUpdateDto);

		// Assert
		assertNotNull(result);
		assertNull(result.getFirstName());
		assertNull(result.getLastName());
	}

	@Test
	public void toDetailModel_emptyCharacteristics_returnsDtoWithEmptyCharacteristics() {
		// Arrange
		playerEntity.setPlayerCharacteristics(Collections.emptySet());

		// Act
		PlayerDetailDto result = mapper.toDetailModel(playerEntity);

		// Assert
		assertNotNull(result);
		assertEquals(0, result.getPlayerCharacteristics().size());
	}

}
