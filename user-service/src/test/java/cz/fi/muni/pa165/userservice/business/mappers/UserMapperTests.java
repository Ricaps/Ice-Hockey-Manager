package cz.fi.muni.pa165.userservice.business.mappers;

import cz.fi.muni.pa165.dto.userService.RoleViewDto;
import cz.fi.muni.pa165.dto.userService.UserCreateDto;
import cz.fi.muni.pa165.dto.userService.UserPaymentDto;
import cz.fi.muni.pa165.dto.userService.UserViewDto;
import cz.fi.muni.pa165.userservice.persistence.entities.Payment;
import cz.fi.muni.pa165.userservice.persistence.entities.User;
import cz.fi.muni.pa165.userservice.persistence.entities.UserHasRole;
import cz.fi.muni.pa165.userservice.testData.PaymentTestData;
import cz.fi.muni.pa165.userservice.testData.RoleTestData;
import cz.fi.muni.pa165.userservice.testData.UserTestData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class UserMapperTests {

	@Autowired
	private UserMapper mapper;

	@Test
	public void mapUserToUserViewDto() {
		// Arrange
		User user = UserTestData.getUser();
		user.setRoles(new HashSet<>(List.of(new UserHasRole(UUID.randomUUID(), user, RoleTestData.getRole()))));
		user.setPayments(new HashSet<>(List.of(PaymentTestData.getPayment())));

		// Act
		UserViewDto userViewDto = mapper.userToUserViewDto(user);

		// Assert
		equalsUserAndUserViewDto(user, userViewDto);
	}

	@Test
	public void mapUserViewDtoToUser() {
		// Arrange
		UserViewDto dto = UserTestData.getUserViewDto();

		// Act
		User user = mapper.userViewDtoToUser(dto);

		// Assert
		equalsUserAndUserViewDto(user, dto);
	}

	@Test
	public void mapUserCreateDtoToUser() {
		// Arrange
		UserCreateDto userCreateDto = UserTestData.getUserCreateDto();

		// Act
		User user = mapper.userCreateDtoToUser(userCreateDto);

		// Assert
		assertEquals(userCreateDto.getBirthDate(), user.getBirthDate());
		assertEquals(userCreateDto.getName(), user.getName());
		assertEquals(userCreateDto.getMail(), user.getMail());
		assertEquals(userCreateDto.getSurname(), user.getSurname());
		assertEquals(userCreateDto.getUsername(), user.getUsername());
		assertEquals(userCreateDto.getPassword(), user.getPasswordHash());
	}

	private void equalsUserAndUserViewDto(User user, UserViewDto userViewDto) {

		assertEquals(user.getTeamId(), userViewDto.getTeamId());
		assertEquals(user.getUsername(), userViewDto.getUsername());
		assertEquals(user.getSurname(), userViewDto.getSurname());
		assertEquals(user.getMail(), userViewDto.getMail());
		assertEquals(user.getIsActive(), userViewDto.getIsActive());
		assertEquals(user.getDeletedAt(), userViewDto.getDeletedAt());
		assertEquals(user.getGuid(), userViewDto.getGuid());
		assertEquals(user.getName(), userViewDto.getName());
		assertEquals(user.getBirthDate(), userViewDto.getBirthDate());

		assertEquals(user.getRoles().size(), userViewDto.getRoles().size());
		List<UserHasRole> userRoles = user.getRoles()
			.stream()
			.sorted(Comparator.comparing(UserHasRole::getGuid))
			.toList();
		List<RoleViewDto> dtoRoles = userViewDto.getRoles()
			.stream()
			.sorted(Comparator.comparing(RoleViewDto::getGuid))
			.toList();

		for (int i = 0; i < userRoles.size(); i++) {
			assertEquals(userRoles.get(i).getRole().getGuid(), dtoRoles.get(i).getGuid());
			assertEquals(userRoles.get(i).getRole().getName(), dtoRoles.get(i).getName());
			assertEquals(userRoles.get(i).getRole().getDescription(), dtoRoles.get(i).getDescription());
			assertEquals(userRoles.get(i).getRole().getCode(), dtoRoles.get(i).getCode());
		}

		assertEquals(user.getPayments().size(), userViewDto.getPayments().size());
		List<Payment> userPayments = user.getPayments()
			.stream()
			.sorted(Comparator.comparing(Payment::getGuid))
			.toList();
		List<UserPaymentDto> dtoPayments = userViewDto.getPayments()
			.stream()
			.sorted(Comparator.comparing(UserPaymentDto::getGuid))
			.toList();

		for (int i = 0; i < userPayments.size(); i++) {
			assertEquals(userPayments.get(i).getGuid(), dtoPayments.get(i).getGuid());
			assertEquals(userPayments.get(i).getPaid(), dtoPayments.get(i).getPaid());
			assertEquals(userPayments.get(i).getCreatedAt(), dtoPayments.get(i).getCreatedAt());

			assertEquals(userPayments.get(i).getBudgetOfferPackage().getGuid(),
					dtoPayments.get(i).getBudgetOfferPackage().getGuid());
			assertEquals(userPayments.get(i).getBudgetOfferPackage().getPriceDollars(),
					dtoPayments.get(i).getBudgetOfferPackage().getPriceDollars());
			assertEquals(userPayments.get(i).getBudgetOfferPackage().getBudgetIncrease(),
					dtoPayments.get(i).getBudgetOfferPackage().getBudgetIncrease());
			assertEquals(userPayments.get(i).getBudgetOfferPackage().getIsAvailable(),
					dtoPayments.get(i).getBudgetOfferPackage().getIsAvailable());
			assertEquals(userPayments.get(i).getBudgetOfferPackage().getDescription(),
					dtoPayments.get(i).getBudgetOfferPackage().getDescription());
		}
	}

}
