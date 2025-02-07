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
@Table(name = "playlist")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Playlist {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "playlist_id")
    private Integer playlist_id;
    
    @Column(name = "create_at")
    private LocalDate create_at;
    
    @Column(name = "playlist_name", length = 255)
    private String playlist_name;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @ManyToMany
    @JoinTable(
			name = "playlist_song",
			joinColumns = {
					@JoinColumn(name = "playlist_id")
			},
			inverseJoinColumns = {
					@JoinColumn(name = "song_id")
			}
		)
    private Set<Song> songs;
}
