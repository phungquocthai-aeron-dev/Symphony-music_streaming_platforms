package com.phungquocthai.symphony.controller;

import com.nimbusds.jose.JOSEException;
import com.phungquocthai.symphony.dto.*;
import com.phungquocthai.symphony.service.AuthenticationService;
import com.phungquocthai.symphony.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(
		name = "Authentication",
		description = "APIs for user authentication, registration, token management and role management"
)
public class AuthenticationController {

	@Autowired
	private AuthenticationService authenticationService;

	@Autowired
	private UserService userService;

	@Operation(
			summary = "Validate access token",
			description = "Checks whether the provided JWT access token is valid and has not expired."
	)
	@ApiResponses({
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "200",
					description = "Token is valid"
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "401",
					description = "Token is invalid or expired"
			)
	})
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


	@Operation(
			summary = "User login",
			description = "Authenticates a user using their credentials and returns a JWT access token."
	)
	@ApiResponses({
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "200",
					description = "Authentication successful"
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "401",
					description = "Invalid username or password"
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "400",
					description = "Invalid request data"
			)
	})
	@PostMapping("/login")
	public ResponseEntity<ApiResponse<AuthenticationResponse>> authenticate(
			@Valid @RequestBody AuthenticationRequest request) {

		AuthenticationResponse authenticated =
				this.authenticationService.authenticate(request);

		if (!authenticated.isAuthenticated()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(ApiResponse.<AuthenticationResponse>builder()
							.code(401)
							.message("Sai tên đăng nhập hoặc mật khẩu")
							.build());
		}

		ApiResponse<AuthenticationResponse> apiResponse =
				ApiResponse.<AuthenticationResponse>builder()
						.result(authenticated)
						.build();

		return ResponseEntity.ok(apiResponse);
	}


	@Operation(
			summary = "User logout",
			description = "Invalidates the current authentication token and logs the user out."
	)
	@ApiResponses({
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "200",
					description = "Logout successful"
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "401",
					description = "Invalid or expired token"
			)
	})
	@PostMapping("/logout")
	public ApiResponse<Void> logout(
			@Valid @RequestBody LogoutRequest request)
			throws JOSEException, ParseException {

		this.authenticationService.logout(request);

		return ApiResponse.<Void>builder().build();
	}


	@Operation(
			summary = "Register new user",
			description = "Creates a new user account using the provided registration information."
	)
	@ApiResponses({
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "200",
					description = "User registered successfully"
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "400",
					description = "Invalid registration data"
			)
	})
	@PostMapping(value = "/register")
	public @ResponseBody ResponseEntity<ApiResponse<UserDTO>> userRegister(
			@RequestBody UserRegistrationDTO userRegistrationDTO) {

		UserDTO userDTO = userService.create(userRegistrationDTO);

		ApiResponse<UserDTO> apiResponse = new ApiResponse<UserDTO>();
		apiResponse.setResult(userDTO);

		return ResponseEntity.ok(apiResponse);
	}


	@Operation(
			summary = "Refresh access token",
			description = "Generates a new access token using a valid refresh token."
	)
	@ApiResponses({
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "200",
					description = "Access token refreshed successfully"
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "401",
					description = "Refresh token is invalid or expired"
			)
	})
	@PostMapping("/refresh")
	public ApiResponse<AuthenticationResponse> authenticate(
			@Valid @RequestBody RefreshRequest request)
			throws JOSEException, ParseException {

		var authenticated =
				this.authenticationService.refreshToken(request);

		return ApiResponse.<AuthenticationResponse>builder()
				.result(authenticated)
				.build();
	}


	@Operation(
			summary = "Grant singer role",
			description = "Grants the singer role to the specified user."
	)
	@ApiResponses({
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "204",
					description = "Singer role granted successfully"
			),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
					responseCode = "404",
					description = "User not found"
			)
	})
	@Parameter(
			name = "id",
			description = "ID of the user to grant the singer role",
			required = true,
			example = "1"
	)
	@PostMapping("/grant/singer")
	public ResponseEntity<Void> grantSinger(
			@RequestParam(required = true, value = "id") Integer userId) {

		this.authenticationService.grantSinger(userId);

		return ResponseEntity.noContent().build();
	}
}

