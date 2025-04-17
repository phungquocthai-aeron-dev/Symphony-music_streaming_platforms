package com.phungquocthai.symphony.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;


@Entity
@Table(name = "subscripttion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subscription {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "subscription_id")
    private Integer subscription_id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vip_id")
    private Vip vip;
    
    @Column(name = "start_date")
    private LocalDate start_date;
    
    @Column(name = "end_date")
    private LocalDate end_date;
    
    @Column(name = "status")
    private String status;
    
    @Column(name = "payment_id", length = 100)
    private String payment_id;
    
    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDate created_at;
}
