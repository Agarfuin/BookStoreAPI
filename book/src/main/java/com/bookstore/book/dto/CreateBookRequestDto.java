package com.bookstore.book.dto;

import com.bookstore.book.enums.Genre;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Set;
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
  @NotNull private Integer publicationYear;
  @NotNull private Set<Genre> genre;

  private String description;

  @NotNull private BigDecimal price;
  @NotNull private Integer quantity;
}
