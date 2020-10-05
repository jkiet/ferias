package com.example.ferias.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OccurrenceTwinDTO extends MetaDTO {

    private final String date;
    private final String name1;
    private final String name2;

    private OccurrenceTwinDTO(Instant instant, String name1, String name2) {
        super();
        this.date = String.format("%1$tY-%1$tm-%1$td", Date.from(instant));
        this.name1 = name1;
        this.name2 = name2;
    }

    public static OccurrenceTwinDTO from(ComparableOccurrenceTwin twin) {
        return new OccurrenceTwinDTO(twin.getInstant(), twin.getName1(), twin.getName2());
    }

    public String getDate() { return date; }

    public String getName1() { return name1; }

    public String getName2() { return name2; }
}
