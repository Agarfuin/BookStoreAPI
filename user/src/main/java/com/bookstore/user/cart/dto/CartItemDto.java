package com.bookstore.user.cart.dto;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDto {

  private UUID bookId;
  private String title;
  private int quantity;
  private BigDecimal pricePerUnit;
  private BigDecimal overallPrice;
}
