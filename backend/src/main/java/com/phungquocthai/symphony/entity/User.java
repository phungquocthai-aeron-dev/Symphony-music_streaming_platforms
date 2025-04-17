package com.phungquocthai.symphony.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Integer userId;
	
	@Column(name = "full_name", length = 50)
	private String fullName;
	
	@Column(name = "birthday")
	private LocalDate birthday;
	
	@Column(name = "password", length = 255)
	private String password;
	
	@Column(name = "create_at")
	@CreationTimestamp
	private LocalDateTime create_at;
	
	@Column(name = "update_at")
	@UpdateTimestamp
	private LocalDateTime update_at;
	
	@Column(name = "phone", length = 20)
	private String phone;
	
	@Column(name = "gender")
	private int gender;
	
	@Column(name = "avatar", length = 255)
	private String avatar;
	
	@Column(name = "role")
	private String role;
		
	@OneToMany(mappedBy = "user")
	private List<Favorite> favorites;
	
	@OneToOne(mappedBy = "user")
	private Singer singer;
	
	@OneToMany(mappedBy = "user")
	private List<Listen> listens;
	
	@OneToMany(mappedBy = "user")
	@JsonManagedReference
	private List<Subscription> subscriptions;
	
	@OneToMany(mappedBy = "user")
	private List<Comment> comments;

	@Override
	public String toString() {
		return "User [userId=" + userId + ", fullName=" + fullName + ", birthday=" + birthday + ", password=" + password
				+ ", create_at=" + create_at + ", update_at=" + update_at + ", phone=" + phone + ", gender=" + gender
				+ ", avatar=" + avatar + ", role=" + role + ", favorites=" + favorites + ", singer=" + singer
				+ ", listens=" + listens + ", subscriptions=" + subscriptions + ", comments=" + comments + "]";
	}
	
	
}
