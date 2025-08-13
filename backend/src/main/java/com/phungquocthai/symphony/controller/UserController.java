package com.phungquocthai.symphony.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
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
import com.phungquocthai.symphony.dto.LibraryDTO;
import com.phungquocthai.symphony.dto.SingerDTO;
import com.phungquocthai.symphony.dto.SingerUpdateDTO;
import com.phungquocthai.symphony.dto.SongDTO;
import com.phungquocthai.symphony.dto.StatisticListenDTO;
import com.phungquocthai.symphony.dto.StatisticRevenueDTO;
import com.phungquocthai.symphony.dto.UserDTO;
import com.phungquocthai.symphony.dto.UserUpdateDTO;
import com.phungquocthai.symphony.entity.User;
import com.phungquocthai.symphony.repository.UserRepository;
import com.phungquocthai.symphony.service.ExcelExportUtil;
import com.phungquocthai.symphony.service.FavoriteService;
import com.phungquocthai.symphony.service.ListenService;
import com.phungquocthai.symphony.service.PlaylistService;
import com.phungquocthai.symphony.service.SingerService;
import com.phungquocthai.symphony.service.SongService;
import com.phungquocthai.symphony.service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private SingerService singerService;
	
	@Autowired
	private SongService songService;
	
	@Autowired
	private FavoriteService favoriteService;
	
	@Autowired
	private PlaylistService playlistService;
	
	@Autowired
	private ListenService listenService;
	
	@Autowired
	ExcelExportUtil excelExportUtil;
	
	
	@GetMapping
	public ResponseEntity<ApiResponse<UserDTO>> findById(@RequestParam(value = "id", required = true) Integer userId) {
		ApiResponse<UserDTO> apiResponse = new ApiResponse<UserDTO>();
		UserDTO user = userService.getUserById(userId);
		apiResponse.setResult(user);
		return ResponseEntity.ok(apiResponse);
	}
	
	@GetMapping("/phone")
	public ResponseEntity<ApiResponse<UserDTO>> findByPhone(@RequestParam(value = "phone", required = true) String phone) {
		ApiResponse<UserDTO> apiResponse = new ApiResponse<UserDTO>();
		UserDTO user = userService.findByPhone(phone);
		apiResponse.setResult(user);
		return ResponseEntity.ok(apiResponse);
	}
	
	@GetMapping("/singer")
	public ResponseEntity<ApiResponse<UserDTO>> findBySingerId(@RequestParam(value = "id", required = true) Integer userId) {
		ApiResponse<UserDTO> apiResponse = new ApiResponse<UserDTO>();
		UserDTO user = userService.getUserBySingerId(userId);
		apiResponse.setResult(user);
		return ResponseEntity.ok(apiResponse);
	}

	@PostMapping("/delete")
	public ResponseEntity<Void> delete(@RequestParam(value = "id", required = true) Integer userId) {
		userService.delete(userId);
		return ResponseEntity.noContent().build();
	}
	
	@PostMapping("/enable")
	public ResponseEntity<Void> enable(@RequestParam(value = "id", required = true) Integer userId) {
		userService.enable(userId);
		return ResponseEntity.noContent().build();
	}

	@PostMapping(value =  "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<ApiResponse<UserDTO>> update(
			@ModelAttribute UserUpdateDTO dto,
			@ModelAttribute SingerUpdateDTO singerdto,
//			@ValidFile(maxSize = 1024 * 1024, // 1MB
//	            allowedContentTypes = {"jpeg", "jpg", "png"},
//	            message = "File không hợp lệ",
//	            required = false)
			@RequestPart(required = false, value = "avatarFile") MultipartFile avatarFile,
			@RequestParam(required = true) String password,
			@RequestParam(required = false) String password_confirm,
			@RequestParam(required = false) String newPassword) {
		
		userService.update(dto, avatarFile, password, password_confirm, newPassword);
		log.info("STOP");
	
		UserDTO user = userService.getUserById(dto.getId());
		log.info(dto.getId().toString());

		if(user != null) {
			log.info(user.getRole());
			log.info(dto.getId().toString());
			if(user.getRole().equals("SINGER")) singerService.update(singerdto);
		}

		return ResponseEntity.ok(
				ApiResponse.<UserDTO>builder()
				.result(user)
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
			@RequestParam(value = "id", required = true) int song_id,
			@AuthenticationPrincipal Jwt jwt) {
		favoriteService.create(Integer.valueOf(jwt.getSubject()), song_id);
		return ResponseEntity.ok(ApiResponse.<Boolean>builder().result(true).build());
	}
	
	@GetMapping("/recentlyListen")
	public ResponseEntity<ApiResponse<List<SongDTO>>> recentlyListenSongsPage(
			@AuthenticationPrincipal Jwt jwt) {
		List<SongDTO> songs = songService.getRecentlyListenSongs(Integer.valueOf(jwt.getSubject()), 100);
		ApiResponse<List<SongDTO>> apiResponse = new ApiResponse<List<SongDTO>>();
		apiResponse.setResult(songs);
		return ResponseEntity.ok(apiResponse);
	}
	
//	@GetMapping("/library")
//	public ResponseEntity<ApiResponse<LibraryDTO>> library(@RequestParam(value = "id", required = true) Integer userId) {
//		LibraryDTO libraryDTO = LibraryDTO.builder()
//				.playlists(playlistService.getPlaylistOfUser(userId))
//				.songs(songService.getFavoriteSongsOfUser(userId))
//				.build();
//		return ResponseEntity.ok(ApiResponse.<LibraryDTO>builder().result(libraryDTO).build());
//	}
	
	@GetMapping("/listened")
	public void listened(
			@RequestParam(value = "id", required = true) int songId,
			@AuthenticationPrincipal Jwt jwt) {
		if(jwt != null) {
			try {
				Integer userId = Integer.parseInt(jwt.getSubject());
				log.info(userId.toString());
				this.songService.listenedSong(userId, songId);
		    } catch (NumberFormatException e) {
		        log.error(e.getMessage());
		    }
			
		}
	}
	
	@GetMapping("/statistic/listenOfMonth")
    public ResponseEntity<ApiResponse<Map<String, Object>>> thongKeLuotNghe(
            @RequestParam("thang") int thang,
            @RequestParam("nam") int nam) {
        
        Integer soLuotNghe = listenService.thongKeTheoThang(thang, nam);
        
        Map<String, Object> response = new HashMap<>();
        response.put("thang", thang);
        response.put("nam", nam);
        response.put("soLuotNghe", soLuotNghe);
        
        ApiResponse<Map<String, Object>> apiResponse = ApiResponse.<Map<String, Object>>builder()
				.result(response)
				.build();
		return ResponseEntity.ok(apiResponse);
	}
	
	@GetMapping("/statistic/revenueOfMonth")
    public ResponseEntity<ApiResponse<Map<String, Object>>> RevenueStatistc(
            @RequestParam("thang") int thang,
            @RequestParam("nam") int nam) {
        
        Float revenue = userService.thongKeTheoThang(thang, nam);
        
        Map<String, Object> response = new HashMap<>();
        response.put("thang", thang);
        response.put("nam", nam);
        response.put("revenue", revenue);
        
        ApiResponse<Map<String, Object>> apiResponse = ApiResponse.<Map<String, Object>>builder()
				.result(response)
				.build();
		return ResponseEntity.ok(apiResponse);
	}
	
	@PostMapping("/statistic/exportListen")
    public ResponseEntity<byte[]> exportStatisticListenToExcel(@RequestParam() Integer year) {
    	    	
    	List<StatisticListenDTO> list = new ArrayList<StatisticListenDTO>();
    	
    	for(int i = 0; i < 12; i++) {
    		StatisticListenDTO item = new StatisticListenDTO();
    		Integer total = listenService.thongKeTheoThang(i+1, year);
    		item.setMonth(i+1);
    		item.setYear(year);
    		item.setTotalListen(total);
    		list.add(item);
    	}
    	
    	byte[] excelData = new byte[0];;
    	
    	if (list == null || list.isEmpty()) {
    		excelData = new byte[0];
        }
    	else {
            try {
				excelData = excelExportUtil.exportToExcel(list, null, "Thống kê lượt nghe theo tháng năm " + year);

			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	            
            // Thiết lập header cho response
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=thong_ke_luot_nghe.xlsx");
            
            // Trả về file Excel
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(excelData);
    }
    
    @PostMapping("/statistic/exportRevenue")
    public ResponseEntity<byte[]> exportRevenueListenToExcel(@RequestParam() Integer year) {
    	    	
    	List<StatisticRevenueDTO> list = new ArrayList<StatisticRevenueDTO>();
    	
    	for(int i = 0; i < 12; i++) {
    		StatisticRevenueDTO item = new StatisticRevenueDTO();
    		Float total = userService.thongKeTheoThang(i+1, year);
    		item.setMonth(i+1);
    		item.setYear(year);
    		item.setTotalRevenue(total);
    		list.add(item);
    	}
    	
    	byte[] excelData = new byte[0];;
    	
    	if (list == null || list.isEmpty()) {
    		excelData = new byte[0];
        }
    	else {
            try {
				excelData = excelExportUtil.exportToExcel(list, null, "Thống kê doanh thu theo tháng năm " + year);

			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	            
            // Thiết lập header cho response
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=thong_ke_doanh_thu.xlsx");
            
            // Trả về file Excel
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(excelData);
    }
	
	@GetMapping("/recently")
	public ResponseEntity<ApiResponse<List<SongDTO>>> recentlyListenSongs(
			@AuthenticationPrincipal Jwt jwt,
			@RequestParam(value = "limit", defaultValue = "50") Integer limit) {
		List<SongDTO> songs = new ArrayList<SongDTO>();
		
		if(jwt != null) {
			try {
				Integer userId = Integer.parseInt(jwt.getSubject());
				songs = songService.getRecentlyListenSongs(userId, limit);

		    } catch (NumberFormatException e) {
		        log.error(e.getMessage());
		    }
			
		}
		
		ApiResponse<List<SongDTO>> apiResponse = ApiResponse.<List<SongDTO>>builder()
				.result(songs)
				.build();
		return ResponseEntity.ok(apiResponse);
	}
	
	@PostMapping("/export")
    public ResponseEntity<byte[]> exportToExcel() {
    	
    	byte[] excelData = new byte[0];;
    	

            try {
				excelData = userService.exportToExcel();
			} catch (IOException e) {
				e.printStackTrace();
			}
    	
    	            
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=danh_sach_nguoi_dung.xlsx");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(excelData);
    }
	
}
