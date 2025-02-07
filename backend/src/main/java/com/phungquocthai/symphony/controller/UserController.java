package com.phungquocthai.symphony.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.phungquocthai.symphony.dto.ApiResponse;
import com.phungquocthai.symphony.dto.UserDTO;
import com.phungquocthai.symphony.service.UserService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
	
	@Autowired
	private UserService userService;

	public ResponseEntity<UserDTO> create(UserDTO dto) {
		// TODO Auto-generated method stub
		return null;
	}

	public ResponseEntity<Void> delete() {
		// TODO Auto-generated method stub
		return null;
	}

	public ResponseEntity<UserDTO> update(UserDTO dto) {
		// TODO Auto-generated method stub
		return null;
	}

	public ResponseEntity<UserDTO> findById() {
		// TODO Auto-generated method stub
		return null;
	}

	@GetMapping("/users")
	public ResponseEntity<ApiResponse<List<UserDTO>>> findAll() {
//		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//		log.info(authentication.getName());
//		authentication.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));
		List<UserDTO> users = userService.getUsers();
		ApiResponse<List<UserDTO>> apiResponse = new ApiResponse<List<UserDTO>>();
		apiResponse.setResult(users);
		return ResponseEntity.ok(apiResponse);
	}

	
}
