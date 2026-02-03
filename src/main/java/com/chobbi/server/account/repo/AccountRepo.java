package com.chobbi.server.account.repo;

import com.chobbi.server.account.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepo extends JpaRepository<Long, AccountEntity> {
}
