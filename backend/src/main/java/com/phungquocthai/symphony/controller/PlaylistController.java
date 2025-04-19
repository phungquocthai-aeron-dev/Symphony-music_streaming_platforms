package com.phungquocthai.symphony.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.phungquocthai.symphony.dto.ApiResponse;
import com.phungquocthai.symphony.dto.PlaylistDTO;
import com.phungquocthai.symphony.service.PlaylistService;

@RestController
@RequestMapping("/playlist")
public class PlaylistController {
	
	@Autowired
	private PlaylistService playlistService;
	
	@GetMapping("/user")
	public ResponseEntity<ApiResponse<List<PlaylistDTO>>> playlistOfUser(@RequestParam(value = "userId") Integer userId) {
		List<PlaylistDTO> playlists = playlistService.getPlaylistOfUser(userId);
		ApiResponse<List<PlaylistDTO>> apiResponse = new ApiResponse<List<PlaylistDTO>>();
		apiResponse.setResult(playlists);
		return ResponseEntity.ok(apiResponse);
	}
	
	@PostMapping("/create")
	public ResponseEntity<ApiResponse<PlaylistDTO>> createPlaylist(
	        @RequestParam("userId") Integer userId,
	        @RequestParam("playlistName") String playlistName) {

	    PlaylistDTO dto = PlaylistDTO.builder()
	            .playlistName(playlistName)
	            .build();

	    PlaylistDTO result = playlistService.createPlaylist(dto, userId);
	    ApiResponse<PlaylistDTO> response = new ApiResponse<>();
	    response.setResult(result);
	    return ResponseEntity.ok(response);
	}
	
	@PostMapping("/update")
    public ResponseEntity<ApiResponse<String>> updatePlaylist(
    		@RequestParam("playlistId") Integer playlistId,
    		@RequestParam("playlistName") String playlistName) {
        playlistService.update(playlistId, playlistName);
        ApiResponse<String> response = new ApiResponse<>();
        response.setResult("Playlist updated successfully");
        return ResponseEntity.ok(response);
    }

	@DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<String>> deletePlaylist(@RequestParam("playlistId") Integer playlistId) {
        playlistService.deletePlaylistWithSongs(playlistId);
        ApiResponse<String> response = new ApiResponse<>();
        response.setResult("Playlist deleted successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/add-song")
    public ResponseEntity<ApiResponse<String>> addSongToPlaylist(
            @RequestParam("playlistId") Integer playlistId,
            @RequestParam("songId") Integer songId) {
        playlistService.addSongToPlaylist(playlistId, songId);
        ApiResponse<String> response = new ApiResponse<>();
        response.setResult("Song added to playlist successfully");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/remove-song")
    public ResponseEntity<ApiResponse<String>> removeSongFromPlaylist(
            @RequestParam("playlistId") Integer playlistId,
            @RequestParam("songId") Integer songId) {
        playlistService.removeSongFromPlaylist(playlistId, songId);
        ApiResponse<String> response = new ApiResponse<>();
        response.setResult("Song removed from playlist successfully");
        return ResponseEntity.ok(response);
    }
	
}
