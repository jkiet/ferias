package com.example.ferias.model;

import io.vavr.Tuple2;

import java.time.Instant;
import java.util.Objects;

public class ComparableOccurrenceTwin implements Comparable<ComparableOccurrenceTwin> {
    private final Instant instant;
    private final String name1;
    private final String name2;

    public ComparableOccurrenceTwin(Instant instant, String name1, String name2) {
        this.instant = instant;
        this.name1 = name1;
        this.name2 = name2;
    }

    public static ComparableOccurrenceTwin from(Tuple2<ComparableOccurrence, ComparableOccurrence> pair) {
        if (null == pair)
            return null;
        if (null == pair._1() || null == pair._2())
            return null;
        if (0 != pair._1().compareTo(pair._2()))
            return null;
        return new ComparableOccurrenceTwin(pair._1().getInstant(),
                pair._1().getName(),
                pair._2().getName());
    }

    public static ComparableOccurrenceTwin of(ComparableOccurrence o1, ComparableOccurrence o2) {
        if (null == o1 || null == o2)
            return null;
        if (0 != o1.compareTo(o2))
            return null;
        return new ComparableOccurrenceTwin(o1.getInstant(), o1.getName(), o2.getName());
    }

    public Instant getInstant() {
        return instant;
    }

    public String getName1() {
        return name1;
    }

    public String getName2() {
        return name2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComparableOccurrenceTwin that = (ComparableOccurrenceTwin) o;
        return Objects.equals(instant, that.instant) &&
                Objects.equals(name1, that.name1) &&
                Objects.equals(name2, that.name2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(instant, name1, name2);
    }

    @Override
    public int compareTo(ComparableOccurrenceTwin twin) {
        return this.instant.compareTo(twin.instant);
    }
}
