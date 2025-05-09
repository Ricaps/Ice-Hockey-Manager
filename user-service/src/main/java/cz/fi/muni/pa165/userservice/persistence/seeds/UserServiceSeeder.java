package cz.fi.muni.pa165.userservice.persistence.seeds;

import cz.fi.muni.pa165.userservice.persistence.entities.BudgetOfferPackage;
import cz.fi.muni.pa165.userservice.persistence.entities.Payment;
import cz.fi.muni.pa165.userservice.persistence.entities.User;
import cz.fi.muni.pa165.userservice.persistence.repositories.BudgetOfferPackageRepository;
import cz.fi.muni.pa165.userservice.persistence.repositories.PaymentRepository;
import cz.fi.muni.pa165.userservice.persistence.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Component
@Slf4j
public class UserServiceSeeder {

	private final BudgetOfferPackageRepository budgetOfferPackageRepository;

	private final PaymentRepository paymentRepository;

	private final UserRepository userRepository;

	private final Faker faker = new Faker(new Random(233343L));

	private final Map<UUID, String> userPasswords = new HashMap<>();

	@Autowired
	public UserServiceSeeder(BudgetOfferPackageRepository budgetOfferPackageRepository,
			PaymentRepository paymentRepository, UserRepository userRepository) {
		this.budgetOfferPackageRepository = budgetOfferPackageRepository;
		this.paymentRepository = paymentRepository;
		this.userRepository = userRepository;
	}

	public void seedTestData(int userCount) {
		log.info("Database seeding started...");

		seedBudgetOfferPackages();
		seedUsers(userCount);
		seedPayments();

		log.info("Database seeding ended!");
	}

	@Transactional
	public void clearDatabase() {
		log.info("Database clear started...");

		paymentRepository.deleteAll();
		userRepository.deleteAll();
		budgetOfferPackageRepository.deleteAll();

		log.info("Database seeding finished!");
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
			user.setIsAdmin(i % 2 == 0);

			if (i % 10 == 0) {
				user.setIsActive(false);
				user.setDeletedAt(LocalDateTime.now().minusDays(faker.random().nextInt(1, 50)));
			}

			User storedUser = userRepository.save(user);
			userPasswords.put(storedUser.getGuid(), password);
		}

		log.info("Users seeded!");
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
