package com.phungquocthai.symphony.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.phungquocthai.symphony.annotation.ValidFile;
import com.phungquocthai.symphony.dto.ApiResponse;
import com.phungquocthai.symphony.dto.SongDTO;
import com.phungquocthai.symphony.dto.UserDTO;
import com.phungquocthai.symphony.dto.UserUpdateDTO;
import com.phungquocthai.symphony.service.FavoriteService;
import com.phungquocthai.symphony.service.SongService;
import com.phungquocthai.symphony.service.UserService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private SongService songService;
	
	@Autowired
	private FavoriteService favoriteService;
	
	public ResponseEntity<ApiResponse<UserDTO>> findById(@RequestParam("id") int userId) {
		ApiResponse<UserDTO> apiResponse = new ApiResponse<UserDTO>();
		UserDTO user = userService.getUser(userId);
		apiResponse.setResult(user);
		return ResponseEntity.ok(apiResponse);
	}

	@PostMapping("/delete")
	public ResponseEntity<Void> delete(@RequestParam("id") Integer userId) {
		userService.delete(userId);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/update")
	public ResponseEntity<ApiResponse<UserDTO>> update(
			@Valid @RequestPart UserUpdateDTO dto,
			@ValidFile(maxSize = 1024 * 1024, // 1MB
	            allowedContentTypes = {"jpeg", "jpg", "png"},
	            message = "File không hợp lệ",
	            required = false)
			@RequestPart(required = false) MultipartFile avatar) {
		return ResponseEntity.ok(
				ApiResponse.<UserDTO>builder()
				.result(userService.update(dto, avatar))
				.build()
				);
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

	@GetMapping("/favorite")
	public ResponseEntity<ApiResponse<List<SongDTO>>> getFavoriteSongsPage(
			@AuthenticationPrincipal Jwt jwt) {
		List<SongDTO> songs = songService.getFavoriteSongsOfUser(Integer.valueOf(jwt.getSubject()));
		ApiResponse<List<SongDTO>> apiResponse = new ApiResponse<List<SongDTO>>();
		apiResponse.setResult(songs);
		return ResponseEntity.ok(apiResponse);
	}
	
	@PostMapping("/favorite")
	public ResponseEntity<ApiResponse<Boolean>> addFavoriteSong(
			@RequestParam("id") int song_id,
			@AuthenticationPrincipal Jwt jwt) {
		favoriteService.create(Integer.valueOf(jwt.getSubject()), song_id);
		return ResponseEntity.ok(ApiResponse.<Boolean>builder().result(true).build());
	}
	
	@GetMapping("/recentlyListen")
	public ResponseEntity<ApiResponse<List<SongDTO>>> recentlyListenSongsPage(
			@AuthenticationPrincipal Jwt jwt) {
		List<SongDTO> songs = songService.getRecentlyListenSongs(Integer.valueOf(jwt.getSubject()));
		ApiResponse<List<SongDTO>> apiResponse = new ApiResponse<List<SongDTO>>();
		apiResponse.setResult(songs);
		return ResponseEntity.ok(apiResponse);
	}
	
	// Thư viện
	
}
