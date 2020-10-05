package com.example.ferias.service;

import com.example.ferias.model.ComparableOccurrenceTwin;
import com.example.ferias.model.RangeEnvelope;

import java.time.Instant;

public interface HolidayService {

    RangeEnvelope<Instant, ComparableOccurrenceTwin> nextTwin(Instant afterInstant, String isoCountryCode1, String isoCountryCode2);
}
