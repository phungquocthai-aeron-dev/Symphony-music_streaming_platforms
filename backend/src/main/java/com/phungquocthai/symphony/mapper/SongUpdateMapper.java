package com.phungquocthai.symphony.mapper;

import org.mapstruct.Mapper;

import com.phungquocthai.symphony.dto.SongUpdateDTO;
import com.phungquocthai.symphony.entity.Song;

@Mapper(componentModel = "spring")
public interface SongUpdateMapper extends MapperInterface<Song, SongUpdateDTO> {

}
