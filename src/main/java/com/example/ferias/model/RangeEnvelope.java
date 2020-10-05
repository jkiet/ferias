package com.example.ferias.model;

import org.javatuples.Pair;

public class RangeEnvelope<R, T> {
    private final boolean isWithinRange;
    private final Pair<R, R> range;
    private final T payload;

    public RangeEnvelope(boolean isWithinRange, Pair<R, R> range) {
        this.isWithinRange = isWithinRange;
        this.range = range;
        this.payload = null;
    }

    public RangeEnvelope(boolean isWithinRange, Pair<R, R> range, T payload) {
        this.isWithinRange = isWithinRange;
        this.range = range;
        this.payload = payload;
    }

    public boolean isWithinRange() { return isWithinRange; }

    public Pair<R, R> getRange() { return range; }

    public T getPayload() { return payload; }
}
