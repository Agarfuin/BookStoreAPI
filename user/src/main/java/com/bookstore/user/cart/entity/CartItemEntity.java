package com.bookstore.user.cart.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cart_item")
public class CartItemEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "shopping_cart_id", nullable = false)
  @JsonBackReference("cart-items")
  private CartEntity shoppingCart;

  @Column(nullable = false)
  private UUID bookId;

  @Column(nullable = false)
  private String title;

  @Positive private int quantity;

  @PositiveOrZero private BigDecimal pricePerUnit;

  @Transient
  public BigDecimal getOverallPrice() {
    return pricePerUnit.multiply(BigDecimal.valueOf(quantity)).setScale(2, RoundingMode.HALF_UP);
  }
}
