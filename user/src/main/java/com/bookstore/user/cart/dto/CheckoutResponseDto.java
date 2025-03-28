package com.bookstore.user.cart.dto;

import com.bookstore.user.cart.enums.PaymentMethod;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutResponseDto {

  private LocalDateTime checkoutDate;
  private Long cartId;
  private String address;
  private BigDecimal totalPrice;
  private PaymentMethod paymentMethod;
}
