package com.chobbi.server.account.entity;

import com.chobbi.server.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity(name = "account")
public class AccountEntity extends BaseEntity {
    private String email;
    private String name;
    private String pwd;

    @OneToMany(mappedBy = "accountEntity")
    private List<AccountRolesEntity> accountRoles = new ArrayList<>();
}
