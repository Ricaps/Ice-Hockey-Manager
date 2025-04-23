package cz.fi.muni.pa165.gameservice.testdata;

import cz.fi.muni.pa165.dto.gameservice.CompetitionCreateDto;
import cz.fi.muni.pa165.dto.gameservice.CompetitionViewDto;
import cz.fi.muni.pa165.gameservice.persistence.entities.Competition;

import java.time.LocalDate;
import java.util.UUID;

public class CompetitionTestData {

	public static CompetitionViewDto getCompetitionView() {
		var competitionView = new CompetitionViewDto();
		competitionView.setGuid(UUID.randomUUID());
		competitionView.setName("Test competition");
		competitionView.setStartAt(LocalDate.now());
		competitionView.setEndAt(LocalDate.now().plusDays(10));

		return competitionView;
	}

	public static Competition getCompetitionEntity() {
		var competitionView = new Competition();
		competitionView.setGuid(UUID.randomUUID());
		competitionView.setName("Test competition");
		competitionView.setStartAt(LocalDate.now());
		competitionView.setEndAt(LocalDate.now().plusDays(10));

		return competitionView;
	}

	public static CompetitionCreateDto getCompetitionCreateDto() {
		var competitionCreate = new CompetitionCreateDto();
		competitionCreate.setName("Test Competition");
		competitionCreate.setStartAt(LocalDate.now());
		competitionCreate.setEndAt(LocalDate.now().plusDays(10));

		return competitionCreate;
	}

}
