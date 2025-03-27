package com.bookstore.user.cart.repository;

import com.bookstore.user.cart.entity.CartEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<CartEntity, Long> {

  Optional<CartEntity> findByUser_Email(String userEmail);
}
