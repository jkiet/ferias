package com.example.ferias.model;

import io.vavr.Tuple2;

public class RangeEnvelope<R, T> {
    private final boolean isWithinRange;
    private final Tuple2<R, R> range;
    private final T payload;

    public RangeEnvelope(boolean isWithinRange, Tuple2<R, R> range) {
        this.isWithinRange = isWithinRange;
        this.range = range;
        this.payload = null;
    }

    public RangeEnvelope(boolean isWithinRange, Tuple2<R, R> range, T payload) {
        this.isWithinRange = isWithinRange;
        this.range = range;
        this.payload = payload;
    }

    public boolean isWithinRange() { return isWithinRange; }

    public Tuple2<R, R> getRange() { return range; }

    public R getRangeStart() { return range._1(); }

    public R getRangeEnd() { return range._2(); }

    public T getPayload() { return payload; }
}
