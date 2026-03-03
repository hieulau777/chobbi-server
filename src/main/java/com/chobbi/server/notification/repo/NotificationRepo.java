package com.chobbi.server.notification.repo;

import com.chobbi.server.notification.entity.NotificationEntity;
import com.chobbi.server.notification.enums.TargetRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepo extends JpaRepository<NotificationEntity, Long> {

    List<NotificationEntity> findByAccountEntityIdAndTargetRoleAndDeletedAtIsNullOrderByCreatedAtDesc(
            Long accountId, TargetRole targetRole);

    @Query("SELECT n FROM notifications n JOIN FETCH n.orderEntity " +
           "WHERE n.accountEntity.id = :accountId AND n.targetRole = :targetRole AND n.deletedAt IS NULL " +
           "ORDER BY n.createdAt DESC")
    List<NotificationEntity> findByAccountIdAndTargetRoleWithOrder(@Param("accountId") Long accountId,
                                                                   @Param("targetRole") TargetRole targetRole);
}
