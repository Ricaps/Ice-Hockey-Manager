package cz.fi.muni.pa165.userservice.integration.api.controllers;

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
import net.datafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

@Component
class TestDataFactory implements CommandLineRunner {

	private static final Logger LOGGER = LoggerFactory.getLogger(TestDataFactory.class);

	private final BudgetOfferPackageRepository budgetOfferPackageRepository;

	private final PaymentRepository paymentRepository;

	private final RoleRepository roleRepository;

	private final UserHasRoleRepository userHasRoleRepository;

	private final UserRepository userRepository;

	private final UserService userService;

	private final Faker faker = new Faker(new Random(233343L));

	private final Map<UUID, String> userPasswords = new HashMap<>();

	@Value("${server.database.seedTestData:true}")
	private boolean shouldSeed;

	@Autowired
	public TestDataFactory(BudgetOfferPackageRepository budgetOfferPackageRepository,
			PaymentRepository paymentRepository, RoleRepository roleRepository,
			UserHasRoleRepository userHasRoleRepository, UserRepository userRepository, UserService userService) {
		this.budgetOfferPackageRepository = budgetOfferPackageRepository;
		this.paymentRepository = paymentRepository;
		this.roleRepository = roleRepository;
		this.userHasRoleRepository = userHasRoleRepository;
		this.userRepository = userRepository;
		this.userService = userService;
	}

	@Override
	public void run(String... args) {
		if (!shouldSeed) {
			LOGGER.info("Seeding test data is turned off. No data were seeded.");
			return;
		}

		seedTestData();
	}

	public void seedTestData() {
		LOGGER.info("Database seeding started...");

		seedRoles();
		seedBudgetOfferPackages();
		seedUsers();
		seedUserRoles();
		seedPayments();

		LOGGER.info("Database seeding ended!");
	}

	private void seedRoles() {
		LOGGER.info("Seeding roles...");

		if (roleRepository.count() != 0) {
			LOGGER.info("Seed roles were found, skipping role seeding.");
			return;
		}

		for (int i = 0; i < 5; i++) {
			Role role = new Role();
			role.setName(faker.job().title());
			role.setCode(role.getName().replaceAll("[AEIOUYaeiouy ]", ""));
			role.setDescription(faker.lorem().sentence());

			roleRepository.save(role);
		}

		LOGGER.info("Roles seeded!");
	}

	private void seedBudgetOfferPackages() {
		LOGGER.info("Seeding budget offer packages...");

		if (budgetOfferPackageRepository.count() != 0) {
			LOGGER.info("Seed budget offer package was found, skipping budget offer packages seeding.");
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

		LOGGER.info("Budget offer packages seeded!");
	}

	private void seedUsers() {
		LOGGER.info("Seeding users...");

		if (userRepository.count() != 0) {
			LOGGER.info("Seed users were found, skipping user seeding.");
			return;
		}

		for (int i = 0; i < 50; i++) {
			User user = new User();
			user.setUsername(faker.internet().username());
			user.setMail(faker.internet().emailAddress());
			user.setName(faker.name().firstName());
			user.setSurname(faker.name().lastName());
			user.setIsActive(true);
			user.setBirthDate(faker.timeAndDate().birthday());
			String password = faker.internet().password();
			user.setPasswordHash(userService.encodePassword(password));

			if (i == 10 || i == 20) {
				user.setIsActive(false);
				user.setDeletedAt(LocalDateTime.now().minusDays(faker.random().nextInt(1, 50)));
			}

			User storedUser = userRepository.save(user);
			userPasswords.put(storedUser.getGuid(), password);
		}

		LOGGER.info("Users seeded!");
	}

	private void seedUserRoles() {
		LOGGER.info("Seeding user roles...");

		if (userHasRoleRepository.count() != 0) {
			LOGGER.info("Seed user roles were found, skipping user roles seeding.");
			return;
		}

		List<User> users = userRepository.findAll();
		List<Role> roles = roleRepository.findAll();

		for (User user : users) {
			int rolesToAssign = faker.random().nextInt(1, 3);
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

		LOGGER.info("User roles seeded!");
	}

	private void seedPayments() {
		LOGGER.info("Seeding payments...");

		if (paymentRepository.count() != 0) {
			LOGGER.info("Seed payments were found, skipping payments seeding.");
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
				payment.setPaid(true);
				payment.setCreatedAt(LocalDateTime.now().minusDays(faker.random().nextInt(1, 100)));

				paymentRepository.save(payment);
			}
		}

		LOGGER.info("Payments seeded!");
	}

	public String getUserPassword(UUID guid) {
		return userPasswords.get(guid);
	}

}
