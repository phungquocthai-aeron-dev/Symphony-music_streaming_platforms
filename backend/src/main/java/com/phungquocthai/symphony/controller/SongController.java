package com.phungquocthai.symphony.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.phungquocthai.symphony.annotation.ValidFile;
import com.phungquocthai.symphony.dto.ApiResponse;
import com.phungquocthai.symphony.dto.CategoryDTO;
import com.phungquocthai.symphony.dto.ListeningStatsDTO;
import com.phungquocthai.symphony.dto.RankingDTO;
import com.phungquocthai.symphony.dto.SongCreateDTO;
import com.phungquocthai.symphony.dto.SongDTO;
import com.phungquocthai.symphony.dto.SongUpdateDTO;
import com.phungquocthai.symphony.dto.TopSongDTO;
import com.phungquocthai.symphony.service.SongService;

import java.util.ArrayList;
import java.util.List;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/song")
@Slf4j
public class SongController {
	
	@Autowired
	private SongService songService;
	
	@GetMapping
	public ResponseEntity<ApiResponse<SongDTO>> findById(@RequestParam(value = "id", required = true) Integer songId) {
		SongDTO song = songService.getSongById(songId);
		ApiResponse<SongDTO> apiResponse = ApiResponse.<SongDTO>builder()
				.result(song)
				.build();
		return ResponseEntity.ok(apiResponse);
	}
	
