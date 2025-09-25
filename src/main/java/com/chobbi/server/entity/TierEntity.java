package com.chobbi.server.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity(name = "tier")
public class TierEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @OneToMany(mappedBy = "tierEntity")
    private List<OptionsEntity> optionsEntities = new ArrayList<>();
}
