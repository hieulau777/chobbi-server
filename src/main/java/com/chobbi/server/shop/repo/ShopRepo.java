package com.chobbi.server.shop.repo;

import com.chobbi.server.shop.entity.ShopEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShopRepo extends JpaRepository<ShopEntity, Long> {
    Optional<ShopEntity> findByIdAndDeletedAtIsNull(Long id);
    Optional<ShopEntity> findByProducts_IdAndDeletedAtIsNull(Long id);

    boolean existsByAccountEntity_Id(Long accountId);

    Optional<ShopEntity> findByAccountEntity_IdAndDeletedAtIsNull(Long accountId);

    @Query("SELECT s FROM shop s WHERE s.accountEntity.email = :email AND s.deletedAt IS NULL")
    Optional<ShopEntity> findByAccountEntity_EmailAndDeletedAtIsNull(@Param("email") String email);
}
