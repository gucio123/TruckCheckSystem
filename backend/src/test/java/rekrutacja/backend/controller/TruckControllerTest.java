package rekrutacja.backend.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import rekrutacja.backend.model.Truck;
import rekrutacja.backend.model.TruckStatus;
import rekrutacja.backend.service.TruckService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebMvcTest(TruckController.class)
public class TruckControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TruckService truckService;

    private Truck truck;

    @BeforeEach
    public void setUp() {
        truck = new Truck(1L, 5, 5, TruckStatus.ARRIVED);
    }

    @Test
    public void testGetEstimatedWaitTime() throws Exception {
        when(truckService.calculateEstimatedWaitTime(1L)).thenReturn(10);

        mockMvc.perform(get("/api/trucks/estimatedWaitTime/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("10"));
    }

    @Test
    public void testGetEstimatedWaitTime_TruckNotFound() throws Exception {
        when(truckService.calculateEstimatedWaitTime(2L)).thenThrow(new RuntimeException("Truck not found"));

        mockMvc.perform(get("/api/trucks/estimatedWaitTime/2"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Truck not found"));
    }

    @Test
    public void testArrive() throws Exception {
        when(truckService.arrive()).thenReturn(truck);

        mockMvc.perform(post("/api/trucks/arrive")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    public void testStatus() throws Exception {
        Map<String, Object> status = new HashMap<>();
        status.put("queue1", List.of(truck));
        status.put("queue2", List.of());
        status.put("trucks", List.of(truck));

        when(truckService.status()).thenReturn(status);

        mockMvc.perform(get("/api/trucks/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.queue1[0].id").value(1L));
    }
}
