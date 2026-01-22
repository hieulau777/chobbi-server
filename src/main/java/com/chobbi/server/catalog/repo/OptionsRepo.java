package com.chobbi.server.catalog.repo;

import com.chobbi.server.catalog.entity.OptionsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OptionsRepo extends JpaRepository<OptionsEntity, Long> {
    List<OptionsEntity> findAllByIdInAndDeletedAtIsNull(List<Long> optionIds);
}
