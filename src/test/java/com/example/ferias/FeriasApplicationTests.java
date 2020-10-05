package com.example.ferias;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
class FeriasApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void whenInvalidPath_thenReturnNotFound() throws Exception {
		// given
		this.mockMvc
				// when
				.perform(MockMvcRequestBuilders.get("/"))
				// then
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

}
