package com.phungquocthai.symphony.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.phungquocthai.symphony.annotation.ValidFile;
import com.phungquocthai.symphony.dto.ApiResponse;
import com.phungquocthai.symphony.dto.CategoryDTO;
import com.phungquocthai.symphony.dto.SearchDTO;
import com.phungquocthai.symphony.dto.SingerDTO;
import com.phungquocthai.symphony.dto.SongCreateDTO;
import com.phungquocthai.symphony.dto.SongDTO;
import com.phungquocthai.symphony.dto.SongUpdateDTO;
import com.phungquocthai.symphony.service.SingerService;
import com.phungquocthai.symphony.service.SongService;
import java.util.List;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/song")
@Slf4j
public class SongController {
	
	@Autowired
	private SongService songService;
	
	@Autowired
	private SingerService singerService;
	
	@GetMapping
	public ResponseEntity<ApiResponse<SongDTO>> findById(@RequestParam(value = "id", required = true) Integer songId) {
		SongDTO song = songService.getSongById(songId);
		ApiResponse<SongDTO> apiResponse = ApiResponse.<SongDTO>builder()
				.result(song)
				.build();
		return ResponseEntity.ok(apiResponse);
	}
	
	@GetMapping("/songs")
	public ResponseEntity<ApiResponse<List<SongDTO>>> findAll() {
		List<SongDTO> songs = songService.findAll();
		ApiResponse<List<SongDTO>> apiResponse = ApiResponse.<List<SongDTO>>builder()
				.result(songs)
				.build();
		return ResponseEntity.ok(apiResponse);
	}
	
	@PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<ApiResponse<SongDTO>> create(
			@Valid @RequestParam SongCreateDTO dto,
			
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
		SongDTO song = songService.create(dto, musicFile, lyricFile, lrcFile, songImgFile);
		return ResponseEntity.ok(
				ApiResponse.<SongDTO>builder()
				.result(song)
				.build()
				);
	}

	@PostMapping("/delete")
	public ResponseEntity<Void> delete(@RequestParam(value = "id", required = true) Integer songId) {
		songService.delete(songId);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/update")
	public ResponseEntity<ApiResponse<SongDTO>> update(
			@Valid @RequestPart SongUpdateDTO dto,
			
			@ValidFile(
				maxSize = 1024 * 1024, // 1MB
        		allowedContentTypes = {"txt"},
        		message = "File không hợp lệ",
        		required = false)
			@RequestPart MultipartFile lrcFile,
		
			@ValidFile(
				maxSize = 1024 * 1024, // 1MB
				allowedContentTypes = {"txt"},
				message = "File không hợp lệ",
				required = false)
			@RequestPart MultipartFile lyricFile,
		
			@ValidFile(maxSize = 1024 * 1024, // 1MB
        		allowedContentTypes = {"jpeg", "jpg", "png"},
        		message = "File không hợp lệ",
        		required = false)
			@RequestPart MultipartFile songImgFile) {
		SongDTO song = songService.update(dto, lyricFile, lrcFile, songImgFile);
		return ResponseEntity.ok(
				ApiResponse.<SongDTO>builder()
				.result(song)
				.build()
				);
	}
	
	@GetMapping("/newSongs")
	public ResponseEntity<ApiResponse<List<SongDTO>>> getNewSongs() {
		List<SongDTO> songs = songService.getNewSongs();
		return ResponseEntity.ok(
				ApiResponse.<List<SongDTO>>builder()
				.result(songs)
				.build()
				);
	}
	
	@GetMapping("/categories")
	public ResponseEntity<ApiResponse<List<CategoryDTO>>> getCategories() {
		List<CategoryDTO> categories = songService.getAllCategories();
		return ResponseEntity.ok(
				ApiResponse.<List<CategoryDTO>>builder()
				.result(categories)
				.build()
				);
	}
	
	@PostMapping("/listenedSong")
	public ResponseEntity<Integer> listenedSong(@RequestParam(value = "id", required = true) Integer songId) {
		Integer totalListens = songService.updateTotalListenOfSong(songId);
		return ResponseEntity.ok(totalListens);
	}
	
	@GetMapping("/search")
	public ResponseEntity<ApiResponse<SearchDTO>> searchSongs(
			@RequestParam(value = "id") String key) {
		if(key == null) return ResponseEntity.noContent().build();
		if(key.length() == 0) return ResponseEntity.noContent().build();
		
		List<SongDTO> songs = songService.searchSongs(key);
		List<SingerDTO> singers = singerService.findByStageName(key);
		
		SearchDTO result = SearchDTO.builder()
				.songs(songs)
				.singers(singers)
				.build();
		
		return ResponseEntity.ok(
				ApiResponse.<SearchDTO>builder()
				.result(result)
				.build()
				);
	}
	
	@GetMapping("/category")
	public ResponseEntity<ApiResponse<List<SongDTO>>> getSongsByCategory(@RequestParam(value = "id", required = true) Integer categoryId) {
		List<SongDTO> songs = songService.getSongsByCategory(categoryId);
		return ResponseEntity.ok(
				ApiResponse.<List<SongDTO>>builder()
				.result(songs)
				.build()
				);
	}
	// xoa file nhạc
}
