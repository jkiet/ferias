package com.example.ferias.controller;

import com.example.ferias.annotation.CheckCountry;
import com.example.ferias.constraint.CountrySet;
import com.example.ferias.model.*;

import com.example.ferias.service.HolidayService;


import org.jooq.lambda.function.Function1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolationException;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping(value = "/holiday")
public class HolidayInformation {

    @Autowired
    private HolidayService holidayService;

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handle(ConstraintViolationException cve) {
        return new ResponseEntity<Object>(
                new MetaDTO(HttpStatus.BAD_REQUEST.getReasonPhrase(),
                        cve.getConstraintViolations()
                                .stream()
                                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                                .collect(Collectors.toList())),
                new HttpHeaders(),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConversionFailedException.class)
    public ResponseEntity<Object> handle(ConversionFailedException cfe) {
        return new ResponseEntity<Object>(
                new MetaDTO(HttpStatus.BAD_REQUEST.getReasonPhrase(), cfe.getMessage()),
                new HttpHeaders(),
                HttpStatus.BAD_REQUEST);
    }

    @GetMapping(value = "/next", produces = {"application/json"})
    public ResponseEntity<? super OccurrenceDTO> next(
            @RequestParam("country") @CheckCountry(CountrySet.ISO) final String country,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate date) {

        return instantEnvelopeProjection(
                holidayService.next(date.atStartOfDay().toInstant(ZoneOffset.UTC), country),
                (x) -> OccurrenceDTO.from(x));
    }

    @GetMapping(value = "/next-twin", produces = {"application/json"})
    public ResponseEntity<? super OccurrenceTwinDTO> nextTwin(
            @RequestParam("country1") @CheckCountry(CountrySet.ISO) final String country1,
            @RequestParam("country2") @CheckCountry(CountrySet.ISO) final String country2,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate date) {

        return instantEnvelopeProjection(
                holidayService.nextTwin(date.atStartOfDay().toInstant(ZoneOffset.UTC), country1, country2),
                (x) -> OccurrenceTwinDTO.from(x));
    }

    /**
     * We don't know if P can be returned for the given Instant,
     * the scope is known to the service implementation, and will be supplied back in the envelope.
     * When we are acting out of range or nothing found the MetaDTO will be returned as body.
     * For the valid response the projectionFn will be applied.
     */
    public static <P, D> ResponseEntity<? super D> instantEnvelopeProjection(
            RangeEnvelope<Instant, P> envelope,
            Function1<P, D> projectionFn) {
        // Out of scope of this particular service implementation (short term lookup).
        // You need different type of service (archive or fortune teller).
        if (!envelope.isWithinRange())
            return ResponseEntity
                    .status(HttpStatus.I_AM_A_TEAPOT)
                    .body(new MetaDTO(HttpStatus.I_AM_A_TEAPOT.getReasonPhrase(),
                            String.format("The date is out of supported range `%1$tY-%1$tm-%1$td - %2$tY-%2$tm-%2$td`",
                                    Date.from(envelope.getRangeStart()),
                                    Date.from(envelope.getRangeEnd()))));
        // Everything is fine, but nothing found.
        if (null == envelope.getPayload())
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new MetaDTO(
                            String.format("Not found within range `%1$tY-%1$tm-%1$td - %2$tY-%2$tm-%2$td`",
                                    Date.from(envelope.getRangeStart()),
                                    Date.from(envelope.getRangeEnd()))));

        return ResponseEntity
                .ok()
                .body(projectionFn.apply(envelope.getPayload()));
    }
}
