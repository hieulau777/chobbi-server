package com.chobbi.server.repo;

import com.chobbi.server.entity.TierEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TierRepo extends JpaRepository<TierEntity, Long> {
}
