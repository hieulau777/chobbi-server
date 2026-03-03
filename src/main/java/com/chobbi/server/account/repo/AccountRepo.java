package com.chobbi.server.account.repo;

import com.chobbi.server.account.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AccountRepo extends JpaRepository<AccountEntity, Long> {

    @Query("SELECT a FROM account a LEFT JOIN FETCH a.accountRoles ar LEFT JOIN FETCH ar.rolesEntity WHERE a.id = :id")
    Optional<AccountEntity> findByIdWithRoles(@Param("id") Long id);

    Optional<AccountEntity> findByEmail(String email);

    @Query("SELECT a FROM account a LEFT JOIN FETCH a.accountRoles ar LEFT JOIN FETCH ar.rolesEntity WHERE a.email = :email")
    Optional<AccountEntity> findByEmailWithRoles(@Param("email") String email);
}
