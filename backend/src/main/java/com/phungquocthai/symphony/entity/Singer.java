package com.phungquocthai.symphony.entity;

import java.util.List;
import java.util.Set;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "singer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Singer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "singer_id")
    private Integer singer_id;
    
    @Column(name = "stage_name", length = 50)
    private String stage_name;
    
    @Column(name = "followers")
    private int followers;
    
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @OneToMany(mappedBy = "singer")
    private List<Album> albums;
    
    @ManyToMany
    @JoinTable(
			name = "present",
			joinColumns = {
					@JoinColumn(name = "singer_id")
			},
			inverseJoinColumns = {
					@JoinColumn(name = "song_id")
			}
		)
    private Set<Song> songs;

}
