package com.phungquocthai.symphony.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.phungquocthai.symphony.dto.AlbumDTO;
import com.phungquocthai.symphony.dto.ApiResponse;
import com.phungquocthai.symphony.service.AlbumService;

@RestController
@RequestMapping("/album")
public class AlbumController {

    @Autowired
    private AlbumService albumService;

    @GetMapping("/singer")
    public ResponseEntity<ApiResponse<List<AlbumDTO>>> albumsOfSinger(@RequestParam("singerId") Integer singerId) {
        List<AlbumDTO> albums = albumService.getAlbumsOfSinger(singerId);
        ApiResponse<List<AlbumDTO>> response = new ApiResponse<>();
        response.setResult(albums);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<AlbumDTO>> createAlbum(
            @RequestParam("singerId") Integer singerId,
            @RequestParam("albumName") String albumName,
            @RequestPart(required = true, value = "imgFile") MultipartFile imgFile) {

        AlbumDTO dto = albumService.createAlbum(albumName, singerId, imgFile);

        ApiResponse<AlbumDTO> response = new ApiResponse<>();
        response.setResult(dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/update")
    public ResponseEntity<ApiResponse<String>> updateAlbum(
    		@RequestParam("albumId") Integer albumId,
            @RequestParam("albumName") String albumName,
            @RequestPart(required = false, value = "imgFile") MultipartFile imgFile) {

        albumService.updateAlbum(albumId, albumName, imgFile);
        ApiResponse<String> response = new ApiResponse<>();
        response.setResult("Album updated successfully");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<String>> deleteAlbum(@RequestParam("albumId") Integer albumId) {
        albumService.deleteAlbumWithSongs(albumId);
        ApiResponse<String> response = new ApiResponse<>();
        response.setResult("Album deleted successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/add-song")
    public ResponseEntity<ApiResponse<String>> addSongToAlbum(
            @RequestParam("albumId") Integer albumId,
            @RequestParam("songId") Integer songId) {

        if (albumService.isSongInAlbum(albumId, songId)) {
            ApiResponse<String> response = new ApiResponse<>();
            response.setResult("Bài hát đã tồn tại trong album");
            return ResponseEntity.ok(response);
        }

        albumService.addSongToAlbum(albumId, songId);
        ApiResponse<String> response = new ApiResponse<>();
        response.setResult("Thêm bài hát thành công");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/remove-song")
    public ResponseEntity<ApiResponse<String>> removeSongFromAlbum(
            @RequestParam("albumId") Integer albumId,
            @RequestParam("songId") Integer songId) {

        albumService.removeSongFromAlbum(albumId, songId);
        ApiResponse<String> response = new ApiResponse<>();
        response.setResult("Bài hát đã được xóa khỏi album");
        return ResponseEntity.ok(response);
    }
}
