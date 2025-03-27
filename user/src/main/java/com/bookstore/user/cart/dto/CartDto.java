package com.bookstore.user.cart.dto;

import com.bookstore.user.cart.entity.CartItemEntity;
import com.bookstore.user.cart.enums.CartStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartDto {

  private Long cartId;
  private UUID userId;
  private List<CartItemEntity> cartItems;
  private BigDecimal totalPrice;
  private CartStatus status;
}
