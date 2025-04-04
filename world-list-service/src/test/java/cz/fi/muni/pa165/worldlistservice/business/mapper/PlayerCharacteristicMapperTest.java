package cz.fi.muni.pa165.worldlistservice.business.mapper;

import cz.fi.muni.pa165.dto.worldlistservice.playercharacteristic.PlayerCharacteristicDto;
import cz.fi.muni.pa165.dto.worldlistservice.playercharacteristic.create.PlayerCharacteristicCreateDto;
import cz.fi.muni.pa165.enums.PlayerCharacteristicType;
import cz.fi.muni.pa165.worldlistservice.business.mappers.PlayerCharacteristicMapper;
import cz.fi.muni.pa165.worldlistservice.persistence.entities.PlayerCharacteristicEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class PlayerCharacteristicMapperTest {

	private final PlayerCharacteristicEntity playerCharacteristicEntity = PlayerCharacteristicEntity.builder()
		.type(PlayerCharacteristicType.SPEED)
		.value(85)
		.build();

	private final PlayerCharacteristicDto playerCharacteristicDto = PlayerCharacteristicDto.builder()
		.type(PlayerCharacteristicType.SPEED)
		.value(85)
		.build();

	private final PlayerCharacteristicCreateDto playerCharacteristicCreateDto = PlayerCharacteristicCreateDto.builder()
		.type(PlayerCharacteristicType.SPEED)
		.value(85)
		.build();

	@InjectMocks
	private PlayerCharacteristicMapper mapper = Mappers.getMapper(PlayerCharacteristicMapper.class);

	@Test
	public void toDetailModel_validEntity_returnsDto() {
		// Act
		PlayerCharacteristicDto result = mapper.toDetailModel(playerCharacteristicEntity);

		// Verify
		assertNotNull(result);
		assertEquals(playerCharacteristicEntity.getType(), result.getType());
		assertEquals(playerCharacteristicEntity.getValue(), result.getValue());
	}

	@Test
	public void toDetailModel_nullEntity_returnsNull() {
		// Act
		PlayerCharacteristicDto result = mapper.toDetailModel(null);

		// Verify
		assertNull(result);
	}

	@Test
	public void toListModel_validEntity_returnsDto() {
		// Act
		PlayerCharacteristicDto result = mapper.toListModel(playerCharacteristicEntity);

		// Verify
		assertNotNull(result);
		assertEquals(playerCharacteristicEntity.getType(), result.getType());
		assertEquals(playerCharacteristicEntity.getValue(), result.getValue());
	}

	@Test
	public void toListModel_nullEntity_returnsNull() {
		// Act
		PlayerCharacteristicDto result = mapper.toListModel(null);

		// Verify
		assertNull(result);
	}

	@Test
	public void toEntityFromCreateModel_validDto_returnsEntity() {
		// Act
		PlayerCharacteristicEntity result = mapper.toEntityFromCreateModel(playerCharacteristicCreateDto);

		// Verify
		assertNotNull(result);
		assertEquals(playerCharacteristicCreateDto.getType(), result.getType());
		assertEquals(playerCharacteristicCreateDto.getValue(), result.getValue());
	}

	@Test
	public void toEntityFromUpdateModel_validDto_returnsEntity() {
		// Act
		PlayerCharacteristicEntity result = mapper.toEntityFromUpdateModel(playerCharacteristicDto);

		// Verify
		assertNotNull(result);
		assertEquals(playerCharacteristicDto.getType(), result.getType());
		assertEquals(playerCharacteristicDto.getValue(), result.getValue());
	}

	@Test
	public void toEntity_nullDto_returnsNull() {
		// Act
		PlayerCharacteristicEntity result = mapper.toEntityFromCreateModel(null);

		// Verify
		assertNull(result);
	}

	@Test
	public void toEntityFromListModel_nullDto_returnsNull() {
		// Act
		PlayerCharacteristicEntity result = mapper.toEntityFromCreateModel(null);

		// Verify
		assertNull(result);
	}

}
