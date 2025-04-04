package cz.fi.muni.pa165.gameservice.persistence.repositories;

import cz.fi.muni.pa165.gameservice.persistence.entities.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ResultRepository extends JpaRepository<Result, UUID> {

}
