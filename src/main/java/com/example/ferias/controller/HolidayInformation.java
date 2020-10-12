package com.example.ferias.controller;

import com.example.ferias.annotation.CheckCountry;
import com.example.ferias.constraint.CountrySet;
import com.example.ferias.model.ComparableOccurrenceTwin;
import com.example.ferias.model.MetaDTO;
import com.example.ferias.model.OccurrenceTwinDTO;

import com.example.ferias.model.RangeEnvelope;
import com.example.ferias.service.HolidayService;


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

    @GetMapping(value = "/next-twin", produces = {"application/json"})
    public ResponseEntity<? super OccurrenceTwinDTO> nextTwin(
            @RequestParam("country1") @CheckCountry(CountrySet.ISO) final String country1,
            @RequestParam("country2") @CheckCountry(CountrySet.ISO) final String country2,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate date) {

        // I don't know if it can be checked for the given date,
        // the scope is known to the service implementation, and will be supplied back in the envelope.
        RangeEnvelope<Instant, ComparableOccurrenceTwin> envelope = holidayService
                .nextTwin(date.atStartOfDay().toInstant(ZoneOffset.UTC), country1, country2);

        // Out of scope of this particular service implementation (short term lookup).
        // You need different type of service (archive or fortune teller).
        if (!envelope.isWithinRange())
            return ResponseEntity
                    .status(HttpStatus.I_AM_A_TEAPOT)
                    .body(new MetaDTO(HttpStatus.I_AM_A_TEAPOT.getReasonPhrase(),
                            String.format("The date is out of supported range `%1$tY-%1$tm-%1$td - %2$tY-%2$tm-%2$td`",
                                    Date.from(envelope.getRange().getValue0()),
                                    Date.from(envelope.getRange().getValue1()))));
        // Everything is fine, but nothing found.
        if (null == envelope.getPayload())
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new MetaDTO(
                            String.format("Not found within range `%1$tY-%1$tm-%1$td - %2$tY-%2$tm-%2$td`",
                                    Date.from(envelope.getRange().getValue0()),
                                    Date.from(envelope.getRange().getValue1()))));
        return ResponseEntity
                .ok()
                .body(OccurrenceTwinDTO.from(envelope.getPayload()));
    }
}
