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

@AutoConfigureMockMvc
@SpringBootTest
public class NextTwinValidationTests {

    private static final String NEXT_TWIN_RELATIVE_URL = "/holiday/next-twin";
    private static final String NEXT_TWIN_COUNTRY1_PARAM = "country1";
    private static final String NEXT_TWIN_COUNTRY2_PARAM = "country2";
    private static final String NEXT_TWIN_DATE_PARAM = "date";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HolidayService holidayService;

    @Test
    void whenParamsAreMissing_thenReturnBadRequest() throws Exception {
        // given
        this.mockMvc
                // when
                .perform(MockMvcRequestBuilders.get(NEXT_TWIN_RELATIVE_URL))
                // then
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void whenParamsAreValid_thenReturnOk() throws Exception {
        // make a happy path underneath
        Mockito.when(
                holidayService.nextTwin(
                        Mockito.any(Instant.class),
                        Mockito.any(String.class),
                        Mockito.any(String.class)))

                .thenReturn(
                        new RangeEnvelope<>(true,
                                new Tuple2<>(Instant.now(), Instant.now()),
                                new ComparableOccurrenceTwin(Instant.now(), "foo", "bar")));
        // given
        this.mockMvc
                // when
                .perform(MockMvcRequestBuilders
                        .get(NEXT_TWIN_RELATIVE_URL)
                        .param(NEXT_TWIN_COUNTRY1_PARAM, "PL")
                        .param(NEXT_TWIN_COUNTRY2_PARAM, "GB")
                        .param(NEXT_TWIN_DATE_PARAM, "2042-01-01"))
                // then
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void whenDateParamIsInvalid_thenReturnBadRequest() throws Exception {
        // given
        this.mockMvc
                // when
                .perform(MockMvcRequestBuilders
                        .get(NEXT_TWIN_RELATIVE_URL)
                        .param(NEXT_TWIN_COUNTRY1_PARAM, "PL")
                        .param(NEXT_TWIN_COUNTRY2_PARAM, "GB")
                        .param(NEXT_TWIN_DATE_PARAM, "2042/01/01"))
                // then
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void whenCountry1ParamIsInvalid_thenReturnBadRequest() throws Exception {
        // given
        this.mockMvc
                // when
                .perform(MockMvcRequestBuilders
                        .get(NEXT_TWIN_RELATIVE_URL)
                        .param(NEXT_TWIN_COUNTRY1_PARAM, "PLA")
                        .param(NEXT_TWIN_COUNTRY2_PARAM, "GB")
                        .param(NEXT_TWIN_DATE_PARAM, "2042-01-01"))
                // then
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void whenCountry2ParamIsInvalid_thenReturnBadRequest() throws Exception {
        // given
        this.mockMvc
                // when
                .perform(MockMvcRequestBuilders
                        .get(NEXT_TWIN_RELATIVE_URL)
                        .param(NEXT_TWIN_COUNTRY1_PARAM, "PL")
                        .param(NEXT_TWIN_COUNTRY2_PARAM, "GBA")
                        .param(NEXT_TWIN_DATE_PARAM, "2042-01-01"))
                // then
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
