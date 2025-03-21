package com.bookstore.book.dto;

import com.bookstore.book.enums.Genre;
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
  private Integer publicationYear;
  private Set<Genre> genre;
  private BigDecimal price;
  private Integer quantity;
}
