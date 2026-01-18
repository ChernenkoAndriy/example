package com.springdb.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springdb.example.dtos.CarDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CarCrudApiTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldPerformFullCarLifecycle() throws Exception {
        CarDto newCar = new CarDto();
        newCar.setBrand("Tesla");
        newCar.setModel("Model 3");
        newCar.setSeatingCapacity(5);

        String response = mockMvc.perform(post("/api/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCar)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.brand").value("Tesla"))
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readValue(response, CarDto.class).getId();

        mockMvc.perform(get("/api/cars/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.model").value("Model 3"));

        newCar.setModel("Model 3 Highland");
        mockMvc.perform(put("/api/cars/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCar)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.model").value("Model 3 Highland"));

        mockMvc.perform(delete("/api/cars/" + id))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/cars/" + id))
                .andExpect(status().isNotFound());
    }
}