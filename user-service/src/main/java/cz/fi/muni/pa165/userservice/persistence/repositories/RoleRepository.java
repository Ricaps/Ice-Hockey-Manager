package cz.fi.muni.pa165.userservice.persistence.repositories;

import cz.fi.muni.pa165.userservice.persistence.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {

	@Query("SELECT r FROM Role r WHERE r.name = :name")
	Optional<Role> findByName(@Param("name") String name);

	@Query("SELECT r FROM Role r WHERE r.code = :code")
	Optional<Role> findByCode(@Param("code") String code);

}
