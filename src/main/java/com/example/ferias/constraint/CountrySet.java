package com.example.ferias.constraint;

import java.util.Locale;
import java.util.Optional;
import java.util.Set;

public enum CountrySet {
    ISO(Set.of(Locale.getISOCountries()));

    private final Set<String> countrySet;

    CountrySet(Set<String> countrySet) {
        this.countrySet = countrySet;
    }

    public boolean contains(String country) {
        return countrySet.contains(Optional.ofNullable(country).orElse(""));
    }
}
