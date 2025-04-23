package cz.fi.muni.pa165.userservice.persistence.seeds;

import cz.fi.muni.pa165.userservice.business.services.UserService;
import cz.fi.muni.pa165.userservice.persistence.entities.BudgetOfferPackage;
import cz.fi.muni.pa165.userservice.persistence.entities.Payment;
import cz.fi.muni.pa165.userservice.persistence.entities.Role;
import cz.fi.muni.pa165.userservice.persistence.entities.User;
import cz.fi.muni.pa165.userservice.persistence.entities.UserHasRole;
import cz.fi.muni.pa165.userservice.persistence.repositories.BudgetOfferPackageRepository;
import cz.fi.muni.pa165.userservice.persistence.repositories.PaymentRepository;
import cz.fi.muni.pa165.userservice.persistence.repositories.RoleRepository;
import cz.fi.muni.pa165.userservice.persistence.repositories.UserHasRoleRepository;
import cz.fi.muni.pa165.userservice.persistence.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

@Component
@Slf4j
public class UserServiceSeeder {

	private final BudgetOfferPackageRepository budgetOfferPackageRepository;

	private final PaymentRepository paymentRepository;

	private final RoleRepository roleRepository;

	private final UserHasRoleRepository userHasRoleRepository;

	private final UserRepository userRepository;

	private final UserService userService;

	private final Faker faker = new Faker(new Random(233343L));

	private final Map<UUID, String> userPasswords = new HashMap<>();

	@Autowired
	public UserServiceSeeder(BudgetOfferPackageRepository budgetOfferPackageRepository,
			PaymentRepository paymentRepository, RoleRepository roleRepository, UserRepository userRepository,
			UserService userService, UserHasRoleRepository userHasRoleRepository) {
		this.budgetOfferPackageRepository = budgetOfferPackageRepository;
		this.paymentRepository = paymentRepository;
		this.roleRepository = roleRepository;
		this.userRepository = userRepository;
		this.userService = userService;
		this.userHasRoleRepository = userHasRoleRepository;

	}

	public void seedTestData(int roleCount, int userCount) {
		log.info("Database seeding started...");

		seedRoles(roleCount);
		seedBudgetOfferPackages();
		seedUsers(userCount);
		seedUserRoles();
		seedPayments();

		log.info("Database seeding ended!");
	}

	@Transactional
	public void clearDatabase() {
		log.info("Database clear started...");

		paymentRepository.deleteAll();
		userHasRoleRepository.deleteAll();
		userRepository.deleteAll();
		roleRepository.deleteAll();
		budgetOfferPackageRepository.deleteAll();

		log.info("Database seeding finished!");
	}

	private void seedRoles(int rolesCount) {
		log.info("Seeding roles...");

		if (roleRepository.count() != 0) {
			log.info("Seed roles were found, skipping role seeding.");
			return;
		}

		for (int i = 0; i < rolesCount; i++) {
			Role role = new Role();
			role.setName(faker.job().title());
			role.setCode(role.getName().replaceAll("[AEIOUYaeiouy ]", ""));
			role.setDescription(faker.lorem().sentence());

			roleRepository.save(role);
		}

		log.info("Roles seeded!");
	}

	private void seedBudgetOfferPackages() {
		log.info("Seeding budget offer packages...");

		if (budgetOfferPackageRepository.count() != 0) {
			log.info("Seed budget offer package was found, skipping budget offer packages seeding.");
			return;
		}

		boolean available = true;
		Integer[] prices = { 1, 1, 5, 2, 10, 10, 20, 25, 100, 200 };
		Random random = new Random(12L);

		for (Integer price : prices) {
			BudgetOfferPackage budgetOfferPackage = new BudgetOfferPackage();
			budgetOfferPackage.setIsAvailable(available);
			budgetOfferPackage.setDescription(faker.lorem().sentence());
			budgetOfferPackage.setPriceDollars(price);
			budgetOfferPackage.setBudgetIncrease((int) (price * 1000000 * random.nextDouble(0.8, 1.5)));
			budgetOfferPackageRepository.save(budgetOfferPackage);

			available = !available;
		}

		log.info("Budget offer packages seeded!");
	}

	private void seedUsers(int userCount) {
		log.info("Seeding users...");

		if (userRepository.count() != 0) {
			log.info("Seed users were found, skipping user seeding.");
			return;
		}

		for (int i = 0; i < userCount; i++) {
			User user = new User();
			user.setUsername(faker.internet().username());
			user.setMail(faker.internet().emailAddress());
			user.setName(faker.name().firstName());
			user.setSurname(faker.name().lastName());
			user.setIsActive(true);
			user.setBirthDate(faker.timeAndDate().birthday());
			String password = faker.internet().password();
			user.setPasswordHash(userService.encodePassword(password));

			if (i % 10 == 0) {
				user.setIsActive(false);
				user.setDeletedAt(LocalDateTime.now().minusDays(faker.random().nextInt(1, 50)));
			}

			User storedUser = userRepository.save(user);
			userPasswords.put(storedUser.getGuid(), password);
		}

		log.info("Users seeded!");
	}

	private void seedUserRoles() {
		log.info("Seeding user roles...");

		if (userHasRoleRepository.count() != 0) {
			log.info("Seed user roles were found, skipping user roles seeding.");
			return;
		}

		List<User> users = userRepository.findAll();
		List<Role> roles = roleRepository.findAll();

		for (User user : users) {
			int rolesToAssign = faker.random().nextInt(1, roles.size());
			Set<Role> assignedRoles = new HashSet<>();

			while (assignedRoles.size() < rolesToAssign) {
				Role randomRole = roles.get(faker.random().nextInt(roles.size()));
				if (assignedRoles.add(randomRole)) {
					UserHasRole userHasRole = new UserHasRole();
					userHasRole.setUser(user);
					userHasRole.setRole(randomRole);
					userHasRoleRepository.save(userHasRole);
				}
			}
		}

		log.info("User roles seeded!");
	}

	private void seedPayments() {
		log.info("Seeding payments...");

		if (paymentRepository.count() != 0) {
			log.info("Seed payments were found, skipping payments seeding.");
			return;
		}

		List<User> users = userRepository.findAll();
		List<BudgetOfferPackage> packages = budgetOfferPackageRepository.findAll();

		for (User user : users) {
			int paymentsCount = faker.random().nextInt(0, 5);
			for (int i = 0; i < paymentsCount; i++) {
				BudgetOfferPackage offer = packages.get(faker.random().nextInt(packages.size()));

				Payment payment = new Payment();
				payment.setUser(user);
				payment.setBudgetOfferPackage(offer);
				payment.setPaid(faker.random().nextBoolean());
				payment.setCreatedAt(LocalDateTime.now().minusDays(faker.random().nextInt(1, 100)));

				paymentRepository.save(payment);
			}
		}

		log.info("Payments seeded!");
	}

	public String getUserPasswordById(UUID guid) {
		return userPasswords.get(guid);
	}

}
