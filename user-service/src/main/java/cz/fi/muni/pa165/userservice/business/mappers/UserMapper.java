package cz.fi.muni.pa165.userservice.business.mappers;

import cz.fi.muni.pa165.dto.userservice.UserCreateDto;
import cz.fi.muni.pa165.dto.userservice.UserUpdateDto;
import cz.fi.muni.pa165.dto.userservice.UserViewDto;
import cz.fi.muni.pa165.userservice.persistence.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

	UserViewDto userToUserViewDto(User user);

	User userCreateDtoToUser(UserCreateDto userCreateDto);

	User userViewDtoToUser(UserViewDto userViewDto);

	User userUpdateDtoToUser(UserUpdateDto userUpdateDto);

}
