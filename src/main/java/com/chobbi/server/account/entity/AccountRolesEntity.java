package com.chobbi.server.account.entity;

import com.chobbi.server.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity(name = "account_roles")
public class AccountRolesEntity extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "account_id")
    private AccountEntity accountEntity;

    @ManyToOne
    @JoinColumn(name = "roles_id")
    private RolesEntity rolesEntity;
}
