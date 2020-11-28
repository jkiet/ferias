package com.example.ferias.service.ical;

import com.example.ferias.controller.HolidayInformation;
import com.example.ferias.model.*;
import com.example.ferias.service.HolidayService;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.Stream;

import io.vavr.control.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.time.Year;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HolidayServiceImpl implements HolidayService, Lookup<List<ComparableOccurrence>, String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(HolidayInformation.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Value("${holiday.api.url.format}")
    private String apiUrlFormat;

    @Value("${holiday.api.key}")
    private String apiKey;

    private static Instant beginningOfThisYear() {
        return Year
                .now()
                .atMonth(1)
                .atDay(1)
                .atStartOfDay()
                .toInstant(ZoneOffset.UTC);
    }

    private static Instant endOfNextYear() {
        return Year
                .now()
                .plusYears(1)
                .atMonth(12)
                .atEndOfMonth()
                .atStartOfDay()
                .toInstant(ZoneOffset.UTC);
    }

    public Tuple2<Instant, Instant> getRange() {
        return new Tuple2<>(beginningOfThisYear(), endOfNextYear());
    }

    public String resolveCountry(String isoCountryCode) {
        switch (isoCountryCode) {
            case "BY":
                return "belarus";
            case "CZ":
                return "czech-republic";
            case "DE":
                return "germany";
            case "DK":
                return "denmark";
            case "FI":
                return "finland";
            case "FR":
                return "france";
            case "IT":
                return "italy";
            case "NL":
                return "netherlands";
            case "PL":
                return "poland";
            case "PT":
                return "portugal";
            case "RU":
                return "russia";
            case "SE":
                return "sweden";
            case "SK":
                return "slovakia";
            case "GB":
                return "united-kingdom";
        }
        throw new RuntimeException("Country code `" + isoCountryCode + "` not supported.");
    }

    public URL resolveApiURL(String isoCountryCode) {
        String urlStr = String.format(apiUrlFormat, resolveCountry(isoCountryCode));
        LOGGER.info(isoCountryCode + "->" + urlStr);
        try {
            return new URL(urlStr);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Can't create URL from " + urlStr, e);
        }
    }

    @Cacheable(value = "icalOccurencesCache", key = "#isoCountryCode")
    @Override
    public List<ComparableOccurrence> lookupBy(String isoCountryCode) {
        return Ical.from(resolveApiURL(isoCountryCode)).getComparableOccurrences();
    }

    public List<ComparableOccurrence> fetchComparableOccurrences(String isoCountryCode) {
        Lookup<List<ComparableOccurrence>, String> proxy = applicationContext.getBean(Lookup.class);
        return proxy.lookupBy(isoCountryCode);
    }

    @Override
    public RangeEnvelope<Instant, ComparableOccurrence> next(final Instant startInstant, String isoCountryCode) {
        Tuple2<Instant, Instant> range = getRange();
        if (startInstant.isBefore(range._1()) || startInstant.isAfter(range._2()))
            return new RangeEnvelope<>(false, range);
        return new RangeEnvelope<Instant, ComparableOccurrence>(true, range,
                Stream.ofAll (fetchComparableOccurrences(isoCountryCode))
                        .foldLeft (null,
                                (acc, o) -> {
                                    if (null == acc && startInstant.isBefore(o.getInstant()))
                                        return o;
                                    else
                                        return acc;
                                }));
    }

    @Override
    public RangeEnvelope<Instant, ComparableOccurrenceTwin> nextTwin(final Instant startInstant, String isoCountryCode1, String isoCountryCode2) {
        Tuple2<Instant, Instant> range = getRange();
        if (startInstant.isBefore(range._1()) || startInstant.isAfter(range._2()))
            return new RangeEnvelope<>(false, range);

        LOGGER.info("API_KEY_TEST --> `" + apiKey + "`");

        List<ComparableOccurrence> distinct1afterStart = fetchComparableOccurrences(isoCountryCode1)
                .stream()
                .filter(o -> startInstant.isBefore(o.getInstant()))
                .collect(Collectors.toList());

        List<ComparableOccurrence> distinct2afterStart = fetchComparableOccurrences(isoCountryCode2)
                .stream()
                .filter(o -> startInstant.isBefore(o.getInstant()))
                .collect(Collectors.toList());

        return new RangeEnvelope<>(true, range,
                reduceToFirstMatch(
                        distinct1afterStart,
                        distinct2afterStart));
    }

    public static ComparableOccurrenceTwin reduceToFirstMatch(
            List<ComparableOccurrence> distinct1,
            List<ComparableOccurrence> distinct2) {
        return ComparableOccurrenceTwin.from(
                Stream.concat(distinct1, distinct2)
                        .sorted(ComparableOccurrence::compareTo)
                        .foldLeft(null,
                                (pair, o) -> {
                                    // 1.complete half of pair
                                    if (null == pair)
                                        return Tuple.of(o, null);
                                    // 2. complete the whole pair
                                    if (null == pair._2())
                                        return pair.update2(o);
                                    // 3.`shift left` when instants are away
                                    if (0 != pair._1().compareTo(pair._2()))
                                        return pair.update1(pair._2()).update2(o);
                                    // 4.pass forward the same matching pair
                                    return pair;
                                }));
    }

}
