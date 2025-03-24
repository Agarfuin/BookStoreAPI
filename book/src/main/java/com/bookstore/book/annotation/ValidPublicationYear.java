package com.bookstore.book.annotation;

import com.bookstore.book.util.PublicationYearValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PublicationYearValidator.class)
public @interface ValidPublicationYear {

  String message() default
      "Publication year must be between "
          + PublicationYearValidator.MIN_YEAR
          + " and the current year";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
