package com.bookstore.user.repository;

import com.bookstore.user.entity.UserEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {

  Optional<UserEntity> findByEmail(String email);

  @Query(
      "SELECT u FROM UserEntity u JOIN UserConfirmationEntity uc ON u.id = uc.userId WHERE uc.token = :token")
  Optional<UserEntity> findByConfirmationToken(String token);
}
