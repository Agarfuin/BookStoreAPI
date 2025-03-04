package com.bookstore.user.repository;

import com.bookstore.user.entity.UserConfirmationEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserConfirmationRepository extends JpaRepository<UserConfirmationEntity, Long> {

  Optional<UserConfirmationEntity> findByToken(String token);
}
