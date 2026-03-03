package com.chobbi.server.account.entity;

import com.chobbi.server.auth.enums.AuthProviderEnums;
import com.chobbi.server.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "social_accounts")
public class SocialAccountEntity extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private AccountEntity accountEntity;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false, length = 50)
    private AuthProviderEnums provider;

    @Column(name = "provider_account_id", nullable = false)
    private String providerAccountId;
}
