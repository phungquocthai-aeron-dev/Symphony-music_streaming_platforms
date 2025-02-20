package com.phungquocthai.symphony.controller;

import java.text.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.nimbusds.jose.JOSEException;
import com.phungquocthai.symphony.dto.ApiResponse;
import com.phungquocthai.symphony.dto.AuthenticationRequest;
import com.phungquocthai.symphony.dto.AuthenticationResponse;
import com.phungquocthai.symphony.dto.IntrospectRequest;
import com.phungquocthai.symphony.dto.IntrospectResponse;
import com.phungquocthai.symphony.dto.LogoutRequest;
import com.phungquocthai.symphony.dto.RefreshRequest;
import com.phungquocthai.symphony.dto.UserDTO;
import com.phungquocthai.symphony.dto.UserRegistrationDTO;
import com.phungquocthai.symphony.service.AuthenticationService;
import com.phungquocthai.symphony.service.UserService;

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
	
	@Autowired
	private UserService userService;
	
	@PostMapping("/valid-token")
	public ResponseEntity<?> checkToken(@RequestBody String token) {
		System.err.println(token);
	    if (token != null) {
	        IntrospectRequest request = new IntrospectRequest(true, token);
	        try {
	            IntrospectResponse response = authenticationService.introspect(request);
	            if (response.isValid()) {
	                return ResponseEntity.ok("Token hợp lệ");
	            }
	        } catch (JOSEException | ParseException e) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token không hợp lệ");
	        }
	    }
	    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token không hợp lệ");
	}

	
	@PostMapping("/login")
	public ResponseEntity<ApiResponse<AuthenticationResponse>> authenticate(@Valid @RequestBody AuthenticationRequest request) {
	    AuthenticationResponse authenticated = this.authenticationService.authenticate(request);

	    if (!authenticated.isAuthenticated()) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
	                .body(ApiResponse.<AuthenticationResponse>builder()
	                .code(401)
	                .message("Sai tên đăng nhập hoặc mật khẩu")
	                .build());
	    }

	    ApiResponse<AuthenticationResponse> apiResponse = ApiResponse.<AuthenticationResponse>builder()
	            .result(authenticated)
	            .build();
	    return ResponseEntity.ok(apiResponse);
	}
	
	@PostMapping("/logout")
	public ApiResponse<Void> logout(@Valid @RequestBody LogoutRequest request) throws JOSEException, ParseException {
		this.authenticationService.logout(request);
		
		return ApiResponse.<Void>builder().build();
	}
	
	@PostMapping(value = "/register")
	public @ResponseBody ResponseEntity<ApiResponse<UserDTO>> userRegister(@Valid @RequestBody UserRegistrationDTO userRegistrationDTO) {
		
		UserDTO userDTO = userService.create(userRegistrationDTO);
		ApiResponse<UserDTO> apiResponse = new ApiResponse<UserDTO>();
		apiResponse.setResult(userDTO);
		
		return ResponseEntity.ok(apiResponse);
	}
	
	@PostMapping("/refresh")
	public ApiResponse<AuthenticationResponse> authenticate(@Valid @RequestBody RefreshRequest request) throws JOSEException, ParseException {
		var authenticated = this.authenticationService.refreshToken(request);
		
		return ApiResponse.<AuthenticationResponse>builder()
				.result(authenticated)
				.build();
	}
}
