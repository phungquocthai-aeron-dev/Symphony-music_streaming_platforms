package com.phungquocthai.symphony.controller;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.phungquocthai.symphony.dto.ApiResponse;
import com.phungquocthai.symphony.dto.SingerDTO;
import com.phungquocthai.symphony.dto.SingerUpdateDTO;
import com.phungquocthai.symphony.service.SingerService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/singer")
@Slf4j
public class SingerController {
	@Autowired
	SingerService singerService;

	@GetMapping
	public ResponseEntity<ApiResponse<SingerDTO>> findById(
			@RequestParam(value = "id", required = true) Integer singerId) {
		SingerDTO singer = singerService.getSinger(singerId);
		ApiResponse<SingerDTO> apiResponse = ApiResponse.<SingerDTO>builder()
				.result(singer)
				.build();
		return ResponseEntity.ok(apiResponse);
	}
	
	@GetMapping("/user")
	public ResponseEntity<ApiResponse<SingerDTO>> findByUserId(
			@RequestParam(value = "id", required = true) Integer userId) {
		SingerDTO singer = singerService.getSingerByUserId(userId);
		ApiResponse<SingerDTO> apiResponse = ApiResponse.<SingerDTO>builder()
				.result(singer)
				.build();
		return ResponseEntity.ok(apiResponse);
	}
	
	@GetMapping("/singers")
	public ResponseEntity<ApiResponse<List<SingerDTO>>> findAll() {
		List<SingerDTO> singers = singerService.getSingers();
		ApiResponse<List<SingerDTO>> apiResponse = ApiResponse.<List<SingerDTO>>builder()
				.result(singers)
				.build();
		return ResponseEntity.ok(apiResponse);
	}
	
	@GetMapping("/stageName")
	public ResponseEntity<ApiResponse<List<SingerDTO>>> findBýtagename(
			@RequestParam(value = "stageName", required = true) String stageName) {
		List<SingerDTO> singers = singerService.findByStageName(stageName);
		ApiResponse<List<SingerDTO>> apiResponse = ApiResponse.<List<SingerDTO>>builder()
				.result(singers)
				.build();
		return ResponseEntity.ok(apiResponse);
	}
	
	@PostMapping("/disable")
	public ResponseEntity<Void> disable(@RequestParam(value = "id", required = true) Integer singerId) {
		singerService.disable(singerId);
		return ResponseEntity.noContent().build();
	}
	
	@PostMapping("/update")
	public ResponseEntity<ApiResponse<SingerDTO>> update(SingerUpdateDTO dto) {
		SingerDTO singer = singerService.update(dto);
		ApiResponse<SingerDTO> apiResponse = ApiResponse.<SingerDTO>builder()
				.result(singer)
				.build();
		return ResponseEntity.ok(apiResponse);
	}
	
	@PostMapping("/unpresent")
	public ResponseEntity<Void> deletePresent(
			@RequestParam(value = "id", required = true) Integer singerId,
			@RequestParam(value = "id", required = true) Integer songId) {
		singerService.deletePresent(singerId, songId);
		return ResponseEntity.noContent().build();
	}
	
	@PostMapping("/exclude")
	public  ResponseEntity<ApiResponse<List<SingerDTO>>> getSingerExclude(
			@RequestBody List<SingerDTO> list) {
		List<Integer> ids = list.stream().map(SingerDTO::getSinger_id).collect(Collectors.toList());
		List<SingerDTO> singers = singerService.findAllExlucde(ids);

		ApiResponse<List<SingerDTO>> apiResponse = ApiResponse.<List<SingerDTO>>builder()
				.result(singers)
				.build();
		return ResponseEntity.ok(apiResponse);
	}
	
	@PostMapping("/export")
    public ResponseEntity<byte[]> exportToExcel() {
    	
    	byte[] excelData = new byte[0];;
    	

            try {
				excelData = singerService.exportToExcel();
			} catch (IOException e) {
				e.printStackTrace();
			}
    	
    	            
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=danh_sach_ca_si.xlsx");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(excelData);
    }
}
