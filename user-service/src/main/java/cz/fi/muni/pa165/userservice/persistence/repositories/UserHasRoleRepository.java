package cz.fi.muni.pa165.userservice.persistence.repositories;

import cz.fi.muni.pa165.userservice.persistence.entities.UserHasRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface UserHasRoleRepository extends JpaRepository<UserHasRole, UUID> {

	@Modifying
	@Query("DELETE FROM UserHasRole uhr WHERE uhr.user.guid = :userId AND uhr.role.guid = :roleId")
	void deleteByUserIdAndRoleId(@Param("userId") UUID userId, @Param("roleId") UUID roleId);

}
