package cz.fi.muni.pa165.gameservice.business.services;

import cz.fi.muni.pa165.gameservice.api.exception.ResourceNotFoundException;
import cz.fi.muni.pa165.gameservice.api.exception.ValidationHelper;
import cz.fi.muni.pa165.gameservice.api.exception.ValueIsMissingException;
import cz.fi.muni.pa165.gameservice.persistence.entities.Competition;
import cz.fi.muni.pa165.gameservice.persistence.repositories.CompetitionRepository;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class CompetitionService {

	private final CompetitionRepository competitionRepository;

	@Autowired
	public CompetitionService(CompetitionRepository competitionRepository) {
		this.competitionRepository = competitionRepository;
	}

	public Competition saveCompetition(Competition newCompetition) {
		ValidationHelper.requireNonNull(newCompetition, "New competition cannot be null");
		validateDateRange(newCompetition);

		return competitionRepository.save(newCompetition);
	}

	public Competition getCompetition(UUID competitionUUID) {
		ValidationHelper.requireNonNull(competitionUUID, "You must provide valid UUID");

		final var optional = competitionRepository.getCompetitionByGuid(competitionUUID);
		return optional.orElseThrow(() -> new ResourceNotFoundException(
				"Competition with guid %s was not found!".formatted(competitionUUID)));
	}

	public Competition updateCompetition(Competition competition) {
		ValidationHelper.requireNonNull(competition, "You must provide competition for update");
		ValidationHelper.requireNonNull(competition.getGuid(), "Competition's guid cannot be empty");
		validateDateRange(competition);

		return competitionRepository.save(competition);
	}

	/**
	 * Checks if the given competition is already running. It means if the current date is
	 * after or equals the start date. It doesn't care about the end date.
	 * @param competition competition to check
	 * @return true if the competition was already started, false in other case
	 */
	public boolean isStarted(Competition competition) {
		var currentDate = LocalDate.now();
		var startDate = competition.getStartAt();

		return currentDate.isAfter(startDate) || currentDate.equals(startDate);
	}

	/**
	 * Validates if endDate is before startDate
	 * @param competition competition to be validated
	 */
	private void validateDateRange(Competition competition) throws ValueIsMissingException, ValidationException {
		final var startAt = competition.getStartAt();
		final var endAt = competition.getEndAt();

		if (endAt == null) {
			return;
		}

		if (startAt == null) {
			throw new ValueIsMissingException("Start or was not provided");
		}

		if (!endAt.isAfter(startAt)) {
			throw new ValidationException("Start at must be later than end at!");
		}
	}

}
