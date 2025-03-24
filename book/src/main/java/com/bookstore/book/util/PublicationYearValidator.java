package com.bookstore.book.util;

import com.bookstore.book.annotation.ValidPublicationYear;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.Year;

public class PublicationYearValidator
    implements ConstraintValidator<ValidPublicationYear, Integer> {

  public static final int MIN_YEAR = 1800;

  @Override
  public boolean isValid(Integer year, ConstraintValidatorContext context) {
    if (year == null) {
      return true;
    }
    int currentYear = Year.now().getValue();
    return year >= MIN_YEAR && year <= currentYear;
  }
}
