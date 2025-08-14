package com.phungquocthai.symphony.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.phungquocthai.symphony.constant.ErrorCode;
import com.phungquocthai.symphony.constant.PathStorage;
import com.phungquocthai.symphony.dto.CategoryDTO;
import com.phungquocthai.symphony.dto.ListeningStatsDTO;
import com.phungquocthai.symphony.dto.SingerDTO;
import com.phungquocthai.symphony.dto.SongCreateDTO;
import com.phungquocthai.symphony.dto.SongDTO;
import com.phungquocthai.symphony.dto.SongUpdateDTO;
import com.phungquocthai.symphony.dto.TopSongDTO;
import com.phungquocthai.symphony.entity.Category;
import com.phungquocthai.symphony.entity.Singer;
import com.phungquocthai.symphony.entity.Song;
import com.phungquocthai.symphony.exception.AppException;
import com.phungquocthai.symphony.mapper.CategoryMapper;
import com.phungquocthai.symphony.mapper.SongCreateMapper;
import com.phungquocthai.symphony.mapper.SongMapper;
import com.phungquocthai.symphony.mapper.TopSongMapper;
import com.phungquocthai.symphony.repository.AlbumRepository;
import com.phungquocthai.symphony.repository.CategoryRepository;
import com.phungquocthai.symphony.repository.FavoriteRepository;
import com.phungquocthai.symphony.repository.ListenRepository;
import com.phungquocthai.symphony.repository.PlaylistRepository;
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
	TopSongMapper topSongMapper;
	
	@Autowired
	CategoryMapper categoryMapper;
	
	@Autowired
	FavoriteRepository favoriteRepository;
	
	@Autowired
	ListenRepository listenRepository;
	
	@Autowired
	PlaylistRepository playlistRepository;
	
	@Autowired
	AlbumRepository albumRepository;
	
	@Autowired
	ExcelExportUtil excelExportUtil;
	
	@Autowired
	NotificationService notificationService;
	
	@Autowired
	AISearchService aiSearchService;
		
	public List<SongDTO> getFavoriteSongsOfUser(Integer userId) {
		List<Song> songEntities = songRepository.getFavoriteSongsOfUser(userId); 
		if(!songEntities.isEmpty()) {
			List<SongDTO> songs = songMapper.toListDTO(songEntities);
			songs.forEach(song -> song.setFavorite(true));
			return songs;
		}
		return null;
	}
	
	public void reverseFavorite(Integer songId, Integer userId) {		
		Integer check = favoriteRepository.findFavorited(songId, userId);
		if(check == null) favoriteRepository.insertFavorite(songId, userId);
		else favoriteRepository.deleteBySongIdAndUserId(songId, userId);
	}
	
//	public void removeFavorite(Integer songId, Integer userId) {
//		favoriteRepository.deleteBySongIdAndUserId(songId, userId);
//	}
	
	public List<SongDTO> getRecentlyListenSongs(Integer userId, Integer limit) {
		List<Song> songEntities = songRepository.findRecentlyListenedSongs(userId, limit); 
		List<SongDTO> songs = null;
		if(!songEntities.isEmpty()) {
			songs = songEntities.stream()
					.map(song -> songMapper.toDTO(song))
					.collect(Collectors.toList());
			songs.forEach(song -> song.setFavorite(isFavoriteSong(song.getSong_id(), userId)));
		}
		return songs;
	}
	
	public void listenedSong(Integer userId, Integer songId) {
		this.songRepository.addListened(userId, songId);
	}
	
	public List<SongDTO> findBySongName(String songName) {
		return this.songMapper.toListDTO(this.songRepository.findBySongNameContainingIgnoreCase(songName));
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	public byte[] exportToExcel() throws IOException {
        return excelExportUtil.exportToExcel(songMapper.toListDTO(songRepository.findAll()), null, "Danh sách bài hát");
    }
	
//	@PreAuthorize("hasRole('SINGER')")
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
		song.setActive(true);
		
		
		SongDTO songDTO = songMapper.toDTO(songRepository.save(song));		
		
		int singersSize = dto.getSingersId().size();
		for(int i = 0; i < singersSize; i++) {
			songRepository.addSongToSinger(dto.getSingersId().get(i), songDTO.getSong_id());
		}
		
		int categoriesSize = dto.getCategoryIds().size();
		for(int i = 0; i < categoriesSize; i++) {

			songRepository.addSongToCategory(dto.getCategoryIds().get(i), songDTO.getSong_id());
		}
		
		String message = "Bài hát " + song.getSongName() + " vừa mới phát hành. Trải nghiệm ngay!";
		
		notificationService.sendNotificationToAllUsers(1, message , "Bài hát mới");
		aiSearchService.updateAiData(song.getPath());
		return songDTO;
	}

