package com.phungquocthai.symphony.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.phungquocthai.symphony.constant.ErrorCode;
import com.phungquocthai.symphony.constant.PathStorage;
import com.phungquocthai.symphony.dto.CategoryDTO;
import com.phungquocthai.symphony.dto.SongCreateDTO;
import com.phungquocthai.symphony.dto.SongDTO;
import com.phungquocthai.symphony.dto.SongUpdateDTO;
import com.phungquocthai.symphony.entity.Category;
import com.phungquocthai.symphony.entity.Singer;
import com.phungquocthai.symphony.entity.Song;
import com.phungquocthai.symphony.exception.AppException;
import com.phungquocthai.symphony.mapper.CategoryMapper;
import com.phungquocthai.symphony.mapper.SongCreateMapper;
import com.phungquocthai.symphony.mapper.SongMapper;
import com.phungquocthai.symphony.repository.CategoryRepository;
import com.phungquocthai.symphony.repository.SingerRepository;
import com.phungquocthai.symphony.repository.SongRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class SongService {
	@Autowired
	SongRepository songRepository;
	
	@Autowired
	SingerRepository singerRepository;
	
	@Autowired
	CategoryRepository categoryRepository;
	
	@Autowired
	FileStorageService fileStorageService;
		
	@Autowired
	SongMapper songMapper;
	
	@Autowired
	SongCreateMapper songCreateMapper;
	
	@Autowired
	CategoryMapper categoryMapper;
	
	
	@Transactional
	public List<SongDTO> getFavoriteSongsOfUser(Integer user_id) {
		List<Song> songEntities = songRepository.getFavoriteSongsOfUser(user_id); 
		if(!songEntities.isEmpty()) {
			List<SongDTO> songs = songEntities.stream()
					.map(songEntity -> new SongDTO(songEntity))
					.collect(Collectors.toList());
			return songs;
		}
		return null;
	}
	
	public List<SongDTO> getRecentlyListenSongs(Integer user_id) {
		List<Song> songEntities = songRepository.getRecentlyListenSongs(user_id); 
		if(!songEntities.isEmpty()) {
			List<SongDTO> songs = songEntities.stream()
					.map(songEntity -> new SongDTO(songEntity))
					.collect(Collectors.toList());
			return songs;
		}
		return null;
	}
	
	@PreAuthorize("hasRole('SINGER')")
	public SongDTO create(SongCreateDTO dto, MultipartFile pathFile, MultipartFile lyricFile,
			MultipartFile lrcFile, MultipartFile songImgFile) {
		Song song = songCreateMapper.toEntity(dto);
		
		PathStorage musicStore = (dto.getIsVip()) ? PathStorage.MUSIC_VIP : PathStorage.MUSIC_NORMAL;
		String path = fileStorageService.storeFile(pathFile, musicStore);
		String lyric = fileStorageService.storeFile(lyricFile, PathStorage.LYRIC);
		String lrc = fileStorageService.storeFile(lrcFile, PathStorage.LRC);
		String songImg = fileStorageService.storeFile(songImgFile, PathStorage.MUSIC_IMG);
		
		song.setPath(path);
		song.setLyric(lyric);
		song.setLrc(lrc);
		song.setSong_img(songImg);
		
		Set<Singer> singers = new HashSet<Singer>(singerRepository.findAllById(dto.getSingersId()));
		Set<Category> categories = new HashSet<Category>(categoryRepository.findAllById(dto.getCategoriesId()));
		
		song.setCategories(categories);
		song.setSingers(singers);
		
		return songMapper.toDTO(songRepository.save(song));		
	}

	@PreAuthorize("hasRole('SINGER', 'ADMIN')")
	public void delete(Integer songId) {
		songRepository.deleteById(songId);
	}
	
	@PreAuthorize("hasRole('SINGER', 'ADMIN')")
	public SongDTO update(SongUpdateDTO dto, MultipartFile lyricFile,
			MultipartFile lrcFile, MultipartFile songImgFile) {
		
		Song song = songRepository.findById(dto.getSong_id())
				.orElseThrow(() -> new AppException(ErrorCode.SONG_NOT_EXISTED));
		
		if(lyricFile != null) {
			String lyric = fileStorageService.storeFile(lyricFile, PathStorage.LYRIC);
			song.setLyric(lyric);
		}
		
		if(lyricFile != null) {
			String lrc = fileStorageService.storeFile(lrcFile, PathStorage.LRC);
			song.setLrc(lrc);
		}
		
		if(songImgFile != null) {
			String songImg = fileStorageService.storeFile(songImgFile, PathStorage.MUSIC_IMG);
			song.setSong_img(songImg);
		}
		
		if(dto.getSingersId() != null) {
			List<Singer> singers = singerRepository.findAllById(dto.getSingersId());
			song.getSingers().addAll(singers);
		}
		
		if(dto.getCategoriesId() != null) {
			if(!dto.getCategoriesId().isEmpty()) {
				List<Category> categories = categoryRepository.findAllById(dto.getCategoriesId());
				song.setCategories(categories.stream().collect(Collectors.toSet()));
			}
		}
		
		return songMapper.toDTO(songRepository.save(song));
	}
	
	public Integer updateTotalListenOfSong(Integer songId) {
		Song song = songRepository.findById(songId)
				.orElseThrow(() -> new AppException(ErrorCode.SONG_NOT_EXISTED));
		song.setTotal_listens(song.getTotal_listens() + 1);
		songRepository.save(song);
		return song.getTotal_listens();
	}
	
	public SongDTO getSongById(Integer songId) {
		Song song = songRepository.findById(songId)
				.orElseThrow(() -> new AppException(ErrorCode.SONG_NOT_EXISTED));
		return songMapper.toDTO(song);
	}
	
	public List<SongDTO> getNewSongs() {
		List<Song> song = songRepository.findSongsFromLastYear();
		return songMapper.toListDTO(song);
	}
	
	public List<SongDTO> searchSongs(String key) {
		List<Song> songs = songRepository.searchSong(key);
		return songMapper.toListDTO(songs);
	}
	
	public List<SongDTO> getSongsByCategory(Integer categoryId) {
		List<Song> songs = songRepository.getSongsByCategory(categoryId);
		return songMapper.toListDTO(songs);
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	public List<SongDTO> findAll() {
		List<Song> songs = songRepository.findAll();
		return songMapper.toListDTO(songs);
	}
	
	public List<CategoryDTO> getAllCategories() {
		List<Category> categories = categoryRepository.findAll();
		return categoryMapper.toListDTO(categories);
	}
}
