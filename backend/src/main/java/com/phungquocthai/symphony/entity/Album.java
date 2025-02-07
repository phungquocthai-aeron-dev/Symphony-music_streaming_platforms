package com.phungquocthai.symphony.entity;

import java.time.LocalDate;

import java.util.Set;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "album")
@Getter 
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Album {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "album_id")
    private Integer albumId;
    
    @Column(name = "release_date")
    private LocalDate releaseDate;
    
    @Column(name = "album_name", length = 255)
    private String albumName;
    
    @Column(name = "album_img", length = 255)
    private String albumImg;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "singer_id")
    private Singer singer;
    
    @ManyToMany
    @JoinTable(
    		name = "album_song",
			joinColumns = {
					@JoinColumn(name = "album_id")
			},
			inverseJoinColumns = {
					@JoinColumn(name = "song_id")
			}
    	)
    private Set<Song> songs;
}
