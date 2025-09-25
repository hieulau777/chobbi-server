package com.chobbi.server.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity(name = "options")
public class OptionsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @ManyToOne
    @JoinColumn(name = "tier_id")
    private TierEntity tierEntity;
}
