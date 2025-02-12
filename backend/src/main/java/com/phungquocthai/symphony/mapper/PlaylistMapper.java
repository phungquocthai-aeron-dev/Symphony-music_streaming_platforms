package com.phungquocthai.symphony.mapper;

import org.mapstruct.Mapper;

import com.phungquocthai.symphony.dto.PlaylistDTO;
import com.phungquocthai.symphony.entity.Playlist;

@Mapper(componentModel = "spring")
public interface PlaylistMapper extends MapperInterface<Playlist, PlaylistDTO> {

}
