package com.phungquocthai.symphony.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.phungquocthai.symphony.dto.ApiResponse;
import com.phungquocthai.symphony.dto.UserDTO;
import com.phungquocthai.symphony.dto.UserRegistrationDTO;
import com.phungquocthai.symphony.service.UserService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/home")
public class HomeController {
	
	@Autowired
	private UserService userService;
	
	@GetMapping(value = "/fruits")
	public @ResponseBody ResponseEntity<List<String>> getAllFruits() {
		List<String> list = new ArrayList<String>();
		list.add("Mãng cầu");
		list.add("Dừa");
		list.add("Đu đủ");
		list.add("Xoài");
		list.add("Sung");
		return ResponseEntity.ok(list);
	}
	
	@GetMapping(value = "/hash")
	public @ResponseBody ResponseEntity<String> getPassword(@RequestParam("password") String password) {
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
		if(password != null) return ResponseEntity.ok(passwordEncoder.encode(password));
		return ResponseEntity.notFound().build();
	}
	
	@PostMapping(value = "/register")
	public @ResponseBody ResponseEntity<ApiResponse<UserDTO>> userRegister(@Valid @RequestBody UserRegistrationDTO userRegistrationDTO) {
		
		UserDTO userDTO = userService.create(userRegistrationDTO);
		ApiResponse<UserDTO> apiResponse = new ApiResponse<UserDTO>();
		apiResponse.setResult(userDTO);
		
		return ResponseEntity.ok(apiResponse);
	}
	
}
