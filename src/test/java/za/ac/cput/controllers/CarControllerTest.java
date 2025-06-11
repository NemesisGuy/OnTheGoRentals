/*
package za.ac.cput.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import za.ac.cput.domain.entity.Car;
import za.ac.cput.domain.enums.PriceGroup;
// import za.ac.cput.domain.mapper.CarMapper; // Implicitly used, not directly needed in test import
import za.ac.cput.security.CustomerUserDetailsService; // Corrected path
import za.ac.cput.security.JwtUtilities;             // Corrected path
import za.ac.cput.security.SpringSecurityConfig;     // Corrected path
import za.ac.cput.service.ICarService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CarController.class)
@Import(SpringSecurityConfig.class) // To load the actual security filter chain for public endpoints
public class CarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ICarService carService;

    @MockBean
    private JwtUtilities jwtUtilities;

    @MockBean
    private CustomerUserDetailsService customerUserDetailsService;

    private Car car1, car2;
    private UUID car1Uuid, car2Uuid;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();

        car1Uuid = UUID.randomUUID();
        car2Uuid = UUID.randomUUID();

        car1 = new Car.Builder()
                .setId(1)
                .setUuid(car1Uuid)
                .setMake("Toyota")
                .setModel("Corolla")
                .setYear(2022)
                .setLicensePlate("CA123000")
                .setPriceGroup(PriceGroup.ECONOMY)
                .setAvailable(true) // Based on your provided test code
                .setDeleted(false)
                .build();

        car2 = new Car.Builder()
                .setId(2)
                .setUuid(car2Uuid)
                .setMake("BMW")
                .setModel("X5")
                .setYear(2023)
                .setLicensePlate("WP987654")
                .setPriceGroup(PriceGroup.LUXURY)
                .setAvailable(true) // Based on your provided test code
                .setDeleted(false)
                .build();
    }

    // Tests for getAvailableCars()
    @Test
    void getAvailableCars_shouldReturn200AndListOfCars_whenCarsAvailable() throws Exception {
        List<Car> availableCars = Arrays.asList(car1, car2);
        when(carService.findAllAvailableAndNonDeleted()).thenReturn(availableCars);

        mockMvc.perform(get("/api/v1/cars/available"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].uuid", is(car1Uuid.toString())))
                .andExpect(jsonPath("$.data[0].make", is("Toyota")))
                .andExpect(jsonPath("$.data[1].uuid", is(car2Uuid.toString())))
                .andExpect(jsonPath("$.data[1].make", is("BMW")))
                .andExpect(jsonPath("$.errors", empty())); // Corrected: check for empty array

        verify(carService).findAllAvailableAndNonDeleted();
    }

    @Test
    void getAvailableCars_shouldReturn204_whenNoCarsAvailable() throws Exception {
        when(carService.findAllAvailableAndNonDeleted()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/cars/available"))
                .andExpect(status().isNoContent());

        verify(carService).findAllAvailableAndNonDeleted();
    }

    // Tests for getAvailableCarsByPriceGroup(String groupString)
    @Test
    void getAvailableCarsByPriceGroup_shouldReturn200AndFilteredCars_whenValidGroupAndCarsExist() throws Exception {
        List<Car> luxuryCars = Collections.singletonList(car2);
        when(carService.getAvailableCarsByPrice(PriceGroup.LUXURY)).thenReturn(luxuryCars);

        mockMvc.perform(get("/api/v1/cars/available/price-group/luxury"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].uuid", is(car2Uuid.toString())))
                .andExpect(jsonPath("$.data[0].priceGroup", is(PriceGroup.LUXURY.toString())));

        verify(carService).getAvailableCarsByPrice(PriceGroup.LUXURY);
    }

    @Test
    void getAvailableCarsByPriceGroup_shouldReturn204_whenValidGroupButNoCarsMatch() throws Exception {
        when(carService.getAvailableCarsByPrice(PriceGroup.ECONOMY)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/cars/available/price-group/ECONOMY"))
                .andExpect(status().isNoContent());

        verify(carService).getAvailableCarsByPrice(PriceGroup.ECONOMY);
    }

    @Test
    void getAvailableCarsByPriceGroup_shouldReturn400_whenInvalidGroupString() throws Exception {
        String invalidGroup = "nonexistent_group";
        String expectedErrorMessage = "Invalid price group value: '" + invalidGroup + "'. Please use a valid price group.";

        mockMvc.perform(get("/api/v1/cars/available/price-group/" + invalidGroup))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is("fail"))) // Corrected: "fail"
                .andExpect(jsonPath("$.data", is(nullValue())))
                .andExpect(jsonPath("$.errors[0].message", is(expectedErrorMessage))); // Corrected: path and exact message

        verifyNoInteractions(carService);
    }

    // Tests for getAllCarsByPriceGroupOptional(String groupString)
    @Test
    void getAllCarsByPriceGroupOptional_shouldReturnAllCars_whenNoGroupStringAndCarsExist() throws Exception {
        List<Car> allCars = Arrays.asList(car1, car2);
        when(carService.getAll()).thenReturn(allCars);

        mockMvc.perform(get("/api/v1/cars/price-group"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.errors", empty())); // Added check for empty errors array
        verify(carService).getAll();
    }

    @Test
    void getAllCarsByPriceGroupOptional_shouldReturn204_whenNoGroupStringAndNoCarsExist() throws Exception {
        when(carService.getAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/cars/price-group"))
                .andExpect(status().isNoContent());
        verify(carService).getAll();
    }

    @Test
    void getAllCarsByPriceGroupOptional_shouldReturnAllCars_whenGroupStringIsAll() throws Exception {
        List<Car> allCars = Arrays.asList(car1, car2);
        when(carService.getAll()).thenReturn(allCars);

        mockMvc.perform(get("/api/v1/cars/price-group/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.errors", empty())); // Added check for empty errors array
        verify(carService).getAll();
    }

    @Test
    void getAllCarsByPriceGroupOptional_shouldReturnAllCars_whenGroupStringIsAllWithSpacesAndMixedCase() throws Exception {
        List<Car> allCars = Arrays.asList(car1, car2);
        when(carService.getAll()).thenReturn(allCars);

        mockMvc.perform(get("/api/v1/cars/price-group/ AlL "))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.errors", empty())); // Added check for empty errors array
        verify(carService).getAll();
    }

    @Test
    void getAllCarsByPriceGroupOptional_shouldReturnFilteredCars_whenValidGroupStringAndCarsExist() throws Exception {
        List<Car> economyCars = Collections.singletonList(car1);
        when(carService.getCarsByPriceGroup(PriceGroup.ECONOMY)).thenReturn(economyCars);

        mockMvc.perform(get("/api/v1/cars/price-group/economy"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].priceGroup", is(PriceGroup.ECONOMY.toString())))
                .andExpect(jsonPath("$.errors", empty())); // Added check for empty errors array
        verify(carService).getCarsByPriceGroup(PriceGroup.ECONOMY);
    }

    @Test
    void getAllCarsByPriceGroupOptional_shouldReturn204_whenValidGroupStringAndNoCarsMatch() throws Exception {
        when(carService.getCarsByPriceGroup(PriceGroup.LUXURY)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/cars/price-group/LUXURY"))
                .andExpect(status().isNoContent());
        verify(carService).getCarsByPriceGroup(PriceGroup.LUXURY);
    }

    @Test
    void getAllCarsByPriceGroupOptional_shouldReturn400_whenInvalidGroupString() throws Exception {
        String invalidGroup = "super_premium";
        String expectedErrorMessage = "Invalid price group value: '" + invalidGroup + "'."; // As per controller


        mockMvc.perform(get("/api/v1/cars/price-group/" + invalidGroup))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is("fail"))) // Corrected: "fail"
                .andExpect(jsonPath("$.errors[0].message", is(expectedErrorMessage))); // Corrected: path and exact message

        verify(carService, never()).getCarsByPriceGroup(any(PriceGroup.class));
        verify(carService, never()).getAll();
    }

    // Tests for getCarByUuid(UUID carUuid)
    @Test
    void getCarByUuid_shouldReturn200AndCar_whenCarExists() throws Exception {
        when(carService.read(car1Uuid)).thenReturn(car1);

        mockMvc.perform(get("/api/v1/cars/" + car1Uuid))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.data.uuid", is(car1Uuid.toString())))
                .andExpect(jsonPath("$.data.make", is("Toyota")))
                .andExpect(jsonPath("$.errors", empty())); // Added check for empty errors array
        verify(carService).read(car1Uuid);
    }

    @Test
    void getCarByUuid_shouldReturn404_whenCarNotFound() throws Exception {
        UUID nonExistentUuid = UUID.randomUUID();
        String expectedErrorMessage = "Car not found with UUID: " + nonExistentUuid.toString();
        when(carService.read(nonExistentUuid)).thenReturn(null);

        mockMvc.perform(get("/api/v1/cars/" + nonExistentUuid))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is("fail"))) // Corrected: "fail"
                .andExpect(jsonPath("$.data", is(nullValue())))
                .andExpect(jsonPath("$.errors[0].message", is(expectedErrorMessage))); // Corrected: path and exact message
        verify(carService).read(nonExistentUuid);
    }

    // Tests for getAllCars()
    @Test
    void getAllCars_shouldReturn200AndListOfCars_whenCarsExist() throws Exception {
        List<Car> allCars = Arrays.asList(car1, car2);
        when(carService.getAll()).thenReturn(allCars);

        mockMvc.perform(get("/api/v1/cars"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].uuid", is(car1Uuid.toString())))
                .andExpect(jsonPath("$.data[1].uuid", is(car2Uuid.toString())))
                .andExpect(jsonPath("$.errors", empty())); // Added check for empty errors array

        verify(carService).getAll();
    }

    @Test
    void getAllCars_shouldReturn204_whenNoCarsExistInSystem() throws Exception {
        when(carService.getAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/cars"))
                .andExpect(status().isNoContent());

        verify(carService).getAll();
    }
}*/