//	@PreAuthorize("hasAnyRole('SINGER', 'ADMIN')")
	public void delete(Integer singerId, Integer songId) {
		Song song = songRepository.findById(songId).orElseThrow();
		
		singerRepository.deletePresent(singerId, songId);
		singerRepository.deleteBySongIdAndSingerOwnership(songId, singerId);
		Integer isOrphanhood = singerRepository.havePresent(songId);
		if(isOrphanhood == null) {
			playlistRepository.deleteAllBySongId(songId);
			favoriteRepository.deleteAllBySongId(songId);
			listenRepository.deleteAllBySongId(songId);
			categoryRepository.deleteAllBySongId(songId);
			albumRepository.deleteAllBySongId(songId);
			
//			songRepository.deleteById(songId);
			songRepository.updateIsActive(songId, false);
			aiSearchService.removeAiData(song.getPath());
			
//			String pathStorage = "D:\\nienluan\\Symphony-music-streaming-platforms\\backend\\uploads";
//
//			fileStorageService.deleteFile(pathStorage + song.getSong_img());
//			fileStorageService.deleteFile(pathStorage + song.getLyric());
//			fileStorageService.deleteFile(pathStorage + song.getLrc());
//			fileStorageService.deleteFile(pathStorage + song.getPath());
		}

	}
	
	public void disable(Integer songId) {
		Song song = songRepository.findById(songId).orElseThrow();
		aiSearchService.removeAiData(song.getPath());
		songRepository.updateIsActive(songId, false);
	}
	
	public void enable(Integer songId) {
		Song song = songRepository.findById(songId).orElseThrow();
		aiSearchService.updateAiData(song.getPath());
		songRepository.updateIsActive(songId, true);
	}
	
