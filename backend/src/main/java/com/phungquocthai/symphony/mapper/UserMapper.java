package com.phungquocthai.symphony.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.phungquocthai.symphony.dto.UserDTO;
import com.phungquocthai.symphony.dto.UserPasswordChangeDTO;
import com.phungquocthai.symphony.dto.UserUpdateDTO;
import com.phungquocthai.symphony.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper extends MapperInterface<User, UserDTO> {
	void updateEntity(@MappingTarget User entity, UserUpdateDTO dto);
	void updateEntity(@MappingTarget User entity, UserPasswordChangeDTO dto);
}
