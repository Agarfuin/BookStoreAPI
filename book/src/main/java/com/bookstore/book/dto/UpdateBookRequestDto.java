package com.bookstore.book.dto;

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
  private Double price;
  private Integer quantity;
}
