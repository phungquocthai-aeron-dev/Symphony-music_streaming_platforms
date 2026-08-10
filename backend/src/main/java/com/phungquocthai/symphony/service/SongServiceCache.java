package com.phungquocthai.symphony.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
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

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SongServiceCache {

    @Autowired private SongRepository     songRepository;
    @Autowired private SingerRepository   singerRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private FavoriteRepository favoriteRepository;
    @Autowired private ListenRepository   listenRepository;
    @Autowired private PlaylistRepository playlistRepository;
    @Autowired private AlbumRepository    albumRepository;

    @Autowired private SongMapper       songMapper;
    @Autowired private SongCreateMapper songCreateMapper;
    @Autowired private TopSongMapper    topSongMapper;
    @Autowired private CategoryMapper   categoryMapper;

    @Autowired private FileStorageService fileStorageService;
    @Autowired private ExcelExportUtil    excelExportUtil;
    @Autowired private NotificationService notificationService;
    @Autowired private AISearchService    aiSearchService;
    @Autowired private CacheManager       cacheManager;

    public List<SongDTO> getFavoriteSongsOfUser(Integer userId) {
        List<Song> entities = songRepository.getFavoriteSongsOfUser(userId);
        if (entities.isEmpty()) return null;
        List<SongDTO> songs = songMapper.toListDTO(entities);
        songs.forEach(s -> s.setFavorite(true));
        return songs;
    }

    public void reverseFavorite(Integer songId, Integer userId) {
        Integer exists = favoriteRepository.findFavorited(songId, userId);
        if (exists == null) favoriteRepository.insertFavorite(songId, userId);
        else                favoriteRepository.deleteBySongIdAndUserId(songId, userId);
    }

    public List<SongDTO> getRecentlyListenSongs(Integer userId, Integer limit) {
        List<Song> entities = songRepository.findRecentlyListenedSongs(userId, limit);
        if (entities.isEmpty()) return null;
        List<SongDTO> songs = songMapper.toListDTO(entities);
        overlayFavorite(songs, userId);
        return songs;
    }

    public void listenedSong(Integer userId, Integer songId) {
        songRepository.addListened(userId, songId);
    }

    @Cacheable(value = "songDetail", key = "#songId")
    public SongDTO getSongByIdCached(Integer songId) {
        log.debug("[CACHE MISS] songDetail#{}", songId);
        Song entity = songRepository.findById(songId)
                .orElseThrow(() -> new AppException(ErrorCode.SONG_NOT_EXISTED));
        return songMapper.toDTO(entity);
    }

    public SongDTO getSongById(Integer songId) {
        SongDTO song = deepCopy(getSongByIdCached(songId));
        Integer userId = currentUserId();
        if (userId != null) {
            song.setFavorite(favoriteRepository.findByPrimaryKey(songId, userId) >= 1);
        }
        return song;
    }

    @Cacheable(value = "newSongs", key = "#limit")
    public List<SongDTO> getNewSongsCached(Integer limit) {
        log.debug("[CACHE MISS] newSongs limit={}", limit);
        return songMapper.toListDTO(songRepository.findSongsFromLastYear(limit));
    }

    public List<SongDTO> getNewSongs(Integer limit) {
        List<SongDTO> songs = deepCopyList(getNewSongsCached(limit));
        overlayFavoriteForCurrentUser(songs);
        return songs;
    }

    @Cacheable(value = "newSongs", key = "'single'")
    public SongDTO getNewSongCached() {
        log.debug("[CACHE MISS] newSongs#single");
        Song entity = songRepository.getNewSong()
                .orElseThrow(() -> new AppException(ErrorCode.SONG_NOT_EXISTED));
        return songMapper.toDTO(entity);
    }

    public SongDTO getNewSong() {
        SongDTO song = deepCopy(getNewSongCached());
        Integer userId = currentUserId();
        if (userId != null) {
            song.setFavorite(favoriteRepository.findByPrimaryKey(song.getSong_id(), userId) >= 1);
        }
        return song;
    }

    /**
     Không favorite
     */
    @Cacheable(value = "songSearch", key = "#songName.toLowerCase().trim()")
    public List<SongDTO> findBySongNameCached(String songName) {
        log.debug("[CACHE MISS] songSearch key={}", songName.toLowerCase().trim());
        return songMapper.toListDTO(songRepository.findBySongNameContainingIgnoreCase(songName));
    }

    /**
    Có favorite
     */
    public List<SongDTO> findBySongName(String songName) {
        List<SongDTO> cached = findBySongNameCached(songName);
        if (cached == null || cached.isEmpty()) return cached;
        List<SongDTO> songs = deepCopyList(cached);
//        overlayFavoriteForCurrentUser(songs);
        return songs;
    }

    /** không cache vì query phức tạp */
    public List<SongDTO> searchSongs(String key) {
        List<SongDTO> songs = songMapper.toListDTO(songRepository.searchSong(key));
        overlayFavoriteForCurrentUser(songs);
        return songs;
    }

    @Cacheable(value = "topSongs", key = "'hot:' + #limit")
    public List<SongDTO> getHotHitSongCached(Integer limit) {
        log.debug("[CACHE MISS] topSongs hot:{}", limit);
        return songMapper.toListDTO(songRepository.findHotHit(limit));
    }

    public List<SongDTO> getHotHitSong(Integer limit) {
        List<SongDTO> songs = deepCopyList(getHotHitSongCached(limit));
        overlayFavoriteForCurrentUser(songs);
        return songs;
    }

    @Cacheable(value = "topSongs", key = "'top:' + #limit")
    public List<TopSongDTO> getTopSongCached(Integer limit) {
        log.debug("[CACHE MISS] topSongs top:{}", limit);
        return buildTopSongDTOs(songRepository.getTopSongsLastHour(limit));
    }

    public List<TopSongDTO> getTopSong(Integer limit) {
        // TopSongDTO mutable tạo list mới để tránh mutate cache
        List<TopSongDTO> songs = new ArrayList<>(getTopSongCached(limit));
        Integer userId = currentUserId();
        if (userId != null) {
            List<Integer> ids = songs.stream()
                    .map(TopSongDTO::getSong_id)
                    .collect(Collectors.toList());
            Set<Integer> favIds = favoriteRepository.findFavoritedSongIds(userId, ids);
            songs.forEach(s -> s.setFavorite(favIds.contains(s.getSong_id())));
        }
        return songs;
    }

    @Cacheable(value = "songs", key = "'cat-id:' + #categoryId")
    public List<SongDTO> getSongsByCategoryIdCached(Integer categoryId) {
        log.debug("[CACHE MISS] songs cat-id:{}", categoryId);
        return songMapper.toListDTO(songRepository.getSongsByCategory(categoryId));
    }

    public List<SongDTO> getSongsByCategoryId(Integer categoryId) {
        List<SongDTO> songs = deepCopyList(getSongsByCategoryIdCached(categoryId));
        overlayFavoriteForCurrentUser(songs);
        return songs;
    }

    @Cacheable(value = "songs", key = "'cat-name:' + #categoryName.toLowerCase() + ':' + #limit")
    public List<SongDTO> getSongsByCategoryNameCached(String categoryName, Integer limit) {
        log.debug("[CACHE MISS] songs cat-name:{}:{}", categoryName, limit);
        return songMapper.toListDTO(songRepository.getSongsByCategory(categoryName, limit));
    }

    public List<SongDTO> getSongsByCategoryName(String categoryName, Integer limit) {
        log.info(categoryName);
        List<SongDTO> songs = deepCopyList(getSongsByCategoryNameCached(categoryName, limit));
        overlayFavoriteForCurrentUser(songs);
        return songs;
    }

    @Cacheable(value = "songs", key = "'singer:' + #singerId")
    public List<SongDTO> getBySingerIdCached(Integer singerId) {
        log.debug("[CACHE MISS] songs singer:{}", singerId);
        return songMapper.toListDTO(songRepository.findBySingerId(singerId));
    }

    public List<SongDTO> getBySingerId(Integer singerId) {
        List<SongDTO> songs = deepCopyList(getBySingerIdCached(singerId));
        overlayFavoriteForCurrentUser(songs);
        return songs;
    }

    @Cacheable(value = "songs", key = "'playlist:' + #playlistId")
    public List<SongDTO> findByPlaylistIdCached(Integer playlistId) {
        log.debug("[CACHE MISS] songs playlist:{}", playlistId);
        return songMapper.toListDTO(songRepository.findByPlaylistId(playlistId));
    }

    public List<SongDTO> findByPlaylistId(Integer playlistId) {
        List<SongDTO> songs = deepCopyList(findByPlaylistIdCached(playlistId));
        overlayFavoriteForCurrentUser(songs);
        return songs;
    }

    @Cacheable(value = "songs", key = "'album:' + #albumId")
    public List<SongDTO> findByAlbumIdCached(Integer albumId) {
        log.debug("[CACHE MISS] songs album:{}", albumId);
        return songMapper.toListDTO(songRepository.findByAlbumId(albumId));
    }

    public List<SongDTO> findByAlbumId(Integer albumId) {
        List<SongDTO> songs = deepCopyList(findByAlbumIdCached(albumId));
        overlayFavoriteForCurrentUser(songs);
        return songs;
    }

    public List<SongDTO> getByCategoryId(List<Integer> ids) {
        // ids là dynamic list → không cache (key không ổn định)
        List<SongDTO> songs = songMapper.toListDTO(songRepository.findAllByCategoryId(ids));
        overlayFavoriteForCurrentUser(songs);
        return songs;
    }

    public List<SongDTO> recommedSongs(List<Integer> ids, int size) {
        List<Song> songs = songRepository.findAllByCategoryId(ids);
        if (size > songs.size()) return songMapper.toListDTO(songs);
        Collections.shuffle(songs, new Random());
        return songMapper.toListDTO(new ArrayList<>(songs.subList(0, size)));
    }

    @Transactional
    public List<SongDTO> recommend(SongDTO song) {
        List<Integer> singerIds = song.getSingers().stream()
                .map(SingerDTO::getSinger_id)
                .collect(Collectors.toList());
        List<Song> entities = new ArrayList<>(songRepository.findAllByCategoryId(singerIds));

        int size = entities.size();
        if (size < 6) {
            List<Song> support = songRepository.findAllByCategoryIdNotIn(song.getCategoryIds(), singerIds);
            int n = 0;
            // [BUG-5 FIX] bản gốc dùng || → infinite loop; phải dùng &&
            while (size < 6 && n < support.size()) {
                entities.add(support.get(n++));
                size++;
            }
        }

        List<SongDTO> songs = songMapper.toListDTO(entities);
        // [BUG-3 FIX] bản gốc check song.getSong_id() (bài gốc) thay vì songItem.getSong_id()
        overlayFavoriteForCurrentUser(songs);
        return songs;
    }

    @Cacheable(value = "allCategories", key = "'all'")
    public List<CategoryDTO> getAllCategories() {
        log.debug("[CACHE MISS] allCategories");
        return categoryMapper.toListDTO(categoryRepository.findAll());
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<SongDTO> findAll() {
        // Admin list không cache (ít gọi, cần data mới nhất)
        List<SongDTO> songs = songMapper.toListDTO(songRepository.findAll());
        overlayFavoriteForCurrentUser(songs);
        return songs;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public byte[] exportToExcel() throws IOException {
        return excelExportUtil.exportToExcel(
                songMapper.toListDTO(songRepository.findAll()), null, "Danh sách bài hát");
    }

    public SongDTO create(SongCreateDTO dto, MultipartFile pathFile, MultipartFile lyricFile,
                          MultipartFile lrcFile, MultipartFile songImgFile) {
        Song song = songCreateMapper.toEntity(dto);

        PathStorage musicStore = dto.getIsVip() ? PathStorage.MUSIC_VIP : PathStorage.MUSIC_NORMAL;
        song.setPath(fileStorageService.storeFile(pathFile, musicStore));
        song.setLyric(fileStorageService.storeFile(lyricFile, PathStorage.LYRIC));
        song.setLrc(fileStorageService.storeFile(lrcFile, PathStorage.LRC));
        song.setSong_img(fileStorageService.storeFile(songImgFile, PathStorage.MUSIC_IMG));
        song.setActive(true);

        SongDTO songDTO = songMapper.toDTO(songRepository.save(song));

        for (Integer singerId : dto.getSingersId()) {
            songRepository.addSongToSinger(singerId, songDTO.getSong_id());
        }
        for (Integer categoryId : dto.getCategoryIds()) {
            songRepository.addSongToCategory(categoryId, songDTO.getSong_id());
        }

        // Bài mới → các list (new, top, category) có thể thay đổi
        clearCache("songs");
        clearCache("newSongs");
        clearCache("topSongs");
        clearCache("songSearch");

        notificationService.sendNotificationToAllUsers(1,
                "Bài hát " + song.getSongName() + " vừa mới phát hành. Trải nghiệm ngay!", "Bài hát mới");
        aiSearchService.updateAiDataAsync(song.getPath());
        return songDTO;
    }

    public SongDTO update(SongUpdateDTO dto, MultipartFile lyricFile,
                          MultipartFile lrcFile, MultipartFile songImgFile) {
        Song song = songRepository.findById(dto.getSong_id())
                .orElseThrow(() -> new AppException(ErrorCode.SONG_NOT_EXISTED));

        if (lyricFile   != null) song.setLyric(fileStorageService.storeFile(lyricFile,   PathStorage.LYRIC));
        if (lrcFile     != null) song.setLrc(fileStorageService.storeFile(lrcFile,       PathStorage.LRC));
        if (songImgFile != null) song.setSong_img(fileStorageService.storeFile(songImgFile, PathStorage.MUSIC_IMG));

        if (dto.getSingersId() != null) {
            List<Singer> singers = singerRepository.findAllById(dto.getSingersId());
            song.getSingers().addAll(singers);
        }

        song.setSongName(dto.getSongName());
        song.setAuthor(dto.getAuthor());
        song.setDuration(dto.getDuration());
        song.setIsVip(dto.getIsVip());

        if (dto.getCategoryIds() != null) {
            for (Integer categoryId : dto.getCategoryIds()) {
                if (singerRepository.isCategoryOfSong(categoryId, dto.getSong_id()) == null)
                    singerRepository.addCategoryForSong(categoryId, dto.getSong_id());
            }
        }

        if (dto.getSingersId() != null) {
            // [BUG-2 FIX] bản gốc duyệt dto.getCategoryIds() nhưng dùng làm singerId
            for (Integer singerId : dto.getSingersId()) {
                if (singerRepository.isPresented(singerId, dto.getSong_id()) == null)
                    singerRepository.addPresent(singerId, dto.getSong_id());
            }
        }

        SongDTO result = songMapper.toDTO(songRepository.save(song));

        // Evict chính xác entry của bài này + clear các list
        evict("songDetail", dto.getSong_id());
        clearCache("songs");
        clearCache("newSongs");
        clearCache("topSongs");
        clearCache("songSearch");

        return result;
    }

    public void delete(Integer singerId, Integer songId) {
        Song song = songRepository.findById(songId).orElseThrow();

        singerRepository.deletePresent(singerId, songId);
        singerRepository.deleteBySongIdAndSingerOwnership(songId, singerId);

        if (singerRepository.havePresent(songId) == null) {
            playlistRepository.deleteAllBySongId(songId);
            favoriteRepository.deleteAllBySongId(songId);
            listenRepository.deleteAllBySongId(songId);
            categoryRepository.deleteAllBySongId(songId);
            albumRepository.deleteAllBySongId(songId);
            songRepository.updateIsActive(songId, false);
            aiSearchService.removeAiData(song.getPath());

            evict("songDetail", songId);
            evict("newSongs", "single");
            clearCache("songs");
            clearCache("newSongs");
            clearCache("topSongs");
            clearCache("songSearch");
        }
    }

    public void disable(Integer songId) {
        Song song = songRepository.findById(songId).orElseThrow();
        aiSearchService.removeAiData(song.getPath());
        songRepository.updateIsActive(songId, false);
        evict("songDetail", songId);
        clearCache("songs");
        clearCache("topSongs");
        clearCache("newSongs");
    }

    public void enable(Integer songId) {
        Song song = songRepository.findById(songId).orElseThrow();
        aiSearchService.updateAiData(song.getPath());
        songRepository.updateIsActive(songId, true);
        evict("songDetail", songId);
        clearCache("songs");
        clearCache("topSongs");
        clearCache("newSongs");
    }

    public Integer updateTotalListenOfSong(Integer songId) {
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new AppException(ErrorCode.SONG_NOT_EXISTED));
        song.setTotal_listens(song.getTotal_listens() + 1);
        songRepository.save(song);
        evict("songDetail", songId);
        return song.getTotal_listens();
    }

    public List<ListeningStatsDTO> listeningStatistics(Integer songId) {
        List<Object[]> rows = songRepository.getHourlyListeningStats(songId);
        List<ListeningStatsDTO> result = new ArrayList<>();
        for (Object[] row : rows) {
            ListeningStatsDTO stat = new ListeningStatsDTO();
            stat.setSong_id((Integer) row[0]);
            if (row[1] instanceof java.sql.Date sqlDate) {
                stat.setListen_date(sqlDate.toLocalDate());
            } else if (row[1] instanceof java.util.Date utilDate) {
                stat.setListen_date(new java.sql.Date(utilDate.getTime()).toLocalDate());
            }
            stat.setHour((Integer) row[2]);
            stat.setTotal_listens_per_hour((Long) row[3]);
            result.add(stat);
        }
        return result;
    }

    public List<List<ListeningStatsDTO>> getTop3TrendingSongsPastHour(
            Integer top1Id, Integer top2Id, Integer top3Id) {
        return List.of(listeningStatistics(top1Id),
                listeningStatistics(top2Id),
                listeningStatistics(top3Id));
    }

    public List<List<ListeningStatsDTO>> getTop2TrendingSongsPastHour(
            Integer top1Id, Integer top2Id) {
        return List.of(listeningStatistics(top1Id), listeningStatistics(top2Id));
    }

    public List<List<ListeningStatsDTO>> getTop1TrendingSongsPastHour(Integer top1Id) {
        return List.of(listeningStatistics(top1Id));
    }

    /**
     * Batch overlay isFavorite cho một list SongDTO.
     *
     * Cơ chế:
     *   1. Gom tất cả songId trong list → 1 IN query lên DB
     *   2. DB trả về Set<Integer> các songId user đã favorite
     *   3. Dùng Set.contains() O(1) để set flag cho từng bài
     *
     * Kết quả: N bài = 1 DB round-trip thay vì N round-trips.
     */
    private void overlayFavorite(List<SongDTO> songs, Integer userId) {
        if (songs == null || songs.isEmpty() || userId == null) return;
        List<Integer> songIds = songs.stream()
                .map(SongDTO::getSong_id)
                .collect(Collectors.toList());
        Set<Integer> favIds = favoriteRepository.findFavoritedSongIds(userId, songIds);
        songs.forEach(s -> s.setFavorite(favIds.contains(s.getSong_id())));
    }

    /** Overlay favorite dựa vào user đang đăng nhập; no-op nếu anonymous. */
    private void overlayFavoriteForCurrentUser(List<SongDTO> songs) {
        Integer userId = currentUserId();
        if (userId != null) overlayFavorite(songs, userId);
    }

    /**
     * Lấy userId từ SecurityContext.
     * @return null nếu anonymous / chưa đăng nhập
     */
    private Integer currentUserId() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        if ("anonymousUser".equals(name)) return null;
        try {
            return Integer.parseInt(name);
        } catch (NumberFormatException e) {
            log.error("Cannot parse userId from token subject: {}", name);
            return null;
        }
    }

    /**
     * Deep copy một SongDTO.
     *
     * BẮT BUỘC trước khi overlay isFavorite:
     * Caffeine lưu object reference — nếu mutate trực tiếp cache entry,
     * lần sau cache hit sẽ trả về isFavorite của user trước.
     *
     * categories và singers là immutable list từ mapper → share reference an toàn.
     */
    private SongDTO deepCopy(SongDTO src) {
        return SongDTO.builder()
                .song_id(src.getSong_id())
                .songName(src.getSongName())
                .song_img(src.getSong_img())
                .total_listens(src.getTotal_listens())
                .path(src.getPath())
                .lyric(src.getLyric())
                .lrc(src.getLrc())
                .duration(src.getDuration())
                .releaseDate(src.getReleaseDate())
                .author(src.getAuthor())
                .isVip(src.getIsVip())
                .categories(src.getCategories())   // ref-share OK (read-only)
                .singers(src.getSingers())          // ref-share OK (read-only)
                .active(src.isActive())
                // isFavorite KHÔNG copy — default false, sẽ được overlay
                .build();
    }

    private List<SongDTO> deepCopyList(List<SongDTO> src) {
        if (src == null) return null;
        return src.stream().map(this::deepCopy).collect(Collectors.toList());
    }

    /** Evict một key cụ thể trong named cache. */
    private void evict(String cacheName, Object key) {
        var cache = cacheManager.getCache(cacheName);
        if (cache != null) cache.evict(key);
    }

    /** Clear toàn bộ entries của named cache (dùng khi không biết key nào bị ảnh hưởng). */
    private void clearCache(String cacheName) {
        var cache = cacheManager.getCache(cacheName);
        if (cache != null) cache.clear();
    }

    /**
     * Build TopSongDTO từ raw Object[] query result.
     * Tách riêng để có thể gọi trong @Cacheable method (không call service khác).
     */
    private List<TopSongDTO> buildTopSongDTOs(List<Object[]> rows) {
        List<TopSongDTO> result = new ArrayList<>();
        for (Object[] row : rows) {
            Song entity = new Song();
            entity.setSong_id((Integer) row[0]);
            entity.setAuthor((String)  row[1]);
            entity.setDuration((Integer) row[2]);
            entity.setIsVip((Boolean) row[3]);
            entity.setLrc((String)    row[4]);
            entity.setLyric((String)  row[5]);
            entity.setPath((String)   row[6]);
            if (row[7] instanceof java.sql.Date sqlDate) {
                entity.setReleaseDate(sqlDate.toLocalDate());
            } else if (row[7] instanceof java.util.Date utilDate) {
                entity.setReleaseDate(new java.sql.Date(utilDate.getTime()).toLocalDate());
            }
            entity.setSongName((String)  row[8]);
            entity.setSong_img((String)  row[9]);
            entity.setTotal_listens((Integer) row[10]);
            entity.setSingers(new LinkedHashSet<>(singerRepository.findBySongId(entity.getSong_id())));

            TopSongDTO dto = topSongMapper.toDTO(entity);
            dto.setTotal_listens_per_hour((Long) row[11]);
            result.add(dto);
        }
        return result;
    }
}