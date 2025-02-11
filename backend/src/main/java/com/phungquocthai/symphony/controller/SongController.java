package com.phungquocthai.symphony.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.phungquocthai.symphony.annotation.ValidFile;
import com.phungquocthai.symphony.dto.ApiResponse;
import com.phungquocthai.symphony.dto.SongCreateDTO;
import com.phungquocthai.symphony.dto.SongDTO;
import com.phungquocthai.symphony.dto.UserDTO;
import com.phungquocthai.symphony.service.SongService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/song")
@Slf4j
public class SongController {
	
	@Autowired
	private SongService songService;
	
	@PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<ApiResponse<SongDTO>> create(
			@Valid @RequestParam SongCreateDTO song,
			@ValidFile(maxSize = 50 * 1024 * 1024, // 50MB
            	allowedContentTypes = {"mp3"},
            	message = "File không hợp lệ")
			@RequestPart MultipartFile musicFile,
		    @ValidFile(maxSize = 1024 * 1024, // 1MB
            	allowedContentTypes = {"txt"},
            	message = "File không hợp lệ")
			@RequestPart MultipartFile lrcFile,
		    @ValidFile(maxSize = 1024 * 1024, // 1MB
            	allowedContentTypes = {"txt"},
            	message = "File không hợp lệ")
			@RequestPart MultipartFile lyricFile,
		    @ValidFile(maxSize = 1024 * 1024, // 1MB
            	allowedContentTypes = {"jpeg", "jpg", "png"},
            	message = "File không hợp lệ")
			@RequestPart MultipartFile songImgFile) {
		return ResponseEntity.ok(
				ApiResponse.<SongDTO>builder()
				.result(songService.create(song, musicFile, lyricFile, lrcFile, songImgFile))
				.build()
				);
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
	
}
