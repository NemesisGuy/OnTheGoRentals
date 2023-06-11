package za.ac.cput.backend.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import za.ac.cput.controllers.CarController;
import za.ac.cput.domain.impl.Car;
import za.ac.cput.domain.impl.PriceGroup;
import za.ac.cput.service.impl.ICarServiceImpl;

import java.util.ArrayList;
import java.util.List;


import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CarController.class)
public class CarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ICarServiceImpl carService;

    private List<Car> carList;

    @BeforeEach
    public void setUp() {
        carList = new ArrayList<>();
        carList.add(Car.builder().id(1).licensePlate("ABC123").make("Toyota").model("Camry").priceGroup(PriceGroup.STANDARD).build());
        carList.add(Car.builder().id(2).licensePlate("DEF456").make("Honda").model("Accord").priceGroup(PriceGroup.LUXURY).build());
        carList.add(Car.builder().id(3).licensePlate("DEF456").make("Toyota").model("Aygo").priceGroup(PriceGroup.ECONOMY).build());
    }

    @Test
    public void testGetCars() throws Exception {
        // Mock the service method
        when(carService.getAll()).thenReturn((ArrayList<Car>) carList);

        // Perform the GET request
        mockMvc.perform(get("/api/cars/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].make").value("Toyota"))
                .andExpect(jsonPath("$[0].model").value("Camry"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].make").value("Honda"))
                .andExpect(jsonPath("$[1].model").value("Accord"));
    }

    @Test
    public void testGetEconomyCars() throws Exception {
        // Mock the service method
        when(carService.getAll()).thenReturn((ArrayList<Car>) carList);

        // Perform the GET request
        mockMvc.perform(get("/api/cars/economy"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$[0].id").value(3))
                .andExpect(jsonPath("$[0].make").value("Toyota"))
                .andExpect(jsonPath("$[0].model").value("Aygo"));
    }

    @Test
    public void testGetLuxuryCars() throws Exception {
        // Mock the service method
        when(carService.getAll()).thenReturn((ArrayList<Car>) carList);

        // Perform the GET request
        mockMvc.perform(get("/api/cars/luxury"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].make").value("Honda"))
                .andExpect(jsonPath("$[0].model").value("Accord"));
    }

    @Test
    public void testGetSpecialCars() throws Exception {
        // Mock the service method
        when(carService.getAll()).thenReturn((ArrayList<Car>) carList);

        // Perform the GET request
        mockMvc.perform(get("/api/cars/special"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
