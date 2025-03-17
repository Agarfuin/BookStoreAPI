package com.bookstore.notification.repository;

import com.bookstore.notification.entity.NotificationEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

  Optional<NotificationEntity> findByToUserId(UUID toUserId);
}
