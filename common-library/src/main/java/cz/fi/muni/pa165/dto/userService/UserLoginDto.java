package cz.fi.muni.pa165.dto.userService;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class UserLoginDto {

	private String username;

	private String password;

}
