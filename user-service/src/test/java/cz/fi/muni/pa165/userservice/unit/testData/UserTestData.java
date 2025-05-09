package cz.fi.muni.pa165.userservice.unit.testData;

import cz.fi.muni.pa165.dto.userService.PaymentUserViewDto;
import cz.fi.muni.pa165.dto.userService.UserCreateDto;
import cz.fi.muni.pa165.dto.userService.UserUpdateDto;
import cz.fi.muni.pa165.dto.userService.UserViewDto;
import cz.fi.muni.pa165.userservice.persistence.entities.User;
import net.datafaker.Faker;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class UserTestData {

	private final static Faker faker = new Faker(new Random(233343L));

	public static User getUser() {
		return User.builder()
			.guid(UUID.randomUUID())
			.deletedAt(null)
			.isActive(true)
			.deletedAt(null)
			.mail("mail@test.cz")
			.name("Jakub")
			.surname("Miskar")
			.username("aodfihas")
			.birthDate(LocalDate.of(2000, 1, 1))
			.build();
	}

	public static UserCreateDto getUserCreateDto() {
		return UserCreateDto.builder()
			.username("usernameBro")
			.mail("test@mail.ok")
			.name("Petr")
			.surname("Klicenka")
			.birthDate(LocalDate.now())
			.build();
	}

	public static UserViewDto getUserViewDto() {
		return UserViewDto.builder()
			.guid(UUID.randomUUID())
			.username("usernameBro")
			.mail("test@mail.ok")
			.isActive(true)
			.deletedAt(null)
			.name("Petr")
			.surname("Klicenka")
			.birthDate(LocalDate.now())
			.payments(List.of(PaymentTestData.getUserPaymentDto()))
			.build();
	}

	public static PaymentUserViewDto getPaymentUserViewDto() {
		return PaymentUserViewDto.builder()
			.guid(UUID.randomUUID())
			.username("litttlePrincess")
			.mail("iam@little.princess")
			.isActive(true)
			.name("Sophie")
			.surname("sweet")
			.build();
	}

	public static UserUpdateDto getUserUpdateDto() {
		return UserUpdateDto.builder()
			.guid(UUID.randomUUID())
			.username("usernameBro")
			.name("Petr")
			.surname("Klicenka")
			.birthDate(LocalDate.now().minusDays(894))
			.build();
	}

	public static List<User> getListOfUsers(int count) {
		List<User> users = new ArrayList<>();

		for (int i = 0; i < count; i++) {
			User user = new User();
			user.setUsername(faker.internet().username());
			user.setMail(faker.internet().emailAddress());
			user.setName(faker.name().firstName());
			user.setSurname(faker.name().lastName());
			user.setIsActive(true);
			user.setBirthDate(faker.timeAndDate().birthday());
			user.setIsAdmin(i % 2 == 0);

			if (i == 10 || i == 20) {
				user.setIsActive(false);
				user.setDeletedAt(LocalDateTime.now().minusDays(faker.random().nextInt(1, 50)));
			}

			users.add(user);
		}

		return users;
	}

}
