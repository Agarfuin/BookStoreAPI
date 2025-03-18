package com.bookstore.book.dto;

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
public class CreateBookRequestDto {

  private UUID bookId = UUID.fromString("00000000-0000-0000-0000-000000000000");

  @NotNull private String title;
  @NotNull private String author;

  private String description;

  @NotNull private Double price;
  @NotNull private Integer quantity;
}
