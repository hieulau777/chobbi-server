package com.chobbi.server.address.repo;

import com.chobbi.server.address.entity.AddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AddressRepo extends JpaRepository<AddressEntity, Long> {

    List<AddressEntity> findAllByAccountEntity_IdAndDeletedAtIsNull(Long accountId);

    Optional<AddressEntity> findByIdAndAccountEntity_IdAndDeletedAtIsNull(Long id, Long accountId);

    long countByAccountEntity_IdAndDeletedAtIsNull(Long accountId);

    List<AddressEntity> findAllByAccountEntity_IdAndDeletedAtIsNullAndIsDefaultTrue(Long accountId);
}


