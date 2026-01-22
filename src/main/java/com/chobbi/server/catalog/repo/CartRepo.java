package com.chobbi.server.catalog.repo;

import com.chobbi.server.entity.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepo extends JpaRepository<CartEntity, Long> {
    Optional<CartEntity> findByAccountEntityId(Long accountId);
}
