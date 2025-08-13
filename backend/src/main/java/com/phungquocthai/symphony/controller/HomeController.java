package com.phungquocthai.symphony.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.phungquocthai.symphony.constant.CategorySong;
import com.phungquocthai.symphony.dto.ApiResponse;
import com.phungquocthai.symphony.dto.HomeDTO;
import com.phungquocthai.symphony.dto.ListeningStatsDTO;
import com.phungquocthai.symphony.dto.SearchDTO;
import com.phungquocthai.symphony.dto.SingerDTO;
import com.phungquocthai.symphony.dto.SongDTO;
import com.phungquocthai.symphony.dto.TopSongDTO;
import com.phungquocthai.symphony.entity.Vip;
import com.phungquocthai.symphony.service.AISearchService;
import com.phungquocthai.symphony.service.SingerService;
import com.phungquocthai.symphony.service.SongService;
import com.phungquocthai.symphony.service.UserService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value = {"/home", "/"})
@Slf4j
public class HomeController {
	@Autowired
	private SongService songService;
	
	@Autowired
	private SingerService singerService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private AISearchService aiSearchService;
		
	@GetMapping
	public ResponseEntity<ApiResponse<HomeDTO>> getHome(@AuthenticationPrincipal Jwt jwt) {
		HomeDTO data = new HomeDTO();
		
		List<SongDTO> hotHitSong = songService.getHotHitSong(6);
		List<SongDTO> vpopSongs = songService.getSongsByCategoryName(CategorySong.VPOP.getValue(), 6);
		List<SongDTO> usUkSongs = songService.getSongsByCategoryName(CategorySong.USUK.getValue(), 6);
		List<SongDTO> remixSongs = songService.getSongsByCategoryName(CategorySong.REMIX.getValue(), 6);
		List<SongDTO> lyricalSongs = songService.getSongsByCategoryName(CategorySong.LYRICAL.getValue(), 6);
		List<SongDTO> newSongs = songService.getNewSongs(9);
		
		data.setHotHitSong(hotHitSong);
		data.setVpopSongs(vpopSongs);
		data.setUsUkSongs(usUkSongs);
		data.setRemixSongs(remixSongs);
		data.setLyricalSongs(lyricalSongs);
		data.setNewSongs(newSongs);
		
//		if(jwt != null) {
//			try {
//				Integer userId = Integer.parseInt(jwt.getSubject());
//				List<SongDTO> recentlyListen = songService.getRecentlyListenSongs(Integer.valueOf(userId), 6);
//				if(recentlyListen != null) {
//					List<SongDTO> recommend = songService.recommend(recentlyListen.get(0));
//					
//					data.setRecentlyListen(recentlyListen);
//					data.setRecommend(recommend);
//				}
//		    } catch (NumberFormatException e) {
//		        log.error(e.getMessage());
//		    }
//			
//		}
		
		List<TopSongDTO> topSongs = songService.getTopSong(3);
		List<List<ListeningStatsDTO>> top = null;
		
		if(!topSongs.isEmpty()) {
			int size = topSongs.size();
			if(size >= 3) {
				top = songService.getTop3TrendingSongsPastHour(
				topSongs.get(0).getSong_id(), topSongs.get(1).getSong_id(), topSongs.get(2).getSong_id());
			} else if(size == 2) {
				top = songService.getTop2TrendingSongsPastHour(
				topSongs.get(0).getSong_id(), topSongs.get(1).getSong_id());
			} else if(size == 1) {
				top = songService.getTop1TrendingSongsPastHour(topSongs.get(0).getSong_id());
			}
		}
				
		data.setTopSongs(topSongs);
		data.setTopTrendingStats(top);
		return ResponseEntity.ok(
				ApiResponse.<HomeDTO>builder()
				.result(data)
				.build()
				);
	}
	
	@GetMapping("/search")
	public ResponseEntity<ApiResponse<SearchDTO>> searchSongs(
			@RequestParam(value = "s") String key) {
		if(key == null) return ResponseEntity.noContent().build();
		if(key.length() == 0) return ResponseEntity.noContent().build();
		
		key = "%" + key + "%";
		
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
	
	@GetMapping("/vip")
	public ResponseEntity<ApiResponse<List<Vip>>> getAllVipPakages() {
		List<Vip> vips = userService.getAllVipPakages();
		
		return ResponseEntity.ok(
				ApiResponse.<List<Vip>>builder()
				.result(vips)
				.build()
				);
	}
	
	@PostMapping("/search-humming")
    public ResponseEntity<List<SongDTO>> searchHumming(@RequestParam("file") MultipartFile file) {
		 try {
		        // In ra thông tin file nhận được
		        System.out.println("Received file: " + file.getOriginalFilename() + ", size: " + file.getSize());
		        
		        List<SongDTO> results = aiSearchService.searchByHumming(file);

		        // In ra kết quả trả về từ AI service (tên bài hát)
		        results.forEach(song -> System.out.println("Matched song: " + song.getSongName()));

		        return ResponseEntity.ok(results);
		    } catch (Exception e) {
		        e.printStackTrace();
		        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		    }
    }

}
