package com.phungquocthai.symphony.mapper;

import org.mapstruct.Mapper;
import com.phungquocthai.symphony.dto.UserDTO;
import com.phungquocthai.symphony.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper extends MapperInterface<User, UserDTO> {
	
}
