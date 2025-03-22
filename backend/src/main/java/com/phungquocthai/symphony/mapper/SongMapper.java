package com.phungquocthai.symphony.mapper;

import org.mapstruct.Mapper;

import com.phungquocthai.symphony.dto.SongDTO;
import com.phungquocthai.symphony.entity.Song;

@Mapper(componentModel = "spring")
public interface SongMapper extends MapperInterface<Song, SongDTO> {

}