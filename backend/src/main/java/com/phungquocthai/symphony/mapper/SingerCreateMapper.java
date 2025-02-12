package com.phungquocthai.symphony.mapper;

import org.mapstruct.Mapper;

import com.phungquocthai.symphony.dto.SingerCreateDTO;
import com.phungquocthai.symphony.entity.Singer;

@Mapper(componentModel = "spring")
public interface SingerCreateMapper extends MapperInterface<Singer, SingerCreateDTO> {

}
