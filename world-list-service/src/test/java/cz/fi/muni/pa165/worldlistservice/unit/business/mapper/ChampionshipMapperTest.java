package cz.fi.muni.pa165.worldlistservice.unit.business.mapper;

import cz.fi.muni.pa165.dto.worldlistservice.championship.create.ChampionshipCreateDto;
import cz.fi.muni.pa165.dto.worldlistservice.championship.detail.ChampionshipDetailDto;
import cz.fi.muni.pa165.dto.worldlistservice.championship.detail.ChampionshipTeamDto;
import cz.fi.muni.pa165.dto.worldlistservice.championship.list.ChampionshipListDto;
import cz.fi.muni.pa165.dto.worldlistservice.championship.update.ChampionshipUpdateDto;
import cz.fi.muni.pa165.dto.worldlistservice.championshipregion.ChampionshipRegionDto;
import cz.fi.muni.pa165.worldlistservice.business.mappers.ChampionshipMapper;
import cz.fi.muni.pa165.worldlistservice.business.mappers.ChampionshipRegionMapper;
import cz.fi.muni.pa165.worldlistservice.persistence.entities.ChampionshipEntity;
import cz.fi.muni.pa165.worldlistservice.persistence.entities.ChampionshipRegionEntity;
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
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ChampionshipMapperTest {

	@Mock
	private ChampionshipRegionMapper championshipRegionMapper;

	@InjectMocks
	private ChampionshipMapper mapper = Mappers.getMapper(ChampionshipMapper.class);

	private ChampionshipEntity championshipEntity;

	private ChampionshipEntity championshipCreateUpdateEntity;

	private ChampionshipDetailDto championshipDetailDto;

	private ChampionshipCreateDto championshipCreateDto;

	private ChampionshipUpdateDto championshipUpdateDto;

	private ChampionshipRegionEntity championshipRegionEntity;

	private ChampionshipRegionDto championshipRegionDto;

	@BeforeEach
	public void setUp() {
		// Arrange
		championshipRegionEntity = ChampionshipRegionEntity.builder().name("Europe").build();

		championshipRegionDto = ChampionshipRegionDto.builder().name("Europe").build();

		TeamEntity championshipTeamEntity = TeamEntity.builder().id(UUID.randomUUID()).name("Best Team").build();

		ChampionshipTeamDto championshipTeamDto = ChampionshipTeamDto.builder()
			.id(UUID.randomUUID())
			.name("Best Team")
			.build();

		championshipEntity = ChampionshipEntity.builder()
			.id(UUID.randomUUID())
			.name("World Championship")
			.championshipRegion(championshipRegionEntity)
			.championshipTeams(Set.of(championshipTeamEntity))
			.build();

		championshipDetailDto = ChampionshipDetailDto.builder()
			.name("World Championship")
			.championshipRegion(championshipRegionDto)
			.championshipTeams(Set.of(championshipTeamDto))
			.build();

		championshipCreateDto = ChampionshipCreateDto.builder()
			.name("World Championship")
			.championshipRegionId(championshipRegionEntity.getId())
			.championshipTeamsIds(Set.of(championshipTeamEntity.getId()))
			.build();

		championshipUpdateDto = ChampionshipUpdateDto.builder()
			.id(UUID.randomUUID())
			.name("World Championship")
			.championshipRegionId(championshipRegionEntity.getId())
			.championshipTeamsIds(Set.of(championshipTeamEntity.getId()))
			.build();

		championshipCreateUpdateEntity = ChampionshipEntity.builder()
			.id(championshipUpdateDto.getId())
			.name(championshipUpdateDto.getName())
			.build();
	}

	@Test
	public void toModel_entityIsValid_modelIsCorrectlyMapped() {
		// Act
		ChampionshipDetailDto result = mapper.toDetailModel(championshipEntity);

		// Assert
		assertNotNull(result);
		assertEquals(championshipEntity.getName(), result.getName());
		assertEquals(championshipEntity.getChampionshipRegion().getName(), result.getChampionshipRegion().getName());
		assertEquals(championshipEntity.getChampionshipRegion().getType(), result.getChampionshipRegion().getType());
		assertEquals(championshipEntity.getChampionshipTeams().size(), result.getChampionshipTeams().size());
		assertEquals(championshipEntity.getChampionshipTeams().size(), result.getChampionshipTeams().size());
	}

	@Test
	public void toEntityFromCreateModel_modelIsValid_entityIsCorrectlyMapped() {
		// Act
		ChampionshipEntity result = mapper.toEntityFromCreateModel(championshipCreateDto);

		// Assert
		assertNotNull(result);
		assertEquals(championshipDetailDto.getName(), result.getName());
		assertNull(result.getChampionshipRegion());
	}

	@Test
	public void toEntityFromUpdateModel_modelIsValid_entityIsCorrectlyMapped() {
		// Act
		ChampionshipEntity result = mapper.toEntityFromUpdateModel(championshipUpdateDto);

		// Assert
		assertNotNull(result);
		assertEquals(championshipDetailDto.getName(), result.getName());
		assertNull(result.getChampionshipRegion());
	}

	@Test
	public void toModel_entityWithNullTeams_teamsAreHandledCorrectly() {
		// Arrange
		championshipEntity.setChampionshipTeams(null);

		// Act
		ChampionshipDetailDto result = mapper.toDetailModel(championshipEntity);

		// Assert
		assertNotNull(result);
		assertNull(result.getChampionshipTeams());
	}

	@Test
	public void toEntityFromCreateModel_modelWithNullTeams_teamsAreHandledCorrectly() {
		// Arrange
		championshipCreateDto.setChampionshipTeamsIds(new HashSet<>());

		// Act
		ChampionshipEntity result = mapper.toEntityFromCreateModel(championshipCreateDto);

		// Assert
		assertNotNull(result);
		assertNull(result.getChampionshipTeams());
	}

	@Test
	public void toEntityFromUpdateModel_modelWithNullTeams_teamsAreHandledCorrectly() {
		// Arrange
		championshipUpdateDto.setChampionshipTeamsIds(new HashSet<>());

		// Act
		ChampionshipEntity result = mapper.toEntityFromUpdateModel(championshipUpdateDto);

		// Assert
		assertNotNull(result);
		assertNull(result.getChampionshipTeams());
	}

	@Test
	public void toModel_entityWithEmptyTeams_emptyListIsHandledCorrectly() {
		// Arrange
		championshipEntity.setChampionshipTeams(Collections.emptySet());

		// Act
		ChampionshipDetailDto result = mapper.toDetailModel(championshipEntity);

		// Assert
		assertNotNull(result);
		assertTrue(result.getChampionshipTeams().isEmpty());
	}

	@Test
	public void toEntityFromCreateModel_modelWithEmptyTeams_emptyListIsHandledCorrectly() {
		// Arrange
		championshipCreateDto.setChampionshipTeamsIds(Collections.emptySet());

		// Act
		ChampionshipEntity result = mapper.toEntityFromCreateModel(championshipCreateDto);

		// Assert
		assertNotNull(result);
		assertNull(result.getChampionshipTeams());
	}

	@Test
	public void toEntityFromUpdateModel_modelWithEmptyTeams_emptyListIsHandledCorrectly() {
		// Arrange
		championshipUpdateDto.setChampionshipTeamsIds(Collections.emptySet());

		// Act
		ChampionshipEntity result = mapper.toEntityFromUpdateModel(championshipUpdateDto);

		// Assert
		assertNotNull(result);
		assertNull(result.getChampionshipTeams());
	}

	@Test
	public void toModel_entityWithDifferentRegion_regionIsMappedCorrectly() {
		// Arrange
		championshipRegionEntity.setName("Asia");

		// Act
		ChampionshipDetailDto result = mapper.toDetailModel(championshipEntity);

		// Assert
		assertNotNull(result);
		assertEquals("Asia", result.getChampionshipRegion().getName());
	}

	@Test
	public void toModel_entityWithLongName_nameIsMappedCorrectly() {
		// Arrange
		String longName = "A".repeat(200);
		championshipEntity.setName(longName);

		// Act
		ChampionshipDetailDto result = mapper.toDetailModel(championshipEntity);

		// Assert
		assertNotNull(result);
		assertEquals(longName, result.getName());
	}

	@Test
	public void toEntityFromCreateModel_modelWithLongName_nameIsMappedCorrectly() {
		// Arrange
		String longName = "A".repeat(200);
		championshipCreateDto.setName(longName);

		// Act
		ChampionshipEntity result = mapper.toEntityFromCreateModel(championshipCreateDto);

		// Assert
		assertNotNull(result);
		assertEquals(longName, result.getName());
	}

	@Test
	public void toEntityFromUpdateModel_modelWithLongName_nameIsMappedCorrectly() {
		// Arrange
		String longName = "A".repeat(200);
		championshipUpdateDto.setName(longName);

		// Act
		ChampionshipEntity result = mapper.toEntityFromUpdateModel(championshipUpdateDto);

		// Assert
		assertNotNull(result);
		assertEquals(longName, result.getName());
	}

	@Test
	public void toModel_entityWithSpecialCharacters_specialCharactersAreHandledCorrectly() {
		// Arrange
		championshipEntity.setName("Championship!@#$%^&*()");

		// Act
		ChampionshipDetailDto result = mapper.toDetailModel(championshipEntity);

		// Assert
		assertNotNull(result);
		assertEquals("Championship!@#$%^&*()", result.getName());
	}

	@Test
	public void toEntityFromCreateModel_modelWithSpecialCharacters_specialCharactersAreHandledCorrectly() {
		// Arrange
		championshipCreateDto.setName("Championship!@#$%^&*()");

		// Act
		ChampionshipEntity result = mapper.toEntityFromCreateModel(championshipCreateDto);

		// Assert
		assertNotNull(result);
		assertEquals("Championship!@#$%^&*()", result.getName());
	}

	@Test
	public void toEntityFromUpdateModel_modelWithSpecialCharacters_specialCharactersAreHandledCorrectly() {
		// Arrange
		championshipUpdateDto.setName("Championship!@#$%^&*()");

		// Act
		ChampionshipEntity result = mapper.toEntityFromUpdateModel(championshipUpdateDto);

		// Assert
		assertNotNull(result);
		assertEquals("Championship!@#$%^&*()", result.getName());
	}

	@Test
	public void toPageModel_emptyPage_returnsEmptyPage() {
		// Arrange
		Page<ChampionshipEntity> emptyPage = new PageImpl<>(Collections.emptyList());

		// Act
		Page<ChampionshipListDto> result = mapper.toPageModel(emptyPage);

		// Assert
		assertNotNull(result);
		assertTrue(result.isEmpty());
	}

}
