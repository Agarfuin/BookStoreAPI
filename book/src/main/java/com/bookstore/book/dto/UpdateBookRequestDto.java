package com.bookstore.book.dto;

import com.bookstore.book.annotation.ValidPublicationYear;
import com.bookstore.book.enums.Genre;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBookRequestDto {

  private String title;
  private String author;
  private String description;

  @ValidPublicationYear private Integer publicationYear;

  private Set<Genre> genre;
  private BigDecimal price;

  @Min(value = 0, message = "Quantity cannot be negative")
  private Integer quantity;
}
