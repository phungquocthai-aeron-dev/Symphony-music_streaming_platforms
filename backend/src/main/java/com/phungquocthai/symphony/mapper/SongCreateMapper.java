package com.phungquocthai.symphony.mapper;

import org.mapstruct.Mapper;

import com.phungquocthai.symphony.dto.SongCreateDTO;
import com.phungquocthai.symphony.entity.Song;

@Mapper(componentModel = "spring")
public interface SongCreateMapper extends MapperInterface<Song, SongCreateDTO> {

}
