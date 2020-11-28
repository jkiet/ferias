package com.example.ferias.controller;

import com.example.ferias.model.ComparableOccurrenceTwin;
import com.example.ferias.model.RangeEnvelope;
import com.example.ferias.service.HolidayService;

import io.vavr.Tuple2;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.Instant;
import java.time.Year;
import java.time.ZoneOffset;

@AutoConfigureMockMvc
@SpringBootTest
public class NextTwinResponseTests {

    private static final String NEXT_TWIN_RELATIVE_URL = "/holiday/next-twin";
    private static final String NEXT_TWIN_COUNTRY1_PARAM = "country1";
    private static final String NEXT_TWIN_COUNTRY2_PARAM = "country2";
    private static final String NEXT_TWIN_DATE_PARAM = "date";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HolidayService holidayService;

    private Tuple2<Instant, Instant> getInstantPair() {
        return new Tuple2<>(
                Year.of(2000).atMonth(1).atDay(1).atStartOfDay().toInstant(ZoneOffset.UTC),
                Year.of(2001).atMonth(12).atEndOfMonth().atStartOfDay().toInstant(ZoneOffset.UTC));
    }

    private Instant getInstantBetween() {
        return Year.of(2001).atMonth(1).atDay(1).atStartOfDay().toInstant(ZoneOffset.UTC);
    }

    @Test
    void whenDateIsOutOfRange_thenReturnTeapot() throws Exception {
        Mockito.when(holidayService.nextTwin(
                Mockito.any(Instant.class),
                Mockito.any(String.class),
                Mockito.any(String.class)))
                .thenReturn(new RangeEnvelope<>(false, getInstantPair(), null));

        // given
        this.mockMvc
                // when
                .perform(MockMvcRequestBuilders
                        .get(NEXT_TWIN_RELATIVE_URL)
                        .param(NEXT_TWIN_COUNTRY1_PARAM, "PL")
                        .param(NEXT_TWIN_COUNTRY2_PARAM, "GB")
                        .param(NEXT_TWIN_DATE_PARAM, "1999-12-31"))
                // then
                .andExpect(MockMvcResultMatchers.status().isIAmATeapot());
    }

    @Test
    void whenDateIsWithinImplRangeAndNothingFound_thenReturnNotFound() throws Exception {
        Mockito.when(holidayService.nextTwin(
                Mockito.any(Instant.class),
                Mockito.any(String.class),
                Mockito.any(String.class)))
                .thenReturn(new RangeEnvelope<>(true, getInstantPair(), null));

        // given
        this.mockMvc
                // when
                .perform(MockMvcRequestBuilders
                        .get(NEXT_TWIN_RELATIVE_URL)
                        .param(NEXT_TWIN_COUNTRY1_PARAM, "PL")
                        .param(NEXT_TWIN_COUNTRY2_PARAM, "GB")
                        .param(NEXT_TWIN_DATE_PARAM, "2001-01-01"))
                // then
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void whenDateIsWithinImplRangeAndSomethingFound_thenReturnOk() throws Exception {
        Mockito.when(holidayService.nextTwin(
                Mockito.any(Instant.class),
                Mockito.any(String.class),
                Mockito.any(String.class)))
                .thenReturn(new RangeEnvelope<>(true, getInstantPair(),
                        new ComparableOccurrenceTwin(getInstantBetween(), "foo", "bar")));

        // given
        this.mockMvc
                // when
                .perform(MockMvcRequestBuilders
                        .get(NEXT_TWIN_RELATIVE_URL)
                        .param(NEXT_TWIN_COUNTRY1_PARAM, "PL")
                        .param(NEXT_TWIN_COUNTRY2_PARAM, "GB")
                        .param(NEXT_TWIN_DATE_PARAM, "2001-01-01"))
                // then
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
