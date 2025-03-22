package com.phungquocthai.symphony.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "listen")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Listen {
	@Id
	@Column(name = "listen_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int listen_id;
	
	@Column(name = "listen_at")
	@CreationTimestamp
	private LocalDateTime listen_at;
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
	
	@ManyToOne
	@JoinColumn(name = "song_id")
	private Song song;
}
