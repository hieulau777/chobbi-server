package com.chobbi.server.account.repo;

import com.chobbi.server.account.entity.SocialAccountEntity;
import com.chobbi.server.auth.enums.AuthProviderEnums;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SocialAccountRepo extends JpaRepository<SocialAccountEntity, Long> {

    Optional<SocialAccountEntity> findByProviderAndProviderAccountId(
            AuthProviderEnums provider, String providerAccountId);
}
