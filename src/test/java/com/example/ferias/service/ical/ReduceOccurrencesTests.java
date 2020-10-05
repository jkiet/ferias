package com.example.ferias.service.ical;

import com.example.ferias.model.ComparableOccurrence;
import com.example.ferias.model.ComparableOccurrenceTwin;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static com.example.ferias.service.ical.HolidayServiceImpl.reduceToFirstMatch;

public class ReduceOccurrencesTests {

    @Test
    void whenTwoEmptyLists_thenNull() {
        assertTrue(null == reduceToFirstMatch(List.of(), List.of()));
    }

    @Test
    void whenTwoListContainingNotMatchingElement_thenNull() {
        ComparableOccurrence co1 = new ComparableOccurrence(Instant.parse("2020-01-01T00:00:00.00Z"), "foo");
        ComparableOccurrence co2 = new ComparableOccurrence(Instant.parse("2020-01-01T00:00:00.01Z"), "bar");
        assertTrue(null == reduceToFirstMatch(List.of(co1), List.of(co2)));
    }

    @Test
    void whenTwoListContainingMatchingElement_thenElementAppearsInTheResult() {
        ComparableOccurrence co1 = new ComparableOccurrence(Instant.parse("2020-01-01T00:00:00.00Z"), "foo");
        ComparableOccurrence co2 = new ComparableOccurrence(Instant.parse("2020-01-01T00:00:00.00Z"), "bar");
        ComparableOccurrenceTwin cot = ComparableOccurrenceTwin.of(co1, co2);
        assertTrue(null != cot);
        assertTrue(cot.equals(reduceToFirstMatch(List.of(co1), List.of(co2))));
    }

    @Test
    void whenOneElementListAndBiggerListContainingMatchingElement_thenElementAppearsInTheResult() {
        ComparableOccurrence co1 = new ComparableOccurrence(Instant.parse("2020-01-05T00:00:00.00Z"), "foo");
        ComparableOccurrence co2 = new ComparableOccurrence(Instant.parse("2020-01-05T00:00:00.00Z"), "bar");
        ComparableOccurrenceTwin cot = ComparableOccurrenceTwin.of(co1, co2);
        assertTrue(null != cot);
        assertTrue(cot.equals(reduceToFirstMatch(
                List.of(new ComparableOccurrence(Instant.parse("2020-01-06T00:00:00.00Z"), "blah"),
                        new ComparableOccurrence(Instant.parse("2020-01-01T00:00:00.00Z"), "blah"),
                        co1),
                List.of(co2))));
        assertTrue(cot.equals(reduceToFirstMatch(
                List.of(co1),
                List.of(new ComparableOccurrence(Instant.parse("2020-01-06T00:00:00.00Z"), "blah"),
                        new ComparableOccurrence(Instant.parse("2020-01-01T00:00:00.00Z"), "blah"),
                        co2))));
    }

    @Test
    void whenTwoListContainingShiftedMatchingElement_thenElementAppearsInTheResult() {
        ComparableOccurrence co1 = new ComparableOccurrence(Instant.parse("2020-01-01T00:00:00.10Z"), "foo");
        ComparableOccurrence co2 = new ComparableOccurrence(Instant.parse("2020-01-01T00:00:00.10Z"), "bar");
        ComparableOccurrenceTwin cot = ComparableOccurrenceTwin.of(co1, co2);

        assertTrue(null != cot);
        assertTrue(cot.equals(reduceToFirstMatch(
                List.of(new ComparableOccurrence(Instant.parse("2020-01-01T00:00:00.12Z"), "ethel"),
                        new ComparableOccurrence(Instant.parse("2020-01-01T00:00:00.07Z"), "baz"),
                        new ComparableOccurrence(Instant.parse("2020-01-01T00:00:00.22Z"), "qox"),
                        new ComparableOccurrence(Instant.parse("2020-01-01T00:00:00.04Z"), "baz"),
                        co1,
                        new ComparableOccurrence(Instant.parse("2020-01-05T05:00:00.02Z"), "ethel"),
                        new ComparableOccurrence(Instant.parse("2020-01-01T00:00:00.01Z"), "bar")),
                List.of(new ComparableOccurrence(Instant.parse("2020-01-01T00:00:00.12Z"), "ethel"),
                        new ComparableOccurrence(Instant.parse("2020-01-01T00:00:00.05Z"), "qox"),
                        co2,
                        new ComparableOccurrence(Instant.parse("2020-01-01T00:00:00.03Z"), "ethel"),
                        new ComparableOccurrence(Instant.parse("2020-01-01T00:00:00.22Z"), "qox")))));
    }
}
