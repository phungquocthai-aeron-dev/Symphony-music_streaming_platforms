package com.phungquocthai.symphony.entity;

import java.time.LocalDate;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.List;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "song")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Song {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "song_id")
    private Integer song_id;
    
    @Column(name = "song_name", length = 100)
    private String songName;
    
    @Column(name = "song_img", length = 255)
    private String song_img;
    
    @Column(name = "listens")
    private Integer total_listens;
    
    @Column(name = "path", length = 255)
    private String path;
    
    @Column(name = "lyric", length = 255)
    private String lyric;
    
    @Column(name = "lrc", length = 255)
    private String lrc;
    
    @Column(name = "duration")
    private Integer duration;
    
    @Column(name = "release_date")
    private LocalDate releaseDate;
    
    @Column(name = "author", length = 255)
    private String author;
    
    @Column(name = "is_vip")
    private Boolean isVip;
    
    @ManyToMany(mappedBy = "songs")
    private Set<Playlist> playlist;
    
    @ManyToMany(mappedBy = "songs")
    private Set<Album> albums;
    
    @ManyToMany(mappedBy = "songs")
    @JsonManagedReference
    private Set<Category> categories;
    
    @ManyToMany(mappedBy = "songs")
    @JsonManagedReference
    private Set<Singer> singers;
    
    @OneToMany(mappedBy = "song")
    private List<Comment> comments;
    
    @OneToMany(mappedBy = "song")
    private List<Favorite> favorites;
    
    @OneToMany(mappedBy = "song")
    private List<Listen> listens;

}
