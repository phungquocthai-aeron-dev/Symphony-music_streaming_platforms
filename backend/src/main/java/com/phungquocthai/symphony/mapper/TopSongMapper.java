package com.phungquocthai.symphony.mapper;

import org.mapstruct.Mapper;
import com.phungquocthai.symphony.dto.TopSongDTO;
import com.phungquocthai.symphony.entity.Song;

@Mapper(componentModel = "spring")
public interface TopSongMapper extends MapperInterface<Song, TopSongDTO> {

}
