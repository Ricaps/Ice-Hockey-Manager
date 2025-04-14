package cz.fi.muni.pa165.worldlistservice.unit.business.mapper;

import cz.fi.muni.pa165.dto.worldlistservice.championshipregion.ChampionshipRegionDto;
import cz.fi.muni.pa165.dto.worldlistservice.championshipregion.create.ChampionshipRegionCreateDto;
import cz.fi.muni.pa165.enums.ChampionshipRegionType;
import cz.fi.muni.pa165.worldlistservice.business.mappers.ChampionshipRegionMapper;
import cz.fi.muni.pa165.worldlistservice.persistence.entities.ChampionshipRegionEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ChampionshipRegionMapperTest {

	private final ChampionshipRegionMapper mapper = Mappers.getMapper(ChampionshipRegionMapper.class);

	private final ChampionshipRegionEntity championshipRegionEntity = ChampionshipRegionEntity.builder()
		.name("Europe")
		.type(ChampionshipRegionType.GLOBAL)
		.build();

	private final ChampionshipRegionDto championshipRegionDto = ChampionshipRegionDto.builder()
		.name("Europe")
		.type(ChampionshipRegionType.GLOBAL)
		.build();

	private final ChampionshipRegionCreateDto championshipRegionCreateDto = ChampionshipRegionCreateDto.builder()
		.name("Europe")
		.type(ChampionshipRegionType.GLOBAL)
		.build();

	@Test
	public void toModelList_multipleEntities_correctlyMappedToList() {
		// Arrange
		List<ChampionshipRegionEntity> entities = List.of(championshipRegionEntity);

		// Act
		List<ChampionshipRegionDto> result = mapper.toModelList(entities);

		// Assert
		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals(championshipRegionEntity.getName(), result.getFirst().getName());
		assertEquals(championshipRegionEntity.getType(), result.getFirst().getType());
	}

	@Test
	public void toPageModel_pageOfEntities_correctlyMappedToPage() {
		// Arrange
		List<ChampionshipRegionEntity> entities = List.of(championshipRegionEntity);
		Page<ChampionshipRegionEntity> entityPage = new PageImpl<>(entities);

		// Act
		Page<ChampionshipRegionDto> result = mapper.toPageModel(entityPage);

		// Assert
		assertNotNull(result);
		assertEquals(1, result.getTotalElements());
		assertEquals(championshipRegionEntity.getName(), result.getContent().getFirst().getName());
		assertEquals(championshipRegionEntity.getType(), result.getContent().getFirst().getType());
	}

	@Test
	public void toModel_entityIsValid_modelIsCorrectlyMapped() {
		// Act
		ChampionshipRegionDto result = mapper.toDetailModel(championshipRegionEntity);

		// Assert
		assertNotNull(result);
		assertEquals(championshipRegionEntity.getName(), result.getName());
		assertEquals(championshipRegionEntity.getType(), result.getType());
	}

	@Test
	public void toEntityFromCreateModel_modelIsValid_entityIsCorrectlyMapped() {
		// Act
		ChampionshipRegionEntity result = mapper.toEntityFromCreateModel(championshipRegionCreateDto);

		// Assert
		assertNotNull(result);
		assertEquals(championshipRegionDto.getName(), result.getName());
		assertEquals(championshipRegionDto.getType(), result.getType());
	}

	@Test
	public void toEntityFromUpdateModel_modelIsValid_entityIsCorrectlyMapped() {
		// Act
		ChampionshipRegionEntity result = mapper.toEntityFromUpdateModel(championshipRegionDto);

		// Assert
		assertNotNull(result);
		assertEquals(championshipRegionDto.getName(), result.getName());
		assertEquals(championshipRegionDto.getType(), result.getType());
	}

	@Test
	public void toModel_entityWithNullValues_nullValuesAreHandledCorrectly() {
		// Arrange
		championshipRegionEntity.setName(null);
		championshipRegionEntity.setType(null);

		// Act
		ChampionshipRegionDto result = mapper.toDetailModel(championshipRegionEntity);

		// Assert
		assertNull(result.getName());
		assertNull(result.getType());
	}

	@Test
	public void toEntityFromCreateModel_modelWithNullValues_nullValuesAreHandledCorrectly() {
		// Arrange
		championshipRegionCreateDto.setName(null);
		championshipRegionCreateDto.setType(null);

		// Act
		ChampionshipRegionEntity result = mapper.toEntityFromCreateModel(championshipRegionCreateDto);

		// Assert
		assertNull(result.getName());
		assertNull(result.getType());
	}

	@Test
	public void toEntityFromUpdateModel_modelWithNullValues_nullValuesAreHandledCorrectly() {
		// Arrange
		championshipRegionDto.setName(null);
		championshipRegionDto.setType(null);

		// Act
		ChampionshipRegionEntity result = mapper.toEntityFromUpdateModel(championshipRegionDto);

		// Assert
		assertNull(result.getName());
		assertNull(result.getType());
	}

	@Test
	public void toModel_entityWithEmptyName_emptyNameIsHandledCorrectly() {
		// Arrange
		championshipRegionEntity.setName("");

		// Act
		ChampionshipRegionDto result = mapper.toDetailModel(championshipRegionEntity);

		// Assert
		assertNotNull(result);
		assertEquals("", result.getName());
	}

	@Test
	public void toEntityFromCreateModel_modelWithEmptyName_emptyNameIsHandledCorrectly() {
		// Arrange
		championshipRegionCreateDto.setName("");

		// Act
		ChampionshipRegionEntity result = mapper.toEntityFromCreateModel(championshipRegionCreateDto);

		// Assert
		assertNotNull(result);
		assertEquals("", result.getName());
	}

	@Test
	public void toEntity_modelWithEmptyName_emptyNameIsHandledCorrectly() {
		// Arrange
		championshipRegionDto.setName("");

		// Act
		ChampionshipRegionEntity result = mapper.toEntityFromUpdateModel(championshipRegionDto);

		// Assert
		assertNotNull(result);
		assertEquals("", result.getName());
	}

	@Test
	public void toModel_entityWithDifferentRegionType_typeIsMappedCorrectly() {
		// Arrange
		championshipRegionEntity.setType(ChampionshipRegionType.REGIONAL);

		// Act
		ChampionshipRegionDto result = mapper.toDetailModel(championshipRegionEntity);

		// Assert
		assertNotNull(result);
		assertEquals(ChampionshipRegionType.REGIONAL, result.getType());
	}

	@Test
	public void toEntityFromCreateModel_modelWithDifferentRegionType_typeIsMappedCorrectly() {
		// Arrange
		championshipRegionCreateDto.setType(ChampionshipRegionType.REGIONAL);

		// Act
		ChampionshipRegionEntity result = mapper.toEntityFromCreateModel(championshipRegionCreateDto);

		// Assert
		assertNotNull(result);
		assertEquals(ChampionshipRegionType.REGIONAL, result.getType());
	}

	@Test
	public void toEntityFromUpdateModel_modelWithDifferentRegionType_typeIsMappedCorrectly() {
		// Arrange
		championshipRegionDto.setType(ChampionshipRegionType.REGIONAL);

		// Act
		ChampionshipRegionEntity result = mapper.toEntityFromUpdateModel(championshipRegionDto);

		// Assert
		assertNotNull(result);
		assertEquals(ChampionshipRegionType.REGIONAL, result.getType());
	}

	@Test
	public void toModel_entityWithLongName_nameIsMappedCorrectly() {
		// Arrange
		String longName = "This is a very long name for a region that exceeds typical lengths";
		championshipRegionEntity.setName(longName);

		// Act
		ChampionshipRegionDto result = mapper.toDetailModel(championshipRegionEntity);

		// Assert
		assertNotNull(result);
		assertEquals(longName, result.getName());
	}

	@Test
	public void toEntityFromCreateModel_modelWithLongName_nameIsMappedCorrectly() {
		// Arrange
		String longName = "This is a very long name for a region that exceeds typical lengths";
		championshipRegionCreateDto.setName(longName);

		// Act
		ChampionshipRegionEntity result = mapper.toEntityFromCreateModel(championshipRegionCreateDto);

		// Assert
		assertNotNull(result);
		assertEquals(longName, result.getName());
	}

	@Test
	public void toEntityFromUpdateModel_modelWithLongName_nameIsMappedCorrectly() {
		// Arrange
		String longName = "This is a very long name for a region that exceeds typical lengths";
		championshipRegionDto.setName(longName);

		// Act
		ChampionshipRegionEntity result = mapper.toEntityFromUpdateModel(championshipRegionDto);

		// Assert
		assertNotNull(result);
		assertEquals(longName, result.getName());
	}

	@Test
	public void toModel_entityWithSpecialCharacters_specialCharactersAreHandledCorrectly() {
		// Arrange
		championshipRegionEntity.setName("Europe!@#$%^&*()");

		// Act
		ChampionshipRegionDto result = mapper.toDetailModel(championshipRegionEntity);

		// Assert
		assertNotNull(result);
		assertEquals("Europe!@#$%^&*()", result.getName());
	}

	@Test
	public void toEntityFromCreateModel_modelWithSpecialCharacters_specialCharactersAreHandledCorrectly() {
		// Arrange
		championshipRegionCreateDto.setName("Europe!@#$%^&*()");

		// Act
		ChampionshipRegionEntity result = mapper.toEntityFromCreateModel(championshipRegionCreateDto);

		// Assert
		assertNotNull(result);
		assertEquals("Europe!@#$%^&*()", result.getName());
	}

	@Test
	public void toEntityFromUpdateModel_modelWithSpecialCharacters_specialCharactersAreHandledCorrectly() {
		// Arrange
		championshipRegionDto.setName("Europe!@#$%^&*()");

		// Act
		ChampionshipRegionEntity result = mapper.toEntityFromUpdateModel(championshipRegionDto);

		// Assert
		assertNotNull(result);
		assertEquals("Europe!@#$%^&*()", result.getName());
	}

	@Test
	public void toModel_entityWithLargeNumber_typeIsHandledCorrectly() {
		// Arrange
		championshipRegionEntity.setType(ChampionshipRegionType.valueOf("GLOBAL"));

		// Act
		ChampionshipRegionDto result = mapper.toDetailModel(championshipRegionEntity);

		// Assert
		assertNotNull(result);
		assertEquals(ChampionshipRegionType.GLOBAL, result.getType());
	}

}