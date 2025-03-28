package com.bookstore.clients.book.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBookRequestDto {

  @Min(value = 0, message = "Quantity cannot be negative")
  private Integer quantity;
}
