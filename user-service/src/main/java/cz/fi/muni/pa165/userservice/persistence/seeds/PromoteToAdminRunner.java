package cz.fi.muni.pa165.userservice.persistence.seeds;

import cz.fi.muni.pa165.userservice.persistence.entities.User;
import cz.fi.muni.pa165.userservice.persistence.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
public class PromoteToAdminRunner implements CommandLineRunner {

	private final UserRepository userRepository;

	@Value("${user-service.promote-to-admin-id:}")
	private String guidToPromote;

	@Autowired
	public PromoteToAdminRunner(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public void run(String... args) throws Exception {
		if (guidToPromote.isBlank()) {
			log.info("Promote to admin id is blank");
			return;
		}

		UUID id;
		try {
			id = UUID.fromString(guidToPromote);
		}
		catch (IllegalArgumentException e) {
			log.info("Promote to admin: id '%s' is invalid".formatted(guidToPromote));
			return;
		}

		Optional<User> user = userRepository.findById(id);
		if (!user.isPresent()) {
			log.info("Promote to admin: user with ID: '%s' not found".formatted(id));
			return;
		}

		user.get().setIsAdmin(true);
		userRepository.save(user.get());
		log.info("User with ID: '%s' promoted to admin.".formatted(id));
	}

}
