package cz.fi.muni.pa165.userservice.integration.api.controllers;

import com.jayway.jsonpath.JsonPath;
import cz.fi.muni.pa165.userservice.persistence.entities.Identifiable;
import org.junit.jupiter.api.Assertions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;

public class BaseControllerIT<R extends JpaRepository<E, UUID>, E extends Identifiable> {

	protected R repository;

	public BaseControllerIT(R repository) {
		this.repository = repository;
	}

	protected UUID getNonExistingId() {
		return getNonExistingEntityId(repository);
	}

	protected E getExistingEntity() {
		return getExistingEntity(1);
	}

	protected E getExistingEntity(int index) {
		return repository.findAll().get(index);
	}

	protected List<E> getExistingEntities() {
		return repository.findAll();
	}

	protected <A> List<A> getMappedEntities(Function<E, A> mapper) {
		return repository.findAll().stream().map(mapper).toList();
	}

	protected E getFirstFilteredEntity(Predicate<E> predicate) {
		return repository.findAll().stream().filter(predicate).findFirst().orElseThrow();
	}

	protected List<E> getFilteredEntities(Predicate<E> predicate) {
		return repository.findAll().stream().filter(predicate).toList();
	}

	protected static <E extends Identifiable, R extends JpaRepository<E, UUID>> E getExistingEntity(R repository) {
		return repository.findAll().getFirst();
	}

	protected static <E extends Identifiable, R extends JpaRepository<E, UUID>> UUID getNonExistingEntityId(
			R repository) {
		var entities = new HashSet<UUID>(repository.findAll().stream().map(Identifiable::getGuid).toList());
		UUID id = UUID.randomUUID();
		while (entities.contains(id)) {
			id = UUID.randomUUID();
		}

		return id;
	}

	protected void compareCreatedAt(MvcResult result, String propertyName, LocalDateTime expected) throws Exception {
		String date = JsonPath.read(result.getResponse().getContentAsString(), "$.%s".formatted(propertyName));
		LocalDateTime createdAt = LocalDateTime.parse(date);
		createdAt = createdAt.truncatedTo(ChronoUnit.SECONDS);
		Assertions.assertEquals(expected.truncatedTo(ChronoUnit.SECONDS), createdAt);
	}

}
