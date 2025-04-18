package cz.fi.muni.pa165.userservice.business.mappers;

import cz.fi.muni.pa165.dto.userService.UserCreateDto;
import cz.fi.muni.pa165.dto.userService.UserViewDto;
import cz.fi.muni.pa165.userservice.persistence.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = { RoleMapper.class }, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

	@Mapping(source = "roles", target = "roles")
	UserViewDto userToUserViewDto(User user);

	@Mapping(source = "password", target = "passwordHash")
	User userCreateDtoToUser(UserCreateDto userCreateDto);

	User userViewDtoToUser(UserViewDto userViewDto);

}
