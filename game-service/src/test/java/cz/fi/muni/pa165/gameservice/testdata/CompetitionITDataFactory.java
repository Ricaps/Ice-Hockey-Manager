package cz.fi.muni.pa165.gameservice.testdata;

import cz.fi.muni.pa165.gameservice.persistence.entities.Competition;
import cz.fi.muni.pa165.gameservice.persistence.repositories.CompetitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class CompetitionITDataFactory {

	private final CompetitionRepository competitionRepository;

	@Autowired
	public CompetitionITDataFactory(CompetitionRepository competitionRepository) {
		this.competitionRepository = competitionRepository;
	}

	public List<Competition> getCompetitionsWithTeams() {
		return competitionRepository.findAll()
			.stream()
			.filter(competition -> competition.getTeams() != null && !competition.getTeams().isEmpty())
			.toList();
	}

	public Competition getCompetitionWithTeams() {
		return getCompetitionsWithTeams().getFirst();
	}

	public Competition getCompetitionWithoutTeams() {
		return competitionRepository.findAll()
			.stream()
			.filter(competition -> competition.getTeams() == null || competition.getTeams().isEmpty())
			.findAny()
			.orElseThrow();
	}

	public Competition getCompetitionWithMatches() {
		return competitionRepository.findAll()
			.stream()
			.filter(competition -> competition.getMatches() != null && !competition.getMatches().isEmpty())
			.findAny()
			.orElseThrow();
	}

	public Competition getCompetitionWithoutMatches() {
		return getCompetitionsWithTeams().stream()
			.filter(competition -> competition.getMatches() == null || competition.getMatches().isEmpty())
			.findAny()
			.orElseThrow();
	}

	public Competition getRunningCompetition() {
		var currentDate = LocalDate.now();
		return competitionRepository.findAll()
			.stream()
			.filter(competition -> currentDate.isAfter(competition.getStartAt())
					&& currentDate.isBefore(competition.getEndAt()))
			.findAny()
			.orElseThrow();
	}

}
