package com.chobbi.server.account.entity;

import com.chobbi.server.account.enums.RoleEnums;
import com.chobbi.server.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity(name = "roles")
public class RolesEntity extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false, length = 50)
    private RoleEnums role;
    @OneToMany(mappedBy = "rolesEntity")
    private List<AccountRolesEntity> accountRoles = new ArrayList<>();
}
