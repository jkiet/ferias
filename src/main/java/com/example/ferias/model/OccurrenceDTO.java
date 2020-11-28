package com.example.ferias.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OccurrenceDTO extends MetaDTO {

    private final String date;
    private final String name;

    private OccurrenceDTO(Instant instant, String name) {
        super();
        this.date = String.format("%1$tY-%1$tm-%1$td", Date.from(instant));
        this.name = name;
    }

    public static OccurrenceDTO from(ComparableOccurrence o) {
        return new OccurrenceDTO(o.getInstant(), o.getName());
    }

    public String getDate() { return date; }

    public String getName() { return name; }
}
