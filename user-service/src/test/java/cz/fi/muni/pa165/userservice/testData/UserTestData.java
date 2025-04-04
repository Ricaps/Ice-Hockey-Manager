package cz.fi.muni.pa165.userservice.testData;

import cz.fi.muni.pa165.dto.userService.ChangePasswordRequestDto;
import cz.fi.muni.pa165.dto.userService.PaymentUserViewDto;
import cz.fi.muni.pa165.dto.userService.UserCreateDto;
import cz.fi.muni.pa165.dto.userService.UserViewDto;
import cz.fi.muni.pa165.userservice.persistence.entities.User;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class UserTestData {

	public static User getUser() {
		return User.builder()
			.guid(UUID.randomUUID())
			.deletedAt(null)
			.isActive(true)
			.deletedAt(null)
			.passwordHash("$2a$12$sqj05k2qPWFG3URhtNolXeWNvW/Gp8n9lizM5VL.7U5VBb64o/GOe")
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
			.password("@validPassword1")
			.name("Petr")
			.surname("Klicenka")
			.birthDate(LocalDate.now())
			.build();
	}

	public static UserViewDto getUserViewDto() {
		return UserViewDto.builder()
			.guid(UUID.randomUUID())
			.teamId(UUID.randomUUID())
			.username("usernameBro")
			.mail("test@mail.ok")
			.isActive(true)
			.deletedAt(null)
			.name("Petr")
			.surname("Klicenka")
			.birthDate(LocalDate.now())
			.roles(List.of(RoleTestData.getRoleViewDto()))
			.payments(List.of(PaymentTestData.getUserPaymentDto()))
			.build();
	}

	public static ChangePasswordRequestDto getChangePasswordRequestDto() {
		return new ChangePasswordRequestDto("oldPassword", "PSWs123@@", UUID.randomUUID());
	}

	public static PaymentUserViewDto getPaymentUserViewDto() {
		return PaymentUserViewDto.builder()
			.guid(UUID.randomUUID())
			.teamId(UUID.randomUUID())
			.username("litttlePrincess")
			.mail("iam@little.princess")
			.isActive(true)
			.name("Sophie")
			.surname("sweet")
			.build();
	}

}
