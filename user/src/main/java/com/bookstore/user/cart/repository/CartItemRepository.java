package com.bookstore.user.cart.repository;

import com.bookstore.user.cart.entity.CartItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItemEntity, Long> {}
