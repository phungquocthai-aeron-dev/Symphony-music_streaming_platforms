package com.phungquocthai.symphony.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.phungquocthai.symphony.constant.ErrorCode;
import com.phungquocthai.symphony.dto.SingerCreateDTO;
import com.phungquocthai.symphony.dto.SingerDTO;
import com.phungquocthai.symphony.dto.SingerUpdateDTO;
import com.phungquocthai.symphony.entity.Singer;
import com.phungquocthai.symphony.entity.User;
import com.phungquocthai.symphony.exception.AppException;
import com.phungquocthai.symphony.mapper.SingerCreateMapper;
import com.phungquocthai.symphony.mapper.SingerMapper;
import com.phungquocthai.symphony.repository.AlbumRepository;
import com.phungquocthai.symphony.repository.SingerRepository;
import com.phungquocthai.symphony.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class SingerService {
	@Autowired
	SingerRepository singerRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	SingerMapper singerMapper;
	
	@Autowired
	SingerCreateMapper singerCreateMapper;
	
	@Autowired
	AlbumRepository albumRepository;
	
	@Autowired
	SongService songService;
	
	@Autowired
	ExcelExportUtil excelExportUtil;
	
	@PreAuthorize("hasRole('ADMIN')")
	public SingerDTO create(SingerCreateDTO dto) {
		Singer singerData = singerCreateMapper.toEntity(dto);
		Singer singer = singerRepository.save(singerData);
		return singerMapper.toDTO(singer);
	}
	
	public SingerDTO update(SingerUpdateDTO dto) {
		log.info("AAAAAAAAAAAAAAAAAAAaa");
		Singer singer = singerRepository.findById(dto.getSinger_id())
				.orElseThrow(() -> new AppException(ErrorCode.SINGER_NOT_EXISTED));
		singer.setStageName(dto.getStageName());
		log.info(dto.getSinger_id()+ "");
		singerRepository.save(singer);
		log.info(dto.getStageName());
		log.info(singer.getStageName());
		
		return singerMapper.toDTO(singer);
	}
	
	@PreAuthorize("hasAnyRole('SINGER', 'ADMIN')")
	public void delete(Integer singerId) {
		List<Integer> songIds = singerRepository.findSongIdsInPresentBySingerId(singerId);
		if(!songIds.isEmpty()) {
			int size = songIds.size();
			for(int i = 0; i < size; i++) {
				songService.delete(singerId, songIds.get(i));
			}
		}
		albumRepository.deleteAlbumsBySingerId(singerId);
		User user = userRepository.findBySingerId(singerId).orElseThrow();
		user.setRole("USER");
		userRepository.save(user);
//		singerRepository.deleteById(singerId);
		
		singerRepository.updateIsActive(singerId, false);
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	public void enable(Integer singerId) {
		User user = userRepository.findBySingerId(singerId).orElseThrow();
		user.setRole("SINGER");
		userRepository.save(user);
		singerRepository.updateIsActive(singerId, true);
	}
	
	public SingerDTO getSinger(Integer singerId) {
		Singer singer = singerRepository.findSingerActive(singerId)
				.orElseThrow(() -> new AppException(ErrorCode.SINGER_NOT_EXISTED));
		return singerMapper.toDTO(singer);
	}
	
//	public List<SingerDTO> findByStageName(String stageName) {
//		List<Singer> singers = singerRepository.findByStageNameContainingIgnoreCase(stageName);
//		return singerMapper.toListDTO(singers);
//	}
	
	public SingerDTO getSingerByUserId(Integer userId) {
		Singer singer = singerRepository.findByUserId(userId)
				.orElseThrow(() -> new AppException(ErrorCode.SINGER_NOT_EXISTED));
		return singerMapper.toDTO(singer);
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	public List<SingerDTO> getSingers() {
		List<Singer> singers = singerRepository.findAll();
		return singerMapper.toListDTO(singers);
	}
	
	@PreAuthorize("hasAnyRole('SINGER', 'ADMIN')")
	public void deletePresent(Integer singerId, Integer songId) {
		singerRepository.deletePresent(singerId, songId);
	}
	
	public List<SingerDTO> findByStageName(String stageName) {
		List<Singer> singers = singerRepository.getByStageName(stageName);
		return singerMapper.toListDTO(singers);
	}
	
	public List<SingerDTO> getBySongId(List<Integer> ids) {
		List<Singer> singers = singerRepository.findAllBySongId(ids);
		return singerMapper.toListDTO(singers);
	}
	
	public List<SingerDTO> findAllExlucde(List<Integer> ids) {
		List<Singer> singers = singerRepository.findAllBySongIdNotIn(ids);
		return singerMapper.toListDTO(singers);
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	public byte[] exportToExcel() throws IOException {
        return excelExportUtil.exportToExcel(singerMapper.toListDTO(singerRepository.findAll()), null, "Danh sách ca sĩ");
    }
}
