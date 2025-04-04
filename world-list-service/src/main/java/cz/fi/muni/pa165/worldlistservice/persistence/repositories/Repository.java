package cz.fi.muni.pa165.worldlistservice.persistence.repositories;

import cz.fi.muni.pa165.dto.worldlistservice.interfaces.Identifiable;
import cz.fi.muni.pa165.worldlistservice.persistence.entities.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface Repository<T extends BaseEntity & Identifiable> extends JpaRepository<T, UUID> {

}
