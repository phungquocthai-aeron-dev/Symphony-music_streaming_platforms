package com.phungquocthai.symphony.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.phungquocthai.symphony.constant.ErrorCode;
import com.phungquocthai.symphony.constant.PathStorage;
import com.phungquocthai.symphony.constant.Role;
import com.phungquocthai.symphony.dto.UserDTO;
import com.phungquocthai.symphony.dto.UserRegistrationDTO;
import com.phungquocthai.symphony.dto.UserUpdateDTO;
import com.phungquocthai.symphony.entity.User;
import com.phungquocthai.symphony.exception.AppException;
import com.phungquocthai.symphony.mapper.UserMapper;
import com.phungquocthai.symphony.mapper.UserRegistrationMapper;
import com.phungquocthai.symphony.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserService {
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	FileStorageService fileStorageService;
	
	@Autowired
	UserMapper userMapper;
	
	@Autowired
	UserRegistrationMapper userRegistrationMapper;
	
	@Autowired
	PasswordEncoder passwordEncoder;

	public UserDTO create(UserRegistrationDTO dto) {
		if(userRepository.existsByPhone(dto.getPhone())) {
			throw new AppException(ErrorCode.USER_EXISTED);
		}
		
		dto.setPassword(passwordEncoder.encode(dto.getPassword()));
		User user = userRegistrationMapper.toEntity(dto);
		user.setRole(Role.USER.getValue());
		user.setAvatar("/avatar/my_avatar.jpg");
		
		userRepository.save(user);
		
		UserDTO userDTO = userMapper.toDTO(user);
		
		return userDTO;
	}
	
	@PreAuthorize("hasRole('ADMIN') or #dto.userId == authentication.name")
	public UserDTO update(UserUpdateDTO dto, MultipartFile avatarFile) {
		User user = userRepository.findById(dto.getId())
				.orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISRED));
		if(avatarFile != null) {
			String avatar = fileStorageService.storeFile(avatarFile, PathStorage.AVATAR);
			user.setAvatar(avatar);
		}
		userMapper.updateEntity(user, dto);
		userRepository.save(user);
		return userMapper.toDTO(user);
	}
	
	@PreAuthorize("hasRole('ADMIN') or #dto.userId == authentication.name")
	public void delete(Integer userId) {
		userRepository.deleteById(userId);
	}

	@PreAuthorize("hasRole('ADMIN')")
	public List<UserDTO> getUsers() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		log.info(authentication.getName());
		authentication.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));
		
		List<User> userEntities = userRepository.findAll();
		List<UserDTO> users = userEntities.stream().map(userEntity -> new UserDTO(userEntity)).collect(Collectors.toList());
		return users;
	}
	
	@PostAuthorize("returnObject.userId == authentication.name")
	public UserDTO getUserById(Integer id) {
		return userMapper.toDTO(userRepository.findById(id)
				.orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISRED)));
	}
}
