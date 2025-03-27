package com.bookstore.user.cart.entity;

import com.bookstore.user.cart.enums.CartStatus;
import com.bookstore.user.entity.UserEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "shopping_cart")
public class CartEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false, unique = true)
  private UserEntity user;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  @JoinColumn(name = "shopping_cart_id")
  private List<CartItemEntity> cartItems = new ArrayList<>();

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private CartStatus status;

  @Transient
  public BigDecimal getTotalPrice() {
    return cartItems.stream()
        .map(CartItemEntity::getOverallPrice)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }
}
