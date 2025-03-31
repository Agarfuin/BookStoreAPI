package com.bookstore.user.cart.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;
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

  @Column(nullable = false)
  private UUID itemId;

  @Column(nullable = false)
  private String title;

  @Positive
  @Column(nullable = false)
  private Integer quantity;

  @PositiveOrZero
  @Column(nullable = false)
  private BigDecimal pricePerUnit;

  @Transient
  public BigDecimal getOverallPrice() {
    return pricePerUnit.multiply(BigDecimal.valueOf(quantity)).setScale(2, RoundingMode.HALF_UP);
  }
}
