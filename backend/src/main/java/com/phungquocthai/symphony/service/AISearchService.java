package com.phungquocthai.symphony.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.*;

import com.phungquocthai.symphony.dto.SearchResponseAI;
import com.phungquocthai.symphony.dto.SongDTO;
import com.phungquocthai.symphony.entity.Song;
import com.phungquocthai.symphony.mapper.SongMapper;
import com.phungquocthai.symphony.repository.SongRepository;
import org.springframework.util.MultiValueMap;
import org.springframework.util.LinkedMultiValueMap;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AISearchService {
	private final SongRepository songRepository;
    private final SongMapper songMapper;
    private final RestTemplate restTemplate;

    @Value("${ai.service.url}")
    private String aiServiceUrl;

    public List<SongDTO> searchByHumming(MultipartFile file) {
        try {
            System.out.println("Received file: " + file.getOriginalFilename() + ", size: " + file.getSize());

            // Convert sang MP3 trước khi gửi
            File mp3File = convertToMp3(file);
            System.out.println("Converted file to MP3: " + mp3File.getAbsolutePath());

            // Chuẩn bị ByteArrayResource từ file mp3
            ByteArrayResource contentsAsResource = new ByteArrayResource(Files.readAllBytes(mp3File.toPath())) {
                @Override
                public String getFilename() {
                    return mp3File.getName();
                }
            };

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", contentsAsResource);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            String url = this.aiServiceUrl + "/search-humming";
            System.out.println("Sending POST request to AI service: " + url);

            ResponseEntity<SearchResponseAI> response = restTemplate.postForEntity(url, requestEntity, SearchResponseAI.class);
            System.out.println("AI service response status: " + response.getStatusCode());

            List<Song> resultSongs = new ArrayList<>();
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                List<String> songNames = response.getBody().getSongs();
                System.out.println("AI service returned songs: " + songNames);

                for (String songName : songNames) {
                    songRepository.findByPathEndWithNoExtension(songName)
                        .ifPresent(song -> {
                            System.out.println("Found song in DB: " + song.getSongName());
                            resultSongs.add(song);
                        });
                }
            }

            // Xóa file mp3 tạm
            mp3File.delete();

            return songMapper.toListDTO(resultSongs);

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


    private File convertToMp3(MultipartFile inputFile) throws IOException, InterruptedException {
        // Tạo file tạm .wav từ MultipartFile
        File tempInput = File.createTempFile("input-", inputFile.getOriginalFilename());
        inputFile.transferTo(tempInput);

        // Đường dẫn file mp3 đầu ra
        File tempMp3 = File.createTempFile("converted-", ".mp3");

        // Tạo process builder
        ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg",
                "-y",
                "-i", tempInput.getAbsolutePath(),
                "-codec:a", "libmp3lame",
                "-qscale:a", "2",
                tempMp3.getAbsolutePath()
        );
        pb.redirectErrorStream(true); // Gộp stdout & stderr

        Process process = pb.start();

        // In log từ ffmpeg để debug
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("FFmpeg convert failed with code: " + exitCode);
        }

        // Xóa file input tạm
        tempInput.delete();

        return tempMp3;
    }

    public void updateAiData(String songFileName) {
        restTemplate.postForEntity(
            aiServiceUrl + "/update-model?song_path=" + songFileName,
            null,
            String.class
        );
    }

    public void removeAiData(String songFileName) {
        restTemplate.postForEntity(
            aiServiceUrl + "/remove-song?song_path=" + songFileName,
            null,
            String.class
        );
    }

}
