package com.phungquocthai.symphony.controller;

import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nimbusds.jose.JOSEException;
import com.phungquocthai.symphony.dto.ApiResponse;
import com.phungquocthai.symphony.dto.AuthenticationRequest;
import com.phungquocthai.symphony.dto.AuthenticationResponse;
import com.phungquocthai.symphony.dto.LogoutRequest;
import com.phungquocthai.symphony.dto.RefreshRequest;
import com.phungquocthai.symphony.service.AuthenticationService;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
	@Autowired
	private AuthenticationService authenticationService;
	
	@PostMapping("/login")
	public ApiResponse<AuthenticationResponse> authenticate(@Valid @RequestBody AuthenticationRequest request) {
		var authenticated = this.authenticationService.authenticate(request);
		
		return ApiResponse.<AuthenticationResponse>builder()
				.result(authenticated)
				.build();
	}
	
	@PostMapping("/logout")
	public ApiResponse<Void> logout(@Valid @RequestBody LogoutRequest request) throws JOSEException, ParseException {
		this.authenticationService.logout(request);
		
		return ApiResponse.<Void>builder().build();
	}
	
	@PostMapping("/refresh")
	public ApiResponse<AuthenticationResponse> authenticate(@Valid @RequestBody RefreshRequest request) throws JOSEException, ParseException {
		var authenticated = this.authenticationService.refreshToken(request);
		
		return ApiResponse.<AuthenticationResponse>builder()
				.result(authenticated)
				.build();
	}
}
