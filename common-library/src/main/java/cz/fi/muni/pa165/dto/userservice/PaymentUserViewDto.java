package cz.fi.muni.pa165.dto.userservice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentUserViewDto {

	private UUID guid;

	private String username;

	private String mail;

	private Boolean isActive;

	private String name;

	private String surname;

}
