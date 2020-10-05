package com.example.ferias.service.ical;

import com.example.ferias.model.ComparableOccurrence;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.util.CompatibilityHints;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * https://ical4j.github.io/ical4j-user-guide/examples/
 */
public class Ical {
    private static final Logger LOGGER = LoggerFactory.getLogger(Ical.class);

    static {
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_UNFOLDING, true);
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING, true);
    }

    private final Calendar calendar;

    private Ical(Calendar calendar) {
        this.calendar = calendar;
    }

    public static Ical from(URL url) {
        CalendarBuilder builder = new CalendarBuilder();
        try (InputStream in = url.openStream()) {
            return new Ical(builder.build(in));
        } catch (Throwable t) {
            throw new RuntimeException("Can't init ical", t);
        }
    }

    private static Instant getInstant(Component component) {
        try {
            return LocalDate
                    .parse(component
                            .getProperties()
                            .getRequired(Property.DTSTART)
                            .getValue(), DateTimeFormatter.BASIC_ISO_DATE)
                    .atStartOfDay()
                    .toInstant(ZoneOffset.UTC);
        } catch (ConstraintViolationException e) {
            throw new RuntimeException("Unexpected problem with the DTSTART property", e);
        }
    }

    private static String getSummary(Component component) {
        try {
            return component.getProperties().getRequired(Property.SUMMARY).getValue();
        } catch (ConstraintViolationException e) {
            throw new RuntimeException("Unexpected problem with the SUMMARY property", e);
        }
    }

    public List<ComparableOccurrence> getComparableOccurrences() {
        return calendar.getComponents().getAll().stream()
                .filter(ct -> ct.getName().equals(Component.VEVENT))
                .map(ct -> new ComparableOccurrence(getInstant(ct), getSummary(ct)))
                .collect(Collectors.toList());
    }
}
