package com.chobbi.server.account.entity;

import com.chobbi.server.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity(name = "roles")
public class RolesEntity extends BaseEntity {
    private String name;
    @OneToMany(mappedBy = "rolesEntity")
    private List<AccountRolesEntity> accountRoles = new ArrayList<>();
}
