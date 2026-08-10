package com.phungquocthai.symphony.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.util.LinkedMultiValueMap;

import com.phungquocthai.symphony.dto.SearchResponseAI;
import com.phungquocthai.symphony.dto.SongDTO;
import com.phungquocthai.symphony.entity.Song;
import com.phungquocthai.symphony.mapper.SongMapper;
import com.phungquocthai.symphony.repository.SongRepository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Log4j2
public class AISearchService {
    private final SongRepository songRepository;
    private final SongMapper songMapper;
    private final RestTemplate restTemplate;

    private final HttpServletRequest httpServletRequest;

    @Value("${ai.service.url}")
    private String aiServiceUrl;

    private String extractBearerToken() {
        String authHeader = httpServletRequest.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        throw new RuntimeException("Missing or invalid Authorization header");
    }

    private HttpHeaders buildHeadersWithAuth() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setBearerAuth(extractBearerToken());
        return headers;
    }

    public List<SongDTO> searchByHumming(MultipartFile file) {
        try {
            File mp3File = convertToMp3(file);

            ByteArrayResource contentsAsResource = new ByteArrayResource(Files.readAllBytes(mp3File.toPath())) {
                @Override
                public String getFilename() {
                    return mp3File.getName();
                }
            };

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", contentsAsResource);

            HttpEntity<MultiValueMap<String, Object>> requestEntity =
                    new HttpEntity<>(body, buildHeadersWithAuth());

            String url = this.aiServiceUrl + "/search-humming";
            ResponseEntity<SearchResponseAI> response =
                    restTemplate.postForEntity(url, requestEntity, SearchResponseAI.class);

            List<Song> resultSongs = new ArrayList<>();
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                List<String> songNames = response.getBody().getSongs();
                for (String songName : songNames) {
                    songRepository.findByPathEndWithNoExtension(songName)
                            .ifPresent(resultSongs::add);
                }
            }

            mp3File.delete();
            return songMapper.toListDTO(resultSongs);

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void updateAiData(String songFileName) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(extractBearerToken());
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        restTemplate.postForEntity(
                aiServiceUrl + "/update-model?song_path=" + songFileName,
                entity,
                String.class
        );
    }

    @Async
    public void updateAiDataAsync(String songPath) {
        try {
            updateAiData(songPath);
        } catch (Exception e) {
            log.error("Failed to update AI data for song: {}", songPath, e);
        }
    }

    public void removeAiData(String songFileName) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(extractBearerToken());
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        restTemplate.postForEntity(
                aiServiceUrl + "/remove-song?song_path=" + songFileName,
                entity,
                String.class
        );
    }

    private File convertToMp3(MultipartFile inputFile) throws IOException, InterruptedException {
        File tempInput = File.createTempFile("input-", inputFile.getOriginalFilename());
        inputFile.transferTo(tempInput);
        File tempMp3 = File.createTempFile("converted-", ".mp3");

        ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg", "-y",
                "-i", tempInput.getAbsolutePath(),
                "-codec:a", "libmp3lame",
                "-qscale:a", "2",
                tempMp3.getAbsolutePath()
        );
        pb.redirectErrorStream(true);
        Process process = pb.start();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) System.out.println(line);
        }
        int exitCode = process.waitFor();
        if (exitCode != 0) throw new RuntimeException("FFmpeg convert failed: " + exitCode);
        tempInput.delete();
        return tempMp3;
    }
}