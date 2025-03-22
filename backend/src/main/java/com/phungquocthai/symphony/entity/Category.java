package com.phungquocthai.symphony.entity;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "category")
@Getter 
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Integer category_id;
    
    @Column(name = "category_name", length = 50)
    private String category_name;
    
    @ManyToMany
    @JoinTable(
			name = "category_song",
			joinColumns = {
					@JoinColumn(name = "category_id")
			},
			inverseJoinColumns = {
					@JoinColumn(name = "song_id")
			}
		)
    @JsonBackReference
    private Set<Song> songs;
}
