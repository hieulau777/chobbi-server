package com.chobbi.server.account.repo;

import com.chobbi.server.account.entity.RolesEntity;
import com.chobbi.server.account.enums.RoleEnums;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolesRepo extends JpaRepository<RolesEntity, Long> {

    Optional<RolesEntity> findByRole(RoleEnums role);
}
