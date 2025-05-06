package cz.fi.muni.pa165.gameservice.testdata;

import cz.fi.muni.pa165.dto.gameservice.ArenaCreateDto;
import cz.fi.muni.pa165.gameservice.persistence.entities.Arena;

public class ArenaTestData {

	public static ArenaCreateDto getArenaForCreate() {
		return ArenaCreateDto.builder().arenaName("O2").cityName("Prague").countryCode("CZE").build();
	}

	public static Arena getArena() {
		return Arena.builder().arenaName("O2").cityName("Prague").countryCode("CZE").build();
	}

}
