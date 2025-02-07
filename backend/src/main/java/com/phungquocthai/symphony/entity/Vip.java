package com.phungquocthai.symphony.entity;

import java.math.BigDecimal;
import java.util.List;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "vip")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vip {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vip_id")
    private Integer vip_id;
    
    @Column(name = "vip_title", length = 255)
    private String vip_title;
    
    @Column(name = "description", length = 255)
    private String description;
    
    @Column(name = "duration_days")
    private int duration_days;
    
    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;
    
    @OneToMany(mappedBy = "vip")
    private List<Subscription> subscriptions;
}
