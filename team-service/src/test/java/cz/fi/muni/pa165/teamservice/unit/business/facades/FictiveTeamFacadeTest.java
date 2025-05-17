package cz.fi.muni.pa165.teamservice.unit.business.facades;

import cz.fi.muni.pa165.dto.teamservice.FictiveTeamCreateDTO;
import cz.fi.muni.pa165.dto.teamservice.FictiveTeamDTO;
import cz.fi.muni.pa165.dto.teamservice.FictiveTeamUpdateDTO;
import cz.fi.muni.pa165.teamservice.api.exception.ResourceAlreadyExistsException;
import cz.fi.muni.pa165.teamservice.api.exception.ResourceNotFoundException;
import cz.fi.muni.pa165.teamservice.business.facades.FictiveTeamFacade;
import cz.fi.muni.pa165.teamservice.business.mappers.FictiveTeamMapper;
import cz.fi.muni.pa165.teamservice.business.services.FictiveTeamService;
import cz.fi.muni.pa165.teamservice.persistence.entities.FictiveTeam;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Jan Martinek
 */
@ExtendWith(MockitoExtension.class)
public class FictiveTeamFacadeTest {

	private final UUID teamId = UUID.randomUUID();

	@Mock
	private FictiveTeamService teamService;

	@Mock
	private FictiveTeamMapper teamMapper;

	@InjectMocks
	private FictiveTeamFacade teamFacade;

	private FictiveTeam team;

	private FictiveTeamDTO teamDTO;

	private FictiveTeamCreateDTO teamCreateDTO;

	private FictiveTeamUpdateDTO teamUpdateDTO;

	@BeforeEach
	void setUp() {
		team = new FictiveTeam();
		team.setGuid(teamId);
		team.setName("Avengers");

		teamDTO = new FictiveTeamDTO();
		teamDTO.setGuid(teamId);
		teamDTO.setName("Avengers");

		teamCreateDTO = new FictiveTeamCreateDTO();
		teamCreateDTO.setName("Avengers");

		teamUpdateDTO = new FictiveTeamUpdateDTO();
		teamUpdateDTO.setGuid(teamId);
		teamUpdateDTO.setName("Avengers Updated");
	}

	@Test
	void createFictiveTeam() throws ResourceAlreadyExistsException {
		when(teamMapper.toEntity(teamCreateDTO)).thenReturn(team);
		when(teamService.createTeam(team)).thenReturn(team);
		when(teamMapper.toDto(team)).thenReturn(teamDTO);

		FictiveTeamDTO result = teamFacade.createFictiveTeam(teamCreateDTO);

		assertThat(result).isEqualTo(teamDTO);
		verify(teamService).createTeam(team);
	}

	@Test
	void createTeam_throwsWhenExists() throws ResourceAlreadyExistsException {
		when(teamMapper.toEntity(teamCreateDTO)).thenReturn(team);
		when(teamService.createTeam(team)).thenThrow(new ResourceAlreadyExistsException("Exists"));

		assertThatThrownBy(() -> teamFacade.createFictiveTeam(teamCreateDTO))
			.isInstanceOf(ResourceAlreadyExistsException.class);
	}

	@Test
	void updateTeam() throws ResourceNotFoundException {
		when(teamMapper.toEntity(teamUpdateDTO)).thenReturn(team);
		when(teamService.updateTeam(team)).thenReturn(team);
		when(teamMapper.toDto(team)).thenReturn(teamDTO);

		FictiveTeamDTO result = teamFacade.updateFictiveTeam(teamUpdateDTO);

		assertThat(result).isEqualTo(teamDTO);
		verify(teamService).updateTeam(team);
	}

	@Test
	void deleteTeam() throws ResourceNotFoundException {
		teamFacade.deleteFictiveTeam(teamId);
		verify(teamService).deleteTeam(teamId);
	}

	@Test
	void findById() throws ResourceNotFoundException {
		when(teamService.findById(teamId)).thenReturn(team);
		when(teamMapper.toDto(team)).thenReturn(teamDTO);

		FictiveTeamDTO result = teamFacade.findById(teamId);

		assertThat(result).isEqualTo(teamDTO);
		verify(teamService).findById(teamId);
	}

	@Test
	void findAll() {
		when(teamService.findAll()).thenReturn(List.of(team));
		when(teamMapper.toDto(team)).thenReturn(teamDTO);

		List<FictiveTeamDTO> result = teamFacade.findAll();

		assertThat(result).containsExactly(teamDTO);
		verify(teamService).findAll();
	}

}
