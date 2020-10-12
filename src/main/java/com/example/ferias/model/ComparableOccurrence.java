package com.example.ferias.model;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

public class ComparableOccurrence implements Serializable, Comparable <ComparableOccurrence>{
    private static final long serialVersionUID = 1L;
    private final Instant instant;
    private final String name;

    public ComparableOccurrence(final Instant instant, String name) {
        this.instant = instant;
        this.name = name;
    }

    public Instant getInstant() {
        return instant;
    }

    public String getName() {
        return name;
    }

    @Override
    public int compareTo(final ComparableOccurrence o) {
        return this.instant.compareTo(o.instant);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComparableOccurrence that = (ComparableOccurrence) o;
        return Objects.equals(instant, that.instant) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(instant, name);
    }

    public String toString() { return instant.toString() + "->" + name; }
}
