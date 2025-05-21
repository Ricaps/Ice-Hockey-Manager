package cz.fi.muni.pa165.service.teamservice.api;

import cz.fi.muni.pa165.dto.teamservice.TeamCharacteristicCreateDTO;
import cz.fi.muni.pa165.dto.teamservice.TeamCharacteristicDTO;
import cz.fi.muni.pa165.dto.teamservice.TeamCharacteristicUpdateDTO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.*;

import java.util.List;
import java.util.UUID;

@HttpExchange("/team-characteristics")
public interface TeamCharacteristicController {

	@PostExchange
	TeamCharacteristicDTO createTeamCharacteristic(@RequestBody TeamCharacteristicCreateDTO createDTO);

	@PutExchange("/{id}")
	TeamCharacteristicDTO updateTeamCharacteristic(@PathVariable UUID id,
			@RequestBody TeamCharacteristicUpdateDTO updateDTO);

	@DeleteExchange("/{id}")
	void deleteTeamCharacteristic(@PathVariable UUID id);

	@GetExchange("/{id}")
	TeamCharacteristicDTO getTeamCharacteristic(@PathVariable UUID id);

	@GetExchange("/team/{teamId}")
	List<TeamCharacteristicDTO> findByTeamId(@PathVariable UUID teamId);

}
