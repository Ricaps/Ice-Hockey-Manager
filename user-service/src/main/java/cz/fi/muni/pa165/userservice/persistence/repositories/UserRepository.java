package cz.fi.muni.pa165.userservice.persistence.repositories;

import cz.fi.muni.pa165.userservice.persistence.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

	@Query("SELECT u FROM User u WHERE u.mail = :mail")
	Optional<User> findByMail(@Param("mail") String mail);

	@Query("SELECT u FROM User u WHERE u.username = :username")
	Optional<User> findByUsername(@Param("username") String username);

	@Query("SELECT u.isAdmin FROM User u WHERE u.guid = :id")
	Optional<Boolean> findIsAdminByGuid(@Param("id") UUID id);

	@Query("SELECT u.isAdmin FROM User u WHERE u.mail = :mail")
	Optional<Boolean> findIsAdminByMail(@Param("mail") String mail);

	@Modifying
	@Query("UPDATE User u SET u.isAdmin = :isAdmin WHERE u.guid = :guid")
	void updateIsAdminByGuid(@Param("guid") UUID guid, @Param("isAdmin") boolean isAdmin);

}