//	@PreAuthorize("hasRole('SINGER')")
	public SongDTO update(SongUpdateDTO dto, MultipartFile lyricFile,
			MultipartFile lrcFile, MultipartFile songImgFile) {
		Song song = songRepository.findById(dto.getSong_id())
				.orElseThrow(() -> new AppException(ErrorCode.SONG_NOT_EXISTED));
		if(lyricFile != null) {
			String lyric = fileStorageService.storeFile(lyricFile, PathStorage.LYRIC);
			song.setLyric(lyric);
		}
		
		if(lrcFile != null) {
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
		
		song.setSongName(dto.getSongName());
		song.setAuthor(dto.getAuthor());
		song.setDuration(dto.getDuration());
		song.setIsVip(dto.getIsVip());		
				
		if(dto.getCategoryIds() != null) {
			for(Integer categoryId : dto.getCategoryIds()) {
				Integer check = singerRepository.isCategoryOfSong(categoryId, dto.getSong_id());
				if(check == null) singerRepository.addCategoryForSong(categoryId, dto.getSong_id());
			}
		}
		
		if(dto.getSingersId() != null) {
			for(Integer singerId : dto.getCategoryIds()) {
				Integer check = singerRepository.isPresented(singerId, dto.getSong_id());
				if(check == null) singerRepository.addPresent(singerId, dto.getSong_id());
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
		Song songEntity = songRepository.findById(songId)
				.orElseThrow(() -> new AppException(ErrorCode.SONG_NOT_EXISTED));
		SongDTO song = songMapper.toDTO(songEntity);
		String userIdLoggedIn = getUserIdIfLoggedIn();
		if(userIdLoggedIn != null) {
			try {
				int userId = Integer.parseInt(userIdLoggedIn);
				boolean check = isFavoriteSong(songId, userId);
				log.info(check + "");

				if(check) {
					song.setFavorite(check);
				}
			} catch (NumberFormatException e) {
				log.error(e.getMessage());
			}
		}
		return song;
	}
	
	public SongDTO getNewSong() {
		Song songEntity = songRepository.getNewSong()
				.orElseThrow(() -> new AppException(ErrorCode.SONG_NOT_EXISTED));
		
		SongDTO song = songMapper.toDTO(songEntity);
		isFavoriteSong(song.getSong_id(), null);
		String userIdLoggedIn = getUserIdIfLoggedIn();
		if(userIdLoggedIn != null) {
			try {
				int userId = Integer.parseInt(userIdLoggedIn);
				boolean check = isFavoriteSong(song.getDuration(), userId);
				if(check) {
					song.setFavorite(check);
				}
			} catch (NumberFormatException e) {
				log.error(e.getMessage());
			}
		}
		
		return song;
	}
	
	public List<SongDTO> getNewSongs(Integer limit) {
		List<Song> songEntities = songRepository.findSongsFromLastYear(limit);
		List<SongDTO> songs = songMapper.toListDTO(songEntities);
		
		String userIdLoggedIn = getUserIdIfLoggedIn();
		if(userIdLoggedIn != null) {
			try {
				int userId = Integer.parseInt(userIdLoggedIn);
				
				songs.forEach(song -> song.setFavorite(isFavoriteSong(song.getSong_id(), userId)));

			} catch (NumberFormatException e) {
				log.error(e.getMessage());
			}
		}
		
		return songs;
	}
	
	public List<SongDTO> searchSongs(String key) {
		List<Song> songEntities = songRepository.searchSong(key);
		
		List<SongDTO> songs = songMapper.toListDTO(songEntities);
		String userIdLoggedIn = getUserIdIfLoggedIn();
		if(userIdLoggedIn != null) {
			try {
				int userId = Integer.parseInt(userIdLoggedIn);
				
				songs.forEach(song -> song.setFavorite(isFavoriteSong(song.getSong_id(), userId)));

			} catch (NumberFormatException e) {
				log.error(e.getMessage());
			}
		}
		return songs;
	}
	
	public List<SongDTO> getSongsByCategoryId(Integer categoryId) {
		List<Song> songEntities = songRepository.getSongsByCategory(categoryId);
		List<SongDTO> songs = songMapper.toListDTO(songEntities);
		String userIdLoggedIn = getUserIdIfLoggedIn();
		if(userIdLoggedIn != null) {
			try {
				int userId = Integer.parseInt(userIdLoggedIn);
				
				songs.forEach(song -> song.setFavorite(isFavoriteSong(song.getSong_id(), userId)));

			} catch (NumberFormatException e) {
				log.error(e.getMessage());
			}
		}
		return songs;
	}
	
	public List<SongDTO> getSongsByCategoryName(String categoryName, Integer limit) {
		List<Song> songEntities = songRepository.getSongsByCategory(categoryName, limit);
		log.info(categoryName);
		List<SongDTO> songs = songMapper.toListDTO(songEntities);
		String userIdLoggedIn = getUserIdIfLoggedIn();
		if(userIdLoggedIn != null) {
			try {
				int userId = Integer.parseInt(userIdLoggedIn);
				
				songs.forEach(song -> song.setFavorite(isFavoriteSong(song.getSong_id(), userId)));

			} catch (NumberFormatException e) {
				log.error(e.getMessage());
			}
		}
		return songs;
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	public List<SongDTO> findAll() {
		List<Song> songEntities = songRepository.findAll();
		List<SongDTO> songs = songMapper.toListDTO(songEntities);
		String userIdLoggedIn = getUserIdIfLoggedIn();
		if(userIdLoggedIn != null) {
			try {
				int userId = Integer.parseInt(userIdLoggedIn);
				
				songs.forEach(song -> song.setFavorite(isFavoriteSong(song.getSong_id(), userId)));

			} catch (NumberFormatException e) {
				log.error(e.getMessage());
			}
		}
		return songs;
	}
	
	public List<SongDTO> getBySingerId(Integer singerId) {
		List<Song> songEntities = songRepository.findBySingerId(singerId);
		List<SongDTO> songs = songMapper.toListDTO(songEntities);
		String userIdLoggedIn = getUserIdIfLoggedIn();
		if(userIdLoggedIn != null) {
			try {
				int userId = Integer.parseInt(userIdLoggedIn);
				
				songs.forEach(song -> song.setFavorite(isFavoriteSong(song.getSong_id(), userId)));

			} catch (NumberFormatException e) {
				log.error(e.getMessage());
			}
		}
		return songs;
	}
	
	public List<CategoryDTO> getAllCategories() {
		List<Category> categories = categoryRepository.findAll();
		return categoryMapper.toListDTO(categories);
	}
	
	public List<SongDTO> recommedSongs(List<Integer> ids, int size) {
		List<Song> songs = songRepository.findAllByCategoryId(ids);
		if(size > songs.size()) return songMapper.toListDTO(songs);
		
		Collections.shuffle(songs, new Random());

	    return songMapper.toListDTO(new ArrayList<Song>(songs.subList(0, size)));
	}
	
	public List<SongDTO> findByPlaylistId(Integer playlistId) {
		List<Song> songEntities = songRepository.findByPlaylistId(playlistId);
		List<SongDTO> songs = songMapper.toListDTO(songEntities);
		String userIdLoggedIn = getUserIdIfLoggedIn();
		if(userIdLoggedIn != null) {
			try {
				int userId = Integer.parseInt(userIdLoggedIn);
				
				songs.forEach(song -> song.setFavorite(isFavoriteSong(song.getSong_id(), userId)));

			} catch (NumberFormatException e) {
				log.error(e.getMessage());
			}
		}
		return songs;
	}
	
	public List<SongDTO> findByAlbumId(Integer albumId) {
		List<Song> songEntities = songRepository.findByAlbumId(albumId);
		List<SongDTO> songs = songMapper.toListDTO(songEntities);
		String userIdLoggedIn = getUserIdIfLoggedIn();
		if(userIdLoggedIn != null) {
			try {
				int userId = Integer.parseInt(userIdLoggedIn);
				
				songs.forEach(song -> song.setFavorite(isFavoriteSong(song.getSong_id(), userId)));

			} catch (NumberFormatException e) {
				log.error(e.getMessage());
			}
		}
		return songs;
	}
	
	
	public List<SongDTO> getHotHitSong(Integer limit) {
		List<Song> songEntities = songRepository.findHotHit(limit);
		List<SongDTO> songs = songMapper.toListDTO(songEntities);
		String userIdLoggedIn = getUserIdIfLoggedIn();
		if(userIdLoggedIn != null) {
			try {
				int userId = Integer.parseInt(userIdLoggedIn);
				
				songs.forEach(song -> song.setFavorite(isFavoriteSong(song.getSong_id(), userId)));

			} catch (NumberFormatException e) {
				log.error(e.getMessage());
			}
		}
		return songs;
	}
	
	public List<SongDTO> getByCategoryId(List<Integer> ids) {
		List<Song> songEntities = songRepository.findAllByCategoryId(ids);
		List<SongDTO> songs = songMapper.toListDTO(songEntities);
		String userIdLoggedIn = getUserIdIfLoggedIn();
		if(userIdLoggedIn != null) {
			try {
				int userId = Integer.parseInt(userIdLoggedIn);
				
				songs.forEach(song -> song.setFavorite(isFavoriteSong(song.getSong_id(), userId)));

			} catch (NumberFormatException e) {
				log.error(e.getMessage());
			}
		}
		return songs;
	}
	
	@Transactional
	public List<SongDTO> recommend(SongDTO song) {
		List<Integer> ids = song.getSingers().stream().map(SingerDTO::getSinger_id).collect(Collectors.toList());
		List<Song> songEntities = songRepository.findAllByCategoryId(ids);
		
		int size = songEntities.size();
		int n = 0;
		if(size < 6) {
			List<Song> support = songRepository.findAllByCategoryIdNotIn(song.getCategoryIds(), ids);
			int sizeSupport = support.size();
			
			if(support.size() > 0) {
				while(size < 6 || n < sizeSupport) {
					songEntities.add(support.get(n++));
					size++;
				}
			}
		}
		
		List<SongDTO> songs = songMapper.toListDTO(songEntities);
		String userIdLoggedIn = getUserIdIfLoggedIn();
		if(userIdLoggedIn != null) {
			try {
				int userId = Integer.parseInt(userIdLoggedIn);
				
				songs.forEach(songItem -> songItem.setFavorite(isFavoriteSong(song.getSong_id(), userId)));

			} catch (NumberFormatException e) {
				log.error(e.getMessage());
			}
		}
		return songs;
	}
	
//	public List<TopSongDTO> getTopSong(Integer limit) {
//		List<Object[]> rows = songRepository.getTopSongsLastHour(limit);
//		List<TopSongDTO> songs = new ArrayList<TopSongDTO>();
//		for (Object[] row : rows) {
//		    Song songEntity = (Song) row[0];
//		    TopSongDTO song = topSongMapper.toDTO(songEntity);
//		    song.setTotal_listens_per_hour(((Long) row[1]).longValue());
//		    songs.add(song);
//		}
//		String userIdLoggedIn = getUserIdIfLoggedIn();
//		if(userIdLoggedIn != null) {
//			try {
//				int userId = Integer.parseInt(userIdLoggedIn);
//				
//				songs.forEach(song -> song.setFavorite(isFavoriteSong(song.getSong_id(), userId)));
//
//			} catch (NumberFormatException e) {
//				log.error(e.getMessage());
//			}
//		}
//		return null;
//	}
	
	public List<TopSongDTO> getTopSong(Integer limit) {
	    List<Object[]> rows = songRepository.getTopSongsLastHour(limit);
	    List<TopSongDTO> songs = new ArrayList<>();

	    for (Object[] row : rows) {
	        // Mapping các cột theo thứ tự trong bảng song (11 cột) + 1 cột cuối là count
	    	
//	    	System.out.println((Integer) row[0] + (String) row[1] + (Integer) row[2] + (Boolean) row[3]
//	    			+ (Boolean) row[3] + (String) row[4] + (String) row[5] + (String) row[6]
//	    					+ (Date) row[7] + (String) row[8] + (String) row[9] + "AAAAAAAAA");
	    	
	        Song songEntity = new Song();
	        songEntity.setSong_id((Integer) row[0]);
	        songEntity.setAuthor((String) row[1]);
	        songEntity.setDuration((Integer) row[2]);
	        songEntity.setIsVip((Boolean) row[3]);
	        songEntity.setLrc((String) row[4]);
	        songEntity.setLyric((String) row[5]);
	        songEntity.setPath((String) row[6]);
	        if (row[7] instanceof java.sql.Date) {
	            // Đối với java.sql.Date, sử dụng toLocalDate() trực tiếp
	            songEntity.setReleaseDate(((java.sql.Date) row[7]).toLocalDate());
	        } else if (row[7] instanceof java.util.Date) {
	            // Đối với java.util.Date thông thường
	            java.util.Date utilDate = (java.util.Date) row[7];
	            songEntity.setReleaseDate(new java.sql.Date(utilDate.getTime()).toLocalDate());
	        }
	        songEntity.setSongName((String) row[8]);
	        songEntity.setSong_img((String) row[9]);
	        songEntity.setTotal_listens((Integer) row[10]);
	        List<Singer> singers = singerRepository.findBySongId(songEntity.getSong_id());
	        songEntity.setSingers(new LinkedHashSet<Singer>(singers));
	        TopSongDTO song = topSongMapper.toDTO(songEntity);
	        
//	        song.setSong_id((Integer) row[0]);
//	        song.setAuthor((String) row[1]);
//	        song.setDuration((Integer) row[2]);
//	        
//	        
//	        song.setIsVip((Boolean) row[3]);                     // is_vip
//	        song.setLrc((String) row[4]);                        // lrc
//	        song.setLyric((String) row[5]);                      // lyric
//	        song.setPath((String) row[6]);                       // path
//
////	        // Xử lý release_date
////	        if (row[7] instanceof java.sql.Date) {
////	            song.setReleaseDate(((java.sql.Date) row[7]).toLocalDate());
////	        } else if (row[7] instanceof java.util.Date) {
////	            java.util.Date utilDate = (java.util.Date) row[7];
////	            song.setReleaseDate(new java.sql.Date(utilDate.getTime()).toLocalDate());
////	        } else {
////	            log.warn("release_date không hợp lệ: " + row[7]);
////	            continue;
////	        }
//
//	        song.setSongName((String) row[8]);                   // song_name
//	        song.setSong_img((String) row[9]);                   // song_img
//	        song.setTotal_listens((Integer) row[10]);

	        song.setTotal_listens_per_hour(((Long) row[11]).longValue());
		    songs.add(song);
	    }

	    // Đánh dấu yêu thích nếu user đã login
	    String userIdLoggedIn = getUserIdIfLoggedIn();
	    if (userIdLoggedIn != null) {
	        try {
	            int userId = Integer.parseInt(userIdLoggedIn);
	            songs.forEach(song -> song.setFavorite(isFavoriteSong(song.getSong_id(), userId)));
	        } catch (NumberFormatException e) {
	            log.error("Invalid user ID: " + e.getMessage());
	        }
	    }

	    return songs;
	}


	public List<ListeningStatsDTO> listeningStatistics(Integer songId) {
		List<Object[]> rows = songRepository.getHourlyListeningStats(songId);
		List<ListeningStatsDTO> songs = new ArrayList<ListeningStatsDTO>();
		for (Object[] row : rows) {
			ListeningStatsDTO song = new ListeningStatsDTO();
		    song.setSong_id(((Integer) row[0]).intValue());
		    if (row[1] instanceof java.sql.Date) {
	            // Đối với java.sql.Date, sử dụng toLocalDate() trực tiếp
		    	song.setListen_date(((java.sql.Date) row[1]).toLocalDate());
	        } else if (row[1] instanceof java.util.Date) {
	            // Đối với java.util.Date thông thường
	            java.util.Date utilDate = (java.util.Date) row[1];
	            song.setListen_date(new java.sql.Date(utilDate.getTime()).toLocalDate());
	        }
		    
		    song.setHour(((Integer) row[2]).intValue());
		    song.setTotal_listens_per_hour(((Long) row[3]).longValue());
		    songs.add(song);
		}
		return songs;
	}
	
	public List<List<ListeningStatsDTO>> getTop3TrendingSongsPastHour(
			Integer top1Id, Integer top2Id, Integer top3Id) {
		List<ListeningStatsDTO> top1 = listeningStatistics(top1Id);
		List<ListeningStatsDTO> top2 = listeningStatistics(top2Id);
		List<ListeningStatsDTO> top3 = listeningStatistics(top3Id);
		
		List<List<ListeningStatsDTO>> result = new ArrayList<List<ListeningStatsDTO>>();
		result.add(top1);
		result.add(top2);
		result.add(top3);
		
		return result;
	}
	
	public List<List<ListeningStatsDTO>> getTop2TrendingSongsPastHour(
			Integer top1Id, Integer top2Id) {
		List<ListeningStatsDTO> top1 = listeningStatistics(top1Id);
		List<ListeningStatsDTO> top2 = listeningStatistics(top2Id);
		
		List<List<ListeningStatsDTO>> result = new ArrayList<List<ListeningStatsDTO>>();
		result.add(top1);
		result.add(top2);
		
		return result;
	}
	
	public List<List<ListeningStatsDTO>> getTop1TrendingSongsPastHour(Integer top1Id) {
		List<ListeningStatsDTO> top1 = listeningStatistics(top1Id);
		
		List<List<ListeningStatsDTO>> result = new ArrayList<List<ListeningStatsDTO>>();
		result.add(top1);
		
		return result;
	}
	
	private boolean isFavoriteSong(Integer songId, Integer userId) {
		int isExisted = favoriteRepository.findByPrimaryKey(songId, userId);
		if(isExisted >= 1) return true;
		
//	    if(userId == null) {
//	    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//		    if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
//		        Jwt jwt = (Jwt) authentication.getPrincipal();
//		        if(jwt != null) {
//		        	Integer usId = Integer.valueOf(jwt.getSubject());
//		        	int isExisted2 = favoriteRepository.findByPrimaryKey(songId, usId);
//		    		if(isExisted2 >= 1) return true;
//		        }
//		    }
//	    }
	    
		return false;
	}
	
	private String getUserIdIfLoggedIn() {
		String userId = SecurityContextHolder.getContext().getAuthentication().getName();
		if(userId.equals("anonymousUser")) {
			return null;
		}
		return userId;
	}
}
