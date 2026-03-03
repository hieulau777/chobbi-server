package com.chobbi.server.account.repo;

import com.chobbi.server.account.entity.AccountRolesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRolesRepo extends JpaRepository<AccountRolesEntity, Long> {
}
