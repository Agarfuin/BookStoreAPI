package com.bookstore.clients.book.dto;

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
public class BookDto {

  private UUID bookId;
  private String title;
  private String author;
  private String description;
  private Integer publicationYear;
  private Set<String> genre;
  private BigDecimal price;
  private Integer quantityInStock;
}
