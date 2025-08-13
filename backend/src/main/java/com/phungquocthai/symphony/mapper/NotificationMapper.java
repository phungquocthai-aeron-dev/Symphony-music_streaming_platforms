package com.phungquocthai.symphony.mapper;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.phungquocthai.symphony.dto.NotificationDTO;
import com.phungquocthai.symphony.entity.Notification;
import com.phungquocthai.symphony.entity.User;

@Mapper(componentModel = "spring")
public interface NotificationMapper extends MapperInterface<Notification, NotificationDTO> {

    @Mapping(source = "user.userId", target = "senderId")
    @Mapping(source = "users", target = "recipientIds")
    NotificationDTO toDTO(Notification entity);

    void updateEntity(@MappingTarget Notification entity, NotificationDTO dto);

    default List<Integer> mapUsersToIds(Set<User> users) {
        if (users == null) return null;
        return users.stream().map(User::getUserId).collect(Collectors.toList());
    }
}
