package com.phungquocthai.symphony.mapper;

import org.mapstruct.Mapper;
import com.phungquocthai.symphony.dto.UserRegistrationDTO;
import com.phungquocthai.symphony.entity.User;

@Mapper(componentModel = "spring")
public interface UserRegistrationMapper extends MapperInterface<User, UserRegistrationDTO> {

}