	@GetMapping("/new")
	public ResponseEntity<ApiResponse<SongDTO>> findNewSong() {
		SongDTO song = songService.getNewSong();
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
	
	@GetMapping("/playlist")
	public ResponseEntity<ApiResponse<List<SongDTO>>> findByPlaylistId(@RequestParam(value = "id", required = true) Integer playlistId) {
		List<SongDTO> songs = songService.findByPlaylistId(playlistId);
		ApiResponse<List<SongDTO>> apiResponse = ApiResponse.<List<SongDTO>>builder()
				.result(songs)
				.build();
		return ResponseEntity.ok(apiResponse);
	}
	
	@GetMapping("/album")
	public ResponseEntity<ApiResponse<List<SongDTO>>> findByAlbumId(@RequestParam(value = "id", required = true) Integer albumId) {
		List<SongDTO> songs = songService.findByAlbumId(albumId);
		ApiResponse<List<SongDTO>> apiResponse = ApiResponse.<List<SongDTO>>builder()
				.result(songs)
				.build();
		return ResponseEntity.ok(apiResponse);
	}
	
	@PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<ApiResponse<SongDTO>> create(
			@Valid @ModelAttribute SongCreateDTO dto,
			
//			@ValidFile(maxSize = 50 * 1024 * 1024, // 50MB
//            	allowedContentTypes = {"mp3"},
//            	message = "File không hợp lệ")
			@RequestPart MultipartFile musicFile,
			
//		    @ValidFile(maxSize = 1024 * 1024, // 1MB
//            	allowedContentTypes = {"txt"},
//            	message = "File không hợp lệ")
			@RequestPart MultipartFile lrcFile,
			
//		    @ValidFile(maxSize = 1024 * 1024, // 1MB
//            	allowedContentTypes = {"txt"},
//            	message = "File không hợp lệ")
			@RequestPart MultipartFile lyricFile,
			
//		    @ValidFile(maxSize = 1024 * 1024, // 1MB
//            	allowedContentTypes = {"jpeg", "jpg", "png"},
//            	message = "File không hợp lệ")
			@RequestPart MultipartFile songImgFile) {
		SongDTO song = songService.create(dto, musicFile, lyricFile, lrcFile, songImgFile);
		return ResponseEntity.ok(
				ApiResponse.<SongDTO>builder()
				.result(song)
				.build()
				);
	}

	@PostMapping("/delete")
	public ResponseEntity<Void> delete(
			@RequestParam(value = "songId", required = true) Integer songId,
			@RequestParam(value = "singerId", required = true) Integer singerId) {
		songService.delete(singerId, songId);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/update")
	public void update(
			@Valid @ModelAttribute SongUpdateDTO dto,
			
//			@ValidFile(
//				maxSize = 1024 * 1024,
//        		allowedContentTypes = {"lrc"},
//        		message = "File không hợp lệ",
//        		required = false)
			@RequestPart(required = false, value = "lrcFile") MultipartFile lrcFile,
		
//			@ValidFile(
//				maxSize = 1024 * 1024,
//				allowedContentTypes = {"txt"},
//				message = "File không hợp lệ",
//				required = false)
			@RequestPart(required = false, value = "lyricFile") MultipartFile lyricFile,
		
//			@ValidFile(maxSize = 1024 * 1024,
//        		allowedContentTypes = {"jpeg", "jpg", "png"},
//        		message = "File không hợp lệ",
//        		required = false)
			@RequestPart(required = false, value = "songImgFile") MultipartFile songImgFile) {
		songService.update(dto, lyricFile, lrcFile, songImgFile);
	}
	
//	@PostMapping("/unfavorite")
//	public ResponseEntity<Void> unfavorite(
//			@RequestParam(value = "id", required = true) Integer songId,
//			@AuthenticationPrincipal Jwt jwt) {
//		
//		if(jwt != null) {
//			try {
//				Integer userId = Integer.parseInt(jwt.getSubject());
//				songService.removeFavorite(songId, userId);
//		    } catch (NumberFormatException e) {
//		        log.error(e.getMessage());
//		    }
//			
//		}
//		
//		return ResponseEntity.noContent().build();
//	}
	
	@PostMapping("/favorite")
	public ResponseEntity<Void> favorite(
			@RequestParam(value = "id", required = true) Integer songId,
			@AuthenticationPrincipal Jwt jwt) {
		
		if(jwt != null) {
			try {
				Integer userId = Integer.parseInt(jwt.getSubject());
				log.info(userId.toString());
				songService.reverseFavorite(songId, userId);
		    } catch (NumberFormatException e) {
		        log.error(e.getMessage());
		    }
			
		}
		
		return ResponseEntity.noContent().build();
	}
	
	@GetMapping("/newSongs")
	public ResponseEntity<ApiResponse<List<SongDTO>>> getNewSongs(@RequestParam(value = "limit", defaultValue = "50") Integer limit) {
		List<SongDTO> songs = songService.getNewSongs(limit);
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
	
	@GetMapping("/category")
	public ResponseEntity<ApiResponse<List<SongDTO>>> getSongsByCategory(
			@RequestParam(value = "id", required = true) Integer categoryId) {
		List<SongDTO> songs = songService.getSongsByCategoryId(categoryId);
		return ResponseEntity.ok(
				ApiResponse.<List<SongDTO>>builder()
				.result(songs)
				.build()
				);
	}
	
	@GetMapping("/songsOfSinger")
	public ResponseEntity<ApiResponse<List<SongDTO>>> getSongsOfSinger(
			@RequestParam(value = "id", required = true) Integer singerId) {
		List<SongDTO> songs = songService.getBySingerId(singerId);
		return ResponseEntity.ok(
				ApiResponse.<List<SongDTO>>builder()
				.result(songs)
				.build()
				);
	}

	@GetMapping("/ranking")
	public ResponseEntity<ApiResponse<RankingDTO>> getTopSong(
			@RequestParam(value = "limit", defaultValue = "10") Integer limit) {
		List<TopSongDTO> songs = songService.getTopSong(limit);
		List<List<ListeningStatsDTO>> top = null;
		
		if(!songs.isEmpty()) {
			int size = songs.size();
			if(size >= 3) {
				top = songService.getTop3TrendingSongsPastHour(
				songs.get(0).getSong_id(), songs.get(1).getSong_id(), songs.get(2).getSong_id());
			} else if(size == 2) {
				top = songService.getTop2TrendingSongsPastHour(
				songs.get(0).getSong_id(), songs.get(1).getSong_id());
			} else if(size == 1) {
				top = songService.getTop1TrendingSongsPastHour(songs.get(0).getSong_id());
			}
		}
		
		RankingDTO result = new RankingDTO(songs, top);
		return ResponseEntity.ok(
				ApiResponse.<RankingDTO>builder()
				.result(result)
				.build()
				);
	}
	
	@GetMapping("/recomend")
	public ResponseEntity<ApiResponse<List<SongDTO>>> recommedSongs(
			@RequestParam(value = "ids") List<Integer> ids, 
			@RequestParam(value = "limit", defaultValue = "6") Integer limit) {
		List<SongDTO> songs = songService.recommedSongs(ids, limit);
		
		
		ApiResponse<List<SongDTO>> apiResponse = ApiResponse.<List<SongDTO>>builder()
				.result(songs)
				.build();
		return ResponseEntity.ok(apiResponse);
	}
	

//	@GetMapping("/recomend")
//	public ResponseEntity<ApiResponse<List<SongDTO>>> getRecommendedSongs(@AuthenticationPrincipal Jwt jwt) {
//		List<SongDTO> songs = new ArrayList<SongDTO>();
//		
//		if(jwt != null) {
//			try {
//				Integer userId = Integer.parseInt(jwt.getSubject());
//				List<SongDTO> recentlyListen = songService.getRecentlyListenSongs(Integer.valueOf(userId), 1);
//				if(recentlyListen != null) {
//					songs = songService.recommend(recentlyListen.get(0));
//					
//				}
//		    } catch (NumberFormatException e) {
//		        log.error(e.getMessage());
//		    }
//			
//		}
//		
//		return ResponseEntity.ok(
//				ApiResponse.<List<SongDTO>>builder()
//				.result(songs)
//				.build()
//				);
//	}

	// xoa file nhạc
}
