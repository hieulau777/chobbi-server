package com.chobbi.server.catalog.repo;

import com.chobbi.server.catalog.entity.VariationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface VariationRepo extends JpaRepository<VariationEntity, Long> {
    List<VariationEntity> findAllByProductEntity_IdAndDeletedAtIsNull(Long productId);
    List<VariationEntity> findAllByProductEntity_Id(Long productId);
    Optional<VariationEntity> findByIdAndDeletedAtIsNull(Long variationId);
    @Modifying
    @Transactional
    @Query("UPDATE #{#entityName} v SET v.stock = v.stock - :quantity " +
            "WHERE v.id = :id AND v.stock >= :quantity")
    int decreaseStock(@Param("id") Long id, @Param("quantity") Integer quantity);
}
