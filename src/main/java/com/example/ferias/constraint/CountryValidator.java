package com.example.ferias.constraint;

import com.example.ferias.annotation.CheckCountry;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class CountryValidator implements ConstraintValidator<CheckCountry, String> {

    private CountrySet countrySet;

    @Override
    public void initialize(CheckCountry constraintAnnotation) {
        countrySet = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(String country, ConstraintValidatorContext context) {
        return countrySet.contains(country);
    }
}
