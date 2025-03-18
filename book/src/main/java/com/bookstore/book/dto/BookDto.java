package com.bookstore.book.dto;

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
  private Double price;
  private Integer quantityInStock;
}
