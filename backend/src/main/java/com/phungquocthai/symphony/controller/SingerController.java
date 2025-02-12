package com.phungquocthai.symphony.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
	
	@GetMapping("/singers")
	public ResponseEntity<ApiResponse<List<SingerDTO>>> findAll() {
		List<SingerDTO> singers = singerService.getSingers();
		ApiResponse<List<SingerDTO>> apiResponse = ApiResponse.<List<SingerDTO>>builder()
				.result(singers)
				.build();
		return ResponseEntity.ok(apiResponse);
	}
	
	@PostMapping("/delete")
	public ResponseEntity<Void> delete(@RequestParam(value = "id", required = true) Integer singerId) {
		singerService.delete(singerId);
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
	
}
