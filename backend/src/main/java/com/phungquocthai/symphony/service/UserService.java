package com.phungquocthai.symphony.service;

import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.phungquocthai.symphony.entity.Vip;
import com.phungquocthai.symphony.exception.AppException;
import com.phungquocthai.symphony.mapper.UserMapper;
import com.phungquocthai.symphony.mapper.UserRegistrationMapper;
import com.phungquocthai.symphony.repository.UserRepository;
import com.phungquocthai.symphony.repository.VipRepository;

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
	
	@Autowired
	VipRepository vipRepository;
	
	@Autowired
	ExcelExportUtil excelExportUtil;

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
	
//	@PreAuthorize("hasRole('ADMIN') or #dto.userId == authentication.name")
	public UserDTO update(UserUpdateDTO dto, MultipartFile avatarFile,
			String password, String password_confirm, String newPassword) {
		
		if(!password.equals(password_confirm)) return null;
		
		User user = userRepository.findById(dto.getId())
				.orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISRED));
		
		boolean authenticated = passwordEncoder.matches(password, user.getPassword());
		log.info(password);
		if(!authenticated) return null;
		
		

		if(avatarFile != null) {
			String avatar = fileStorageService.storeFile(avatarFile, PathStorage.AVATAR);
			user.setAvatar(avatar);
		}
		
		if(!newPassword.isBlank()) {
			user.setPassword(passwordEncoder.encode(newPassword));
		}
		
		user.setBirthday(dto.getBirthday());
		user.setPhone(dto.getPhone());
		user.setGender(dto.getGender());
		user.setFullName(dto.getFullName());
		
		log.info(user.toString());
		log.info("Info");
		
		userRepository.save(user);
		return userMapper.toDTO(user);
	}
	
	@PreAuthorize("hasRole('ADMIN') or #userId.toString() == authentication.name")
	public void delete(Integer userId) {
	    userRepository.deleteById(userId);
	}

	@PreAuthorize("hasRole('ADMIN')")
	public List<UserDTO> getUsers() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		log.info(authentication.getName());
		authentication.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));
		
		List<User> userEntities = userRepository.findAll();
		List<UserDTO> users = userMapper.toListDTO(userEntities);
		return users;
	}
	
//	@PostAuthorize("returnObject.userId == authentication.name")
	public UserDTO getUserById(Integer id) {
		User entity = userRepository.findById(id)
				.orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISRED));
		UserDTO user = userMapper.toDTO(entity);
		return user;
	}
	
	public UserDTO getUserBySingerId(Integer id) {
		User entity = userRepository.findBySingerId(id)
				.orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISRED));
		UserDTO user = userMapper.toDTO(entity);
		return user;
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	public byte[] exportToExcel() throws IOException {
        return excelExportUtil.exportToExcel(userMapper.toListDTO(userRepository.findAll()), null, "Danh sách người dùng");
    }
	
	public UserDTO findByPhone(String phone) {
		User user = userRepository.findByPhone(phone).orElseThrow();
		return userMapper.toDTO(user);
	}
	
	public List<Vip> getAllVipPakages() {
		return vipRepository.findAll();
	}
}
