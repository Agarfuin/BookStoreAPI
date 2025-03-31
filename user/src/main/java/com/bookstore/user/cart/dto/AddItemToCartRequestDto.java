package com.bookstore.user.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddItemToCartRequestDto {

  @NotNull(message = "ItemId is required")
  private UUID itemId;

  @NotNull(message = "Quantity is required")
  @Min(value = 0, message = "Quantity cannot be negative")
  private Integer quantity;
}
