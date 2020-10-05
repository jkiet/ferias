package com.example.ferias.annotation;

import com.example.ferias.constraint.CountrySet;
import com.example.ferias.constraint.CountryValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CountryValidator.class)
public @interface CheckCountry {
    String message() default "{com.example.ferias.constraint.country}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    CountrySet value();
}
