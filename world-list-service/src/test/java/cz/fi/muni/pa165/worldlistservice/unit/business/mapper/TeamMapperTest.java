package cz.fi.muni.pa165.worldlistservice.unit.business.mapper;

import cz.fi.muni.pa165.dto.worldlistservice.championshipregion.ChampionshipRegionDto;
import cz.fi.muni.pa165.dto.worldlistservice.team.create.TeamCreateDto;
import cz.fi.muni.pa165.dto.worldlistservice.team.detail.TeamChampionshipDto;
import cz.fi.muni.pa165.dto.worldlistservice.team.detail.TeamDetailDto;
import cz.fi.muni.pa165.dto.worldlistservice.team.detail.TeamPlayerDto;
import cz.fi.muni.pa165.dto.worldlistservice.team.list.TeamListDto;
import cz.fi.muni.pa165.dto.worldlistservice.team.update.TeamUpdateDto;
import cz.fi.muni.pa165.enums.ChampionshipRegionType;
import cz.fi.muni.pa165.worldlistservice.business.mappers.ChampionshipMapper;
import cz.fi.muni.pa165.worldlistservice.business.mappers.TeamMapper;
import cz.fi.muni.pa165.worldlistservice.persistence.entities.ChampionshipEntity;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TeamMapperTest {

	@Mock
	private ChampionshipMapper championshipMapper;

	@InjectMocks
	private TeamMapper mapper = Mappers.getMapper(TeamMapper.class);

	private TeamEntity teamEntity;

	private TeamDetailDto teamDetailDto;

	private TeamCreateDto teamCreateDto;

	private TeamUpdateDto teamUpdateDto;

	@BeforeEach
	public void setUp() {
		TeamChampionshipDto teamChampionshipDto = TeamChampionshipDto.builder()
			.id(UUID.randomUUID())
			.name("World Championship")
			.championshipRegion(
					new ChampionshipRegionDto(UUID.randomUUID(), "Europe", ChampionshipRegionType.CONTINENTAL))
			.build();
		TeamPlayerDto teamPlayerDto = TeamPlayerDto.builder()
			.id(UUID.randomUUID())
			.firstName("John")
			.lastName("Doe")
			.overallRating(85)
			.build();

		PlayerEntity playerEntity = PlayerEntity.builder()
			.id(UUID.randomUUID())
			.firstName("John")
			.lastName("Doe")
			.build();

		ChampionshipEntity championshipEntity = ChampionshipEntity.builder().name("World Championship").build();

		teamEntity = TeamEntity.builder()
			.id(UUID.randomUUID())
			.name("FC Awesome")
			.championship(championshipEntity)
			.teamPlayers(Set.of(playerEntity))
			.build();

		teamDetailDto = TeamDetailDto.builder()
			.id(UUID.randomUUID())
			.name("FC Awesome")
			.championship(teamChampionshipDto)
			.teamPlayers(Set.of(teamPlayerDto))
			.build();

		teamUpdateDto = TeamUpdateDto.builder()
			.id(UUID.randomUUID())
			.name("FC Awesome")
			.championshipId(teamChampionshipDto.getId())
			.teamPlayersIds(Set.of(teamPlayerDto.getId()))
			.build();

		teamCreateDto = TeamCreateDto.builder()
			.name("FC Awesome")
			.championshipId(teamChampionshipDto.getId())
			.teamPlayersIds(Set.of(teamPlayerDto.getId()))
			.build();
	}

	@Test
	public void toDetailModel_teamEntityProvided_teamDetailDtoReturned() {
		// Act
		TeamDetailDto result = mapper.toDetailModel(teamEntity);

		// Assert
		assertNotNull(result);
		assertEquals(teamEntity.getName(), result.getName());
		assertEquals(teamEntity.getChampionship().getName(), result.getChampionship().getName());
		assertEquals(teamEntity.getTeamPlayers().size(), result.getTeamPlayers().size());
	}

	@Test
	public void toListModel_teamEntityProvided_teamListDtoReturned() {
		// Act
		TeamListDto result = mapper.toListModel(teamEntity);

		// Assert
		assertNotNull(result);
		assertEquals(teamEntity.getName(), result.getName());
	}

	@Test
	public void toEntity_teamCreateDtoProvided_teamEntityReturned() {
		// Act
		TeamEntity result = mapper.toEntityFromCreateModel(teamCreateDto);

		// Assert
		assertNotNull(result);
		assertEquals(teamDetailDto.getName(), result.getName());
		assertNull(result.getChampionship());
		assertNull(result.getTeamPlayers());
	}

	@Test
	public void toEntity_teamUpdateDtoProvided_teamEntityReturned() {
		// Act
		TeamEntity result = mapper.toEntityFromUpdateModel(teamUpdateDto);

		// Assert
		assertNotNull(result);
		assertEquals(teamDetailDto.getName(), result.getName());
		assertNull(result.getChampionship());
		assertNull(result.getTeamPlayers());
	}

	@Test
	public void toModelList_singleTeamEntityProvided_teamListDtoReturned() {
		// Act
		List<TeamListDto> result = mapper.toModelList(Collections.singletonList(teamEntity));

		// Assert
		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals(teamEntity.getName(), result.getFirst().getName());
	}

	@Test
	public void toPageModel_singleTeamEntityProvided_pageContainingTeamListDtoReturned() {
		// Act
		Page<TeamListDto> result = mapper.toPageModel(new PageImpl<>(Collections.singletonList(teamEntity)));

		// Assert
		assertNotNull(result);
		assertEquals(1, result.getContent().size());
		assertEquals(teamEntity.getName(), result.getContent().getFirst().getName());
	}

	@Test
	public void toDetailModel_teamEntityWithNullName_teamDetailDtoWithNullNameReturned() {
		// Arrange
		teamEntity.setName(null);

		// Act
		TeamDetailDto result = mapper.toDetailModel(teamEntity);

		// Assert
		assertNotNull(result);
		assertNull(result.getName());
	}

	@Test
	public void toEntity_teamCreateDtoWithNullName_teamEntityWithNullNameReturned() {
		// Arrange
		teamCreateDto.setName(null);

		// Act
		TeamEntity result = mapper.toEntityFromCreateModel(teamCreateDto);

		// Assert
		assertNotNull(result);
		assertNull(result.getName());
	}

	@Test
	public void toEntity_teamUpdateDtoWithNullName_teamEntityWithNullNameReturned() {
		// Arrange
		teamUpdateDto.setName(null);

		// Act
		TeamEntity result = mapper.toEntityFromUpdateModel(teamUpdateDto);

		// Assert
		assertNotNull(result);
		assertNull(result.getName());
	}

	@Test
	public void toListModel_teamEntityWithNullName_teamListDtoWithNullNameReturned() {
		// Arrange
		teamEntity.setName(null);

		// Act
		TeamListDto result = mapper.toListModel(teamEntity);

		// Assert
		assertNotNull(result);
		assertNull(result.getName());
	}

	@Test
	public void toModelList_emptyListProvided_emptyListReturned() {
		// Act
		List<TeamListDto> result = mapper.toModelList(Collections.emptyList());

		// Assert
		assertNotNull(result);
		assertTrue(result.isEmpty());
	}

	@Test
	public void toPageModel_emptyListProvided_emptyPageReturned() {
		// Act
		Page<TeamListDto> result = mapper.toPageModel(new PageImpl<>(Collections.emptyList()));

		// Assert
		assertNotNull(result);
		assertTrue(result.isEmpty());
	}

	@Test
	public void toDetailModel_teamEntityWithPlayers_teamDetailDtoWithPlayersReturned() {
		// Arrange
		teamEntity.setName("FC With Players");

		// Act
		TeamDetailDto result = mapper.toDetailModel(teamEntity);

		// Assert
		assertNotNull(result);
		assertEquals("FC With Players", result.getName());
		assertEquals(1, result.getTeamPlayers().size());
	}

	@Test
	public void toEntity_teamCreateDtoWithPlayers_teamEntityWithPlayersReturned() {
		// Arrange
		teamCreateDto.setName("FC With Players");

		// Act
		TeamEntity result = mapper.toEntityFromCreateModel(teamCreateDto);

		// Assert
		assertNotNull(result);
		assertEquals("FC With Players", result.getName());
		assertNull(result.getTeamPlayers());
	}

	@Test
	public void toEntity_teamUpdateDtoWithPlayers_teamEntityWithPlayersReturned() {
		// Arrange
		teamUpdateDto.setName("FC With Players");

		// Act
		TeamEntity result = mapper.toEntityFromUpdateModel(teamUpdateDto);

		// Assert
		assertNotNull(result);
		assertEquals("FC With Players", result.getName());
		assertNull(result.getTeamPlayers());
	}

	@Test
	public void toPageModel_multipleTeamEntitiesProvided_pageContainingTeamListDtosReturned() {
		// Arrange
		TeamEntity team2 = new TeamEntity();
		team2.setName("FC Example");
		Page<TeamEntity> page = new PageImpl<>(List.of(teamEntity, team2));

		// Act
		Page<TeamListDto> result = mapper.toPageModel(page);

		// Assert
		assertNotNull(result);
		assertEquals(2, result.getContent().size());
		assertEquals("FC Awesome", result.getContent().get(0).getName());
		assertEquals("FC Example", result.getContent().get(1).getName());
	}

	@Test
	public void toModelList_multipleTeamEntitiesProvided_listContainingTeamListDtosReturned() {
		// Arrange
		TeamEntity team2 = new TeamEntity();
		team2.setName("FC Example");

		// Act
		List<TeamListDto> result = mapper.toModelList(List.of(teamEntity, team2));

		// Assert
		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals("FC Awesome", result.get(0).getName());
		assertEquals("FC Example", result.get(1).getName());
	}

	@Test
	public void toDetailModel_teamEntityWithNullChampionship_teamDetailDtoWithNullChampionshipReturned() {
		// Arrange
		teamEntity.setChampionship(null);

		// Act
		TeamDetailDto result = mapper.toDetailModel(teamEntity);

		// Assert
		assertNotNull(result);
		assertNull(result.getChampionship());
	}

}
