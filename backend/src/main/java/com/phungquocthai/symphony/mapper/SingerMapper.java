package com.phungquocthai.symphony.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.phungquocthai.symphony.dto.SingerDTO;
import com.phungquocthai.symphony.entity.Singer;

@Mapper(componentModel = "spring")
public interface SingerMapper extends MapperInterface<Singer, SingerDTO> {
	@Override
	@Mapping(target = "followers", ignore = true)
	Singer toEntity(SingerDTO dto);
}
