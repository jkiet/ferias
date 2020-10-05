package com.example.ferias.service.ical;

import com.example.ferias.controller.HolidayInformation;
import com.example.ferias.model.ComparableOccurrence;
import com.example.ferias.model.ComparableOccurrenceTwin;
import com.example.ferias.model.RangeEnvelope;
import com.example.ferias.service.HolidayService;

import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.time.Year;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class HolidayServiceImpl implements HolidayService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HolidayInformation.class);

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

    public Pair<Instant, Instant> getRange() {
        return new Pair<>(beginningOfThisYear(), endOfNextYear());
    }

    public String resolveCountry(String isoCountryCode) {
        Locale locale = new Locale("", isoCountryCode);
        String name = locale.getDisplayCountry().replaceAll(" ", "-").toLowerCase();

        // well, it should be done better, but I do not understand everything at the moment.
        // - what about the regions?
        //   * can query `united-kingdom` or region like `united-kingdom/england`
        //     in this particular case it is possible to fall back to whole UK

        if (name.equals("czechia"))
            return "czech-republic";

        return name;
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

    //@Cacheable(value = "icalOccurencesCache", key = "#isoCountryCode")
    public List<ComparableOccurrence> fetchComparableOccurrences(String isoCountryCode) {
        return Ical.from(resolveApiURL(isoCountryCode)).getComparableOccurrences();
    }

    @Override
    public RangeEnvelope<Instant, ComparableOccurrenceTwin> nextTwin(final Instant startInstant, String isoCountryCode1, String isoCountryCode2) {
        Pair<Instant, Instant> range = getRange();
        if (startInstant.isBefore(range.getValue0()) || startInstant.isAfter(range.getValue1()))
            return new RangeEnvelope<>(false, range);

        LOGGER.info("API_KEY_TEST --> `" + apiKey + "`");

        List<ComparableOccurrence> sortedDistinct1afterStart = fetchComparableOccurrences(isoCountryCode1).stream()
                .filter(o -> startInstant.isBefore(o.getInstant()))
                .collect(Collectors.toList());

        List<ComparableOccurrence> sortedDistinct2afterStart = fetchComparableOccurrences(isoCountryCode2).stream()
                .filter(o -> startInstant.isBefore(o.getInstant()))
                .collect(Collectors.toList());

        return new RangeEnvelope<>(true, range,
                reduceToFirstMatch(
                        sortedDistinct1afterStart,
                        sortedDistinct2afterStart));
    }

    public static ComparableOccurrenceTwin reduceToFirstMatch(
            List<ComparableOccurrence> sortedDistinct1,
            List<ComparableOccurrence> sortedDistinct2) {
        return ComparableOccurrenceTwin.from(Stream
                .concat(sortedDistinct1.stream(),
                        sortedDistinct2.stream())
                .sorted(ComparableOccurrence::compareTo)
                .reduce((Pair<ComparableOccurrence, ComparableOccurrence>) null,
                        (pair, o) -> {
                            // 1.complete half of pair
                            if (null == pair)
                                return new Pair<>(o, null);
                            // 2. complete the whole pair
                            if (null == pair.getValue1())
                                return pair.setAt1(o);
                            // 3.`shift left` when instants are away
                            if (0 != pair.getValue0().compareTo(pair.getValue1()))
                                return pair.setAt0(pair.getValue1()).setAt1(o);
                            // 4.pass forward the same matching pair
                            return pair;
                        },
                        // 5.choose better match if necessary
                        (p1, p2) -> (0 < p1.getValue0().compareTo(p2.getValue0())) ? p1 : p2));
    }
}
