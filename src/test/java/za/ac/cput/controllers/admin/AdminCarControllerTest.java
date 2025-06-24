/*
package za.ac.cput.controllers.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import za.ac.cput.domain.dto.request.CarCreateDTO;
import za.ac.cput.domain.dto.request.CarUpdateDTO;
import za.ac.cput.domain.entity.Car;
import za.ac.cput.domain.entity.security.Role;
import za.ac.cput.domain.entity.security.RoleName;
import za.ac.cput.domain.entity.security.User;
import za.ac.cput.domain.enums.PriceGroup;
import za.ac.cput.domain.mapper.CarMapper;
import za.ac.cput.exception.ResourceNotFoundException;
import za.ac.cput.security.CustomerUserDetailsService;
import za.ac.cput.security.JwtUtilities;
import za.ac.cput.security.SpringSecurityConfig;
import za.ac.cput.service.ICarService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminCarController.class)
@Import(SpringSecurityConfig.class)
public class AdminCarControllerTest {

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

    private User adminUserAccount;
    private Car car1, car2;
    private UUID car1Uuid, car2Uuid;
    private String dummyToken = "Bearer dummy-jwt-token";


    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();

        Role adminRole = Role.builder().id(1).roleName(RoleName.ADMIN).build();
        adminUserAccount = User.builder()
                .id(1)
                .uuid(UUID.randomUUID())
                .email("admin@onthego.com")
                .firstName("Admin")
                .lastName("User")
                .password("encodedPassword")
                .roles(Collections.singletonList(adminRole))
                .build();

        car1Uuid = UUID.randomUUID();
        car2Uuid = UUID.randomUUID();

        car1 = new Car.Builder().setId(1).setUuid(car1Uuid).setMake("Toyota").setModel("Corolla").setYear(2022).setLicensePlate("ADM001").setPriceGroup(PriceGroup.ECONOMY).setAvailable(true).setDeleted(false).build();
        car2 = new Car.Builder().setId(2).setUuid(car2Uuid).setMake("BMW").setModel("X5").setYear(2023).setLicensePlate("ADM002").setPriceGroup(PriceGroup.LUXURY).setAvailable(false).setDeleted(false).build();

        when(jwtUtilities.validateToken(anyString())).thenReturn(true);
        when(jwtUtilities.extractUserEmail(anyString())).thenReturn(adminUserAccount.getEmail());


        List<SimpleGrantedAuthority> authorities = adminUserAccount.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleNameEnum().name()))
                .collect(Collectors.toList());

        UserDetails springAdminUserDetails = new org.springframework.security.core.userdetails.User(
                adminUserAccount.getEmail(),
                adminUserAccount.getPassword(),
                authorities
        );
        when(customerUserDetailsService.loadUserByUsername(adminUserAccount.getEmail())).thenReturn(springAdminUserDetails);
    }

    private MockHttpServletRequestBuilder authenticatedGet(String url) {
        return get(url).header("Authorization", dummyToken);
    }

    private MockHttpServletRequestBuilder authenticatedPost(String url) {
        return post(url).header("Authorization", dummyToken).with(csrf());
    }

    private MockHttpServletRequestBuilder authenticatedPut(String url) {
        return put(url).header("Authorization", dummyToken).with(csrf());
    }

    private MockHttpServletRequestBuilder authenticatedDelete(String url) {
        return delete(url).header("Authorization", dummyToken).with(csrf());
    }

    @Test
    void getAllCarsForAdmin_shouldReturn200AndListOfCars_whenCarsExist() throws Exception {
        List<Car> allCars = Arrays.asList(car1, car2);
        when(carService.getAll()).thenReturn(allCars);

        mockMvc.perform(authenticatedGet("/api/v1/admin/cars"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].uuid", is(car1Uuid.toString())))
                .andExpect(jsonPath("$.data[1].uuid", is(car2Uuid.toString())))
                .andExpect(jsonPath("$.errors", empty()));
        verify(carService).getAll();
    }

    @Test
    void getAllCarsForAdmin_shouldReturn204_whenNoCarsExist() throws Exception {
        when(carService.getAll()).thenReturn(Collections.emptyList());
        mockMvc.perform(authenticatedGet("/api/v1/admin/cars"))
                .andExpect(status().isNoContent());
        verify(carService).getAll();
    }

    @Test
    void createCar_shouldReturn201AndCreatedCar_whenValidInput() throws Exception {
        // Corrected DTO instantiation
        CarCreateDTO createDTO = new CarCreateDTO(
                "Honda", "Civic", 2024, "NEW001", PriceGroup.STANDARD, "300.00", true
        );
        Car carToCreate = CarMapper.toEntity(createDTO);
        Car createdCar = new Car.Builder().copy(carToCreate).setId(3).setUuid(UUID.randomUUID()).build();

        when(carService.create(any(Car.class))).thenReturn(createdCar);

        mockMvc.perform(authenticatedPost("/api/v1/admin/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.data.uuid", is(createdCar.getUuid().toString())))
                .andExpect(jsonPath("$.data.make", is("Honda")))
                .andExpect(jsonPath("$.errors", empty()));
        verify(carService).create(argThat(c -> c.getMake().equals("Honda") && c.getModel().equals("Civic")));
    }

    @Test
    void createCar_shouldReturn400_whenInvalidInput() throws Exception {
        // Corrected DTO instantiation (null make for invalid input)
        CarCreateDTO createDTO = new CarCreateDTO(
                null, "Civic", 2024, "NEW001", PriceGroup.STANDARD, "300.00", true
        );
        mockMvc.perform(authenticatedPost("/api/v1/admin/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is("fail")))
                .andExpect(jsonPath("$.errors", not(empty())))
                .andExpect(jsonPath("$.errors[0].field", is("make")));
        verifyNoInteractions(carService);
    }

    @Test
    void getCarByUuidAdmin_shouldReturn200AndCar_whenCarExists() throws Exception {
        when(carService.read(car1Uuid)).thenReturn(car1);
        mockMvc.perform(authenticatedGet("/api/v1/admin/cars/" + car1Uuid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.data.uuid", is(car1Uuid.toString())))
                .andExpect(jsonPath("$.data.make", is(car1.getMake())))
                .andExpect(jsonPath("$.errors", empty()));
        verify(carService).read(car1Uuid);
    }

    @Test
    void getCarByUuidAdmin_shouldReturn404_whenCarNotFound() throws Exception {
        UUID nonExistentUuid = UUID.randomUUID();
        when(carService.read(nonExistentUuid)).thenThrow(new ResourceNotFoundException("Car not found with UUID: " + nonExistentUuid));
        mockMvc.perform(authenticatedGet("/api/v1/admin/cars/" + nonExistentUuid))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is("fail")))
                .andExpect(jsonPath("$.errors[0].message", containsString("Car not found with UUID: " + nonExistentUuid)));
        verify(carService).read(nonExistentUuid);
    }

    @Test
    void updateCar_shouldReturn200AndUpdatedCar_whenValidInputAndCarExists() throws Exception {
        // Corrected DTO instantiation
        CarUpdateDTO updateDTO = new CarUpdateDTO(
                "Toyota", "Corolla X", 2022, "ADM001-UPD", PriceGroup.STANDARD, "275.00", false
        );
        Car updatedCarEntity = CarMapper.applyUpdateDtoToEntity(updateDTO, car1);
        updatedCarEntity = new Car.Builder().copy(updatedCarEntity).setUuid(car1Uuid).build();

        when(carService.read(car1Uuid)).thenReturn(car1);
        when(carService.update(any(Car.class))).thenReturn(updatedCarEntity);

        mockMvc.perform(authenticatedPut("/api/v1/admin/cars/" + car1Uuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.data.uuid", is(car1Uuid.toString())))
                .andExpect(jsonPath("$.data.model", is("Corolla X")))
                .andExpect(jsonPath("$.data.available", is(false)))
                .andExpect(jsonPath("$.errors", empty()));
        verify(carService).read(car1Uuid);
        verify(carService).update(argThat(c -> c.getId() == car1.getId() && c.getModel().equals("Corolla X") && !c.isAvailable()));
    }

    @Test
    void updateCar_shouldReturn404_whenCarToUpdateNotFound() throws Exception {
        UUID nonExistentUuid = UUID.randomUUID();
        // Corrected DTO instantiation
        CarUpdateDTO updateDTO = new CarUpdateDTO(
                "NonExistent", "Model", 2020, "NEX001", PriceGroup.OTHER, "100.00", true
        );
        when(carService.read(nonExistentUuid)).thenThrow(new ResourceNotFoundException("Car not found with UUID: " + nonExistentUuid));
        mockMvc.perform(authenticatedPut("/api/v1/admin/cars/" + nonExistentUuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is("fail")))
                .andExpect(jsonPath("$.errors[0].message", containsString("Car not found with UUID: " + nonExistentUuid)));
        verify(carService).read(nonExistentUuid);
        verify(carService, never()).update(any(Car.class));
    }

    @Test
    void updateCar_shouldReturn400_whenInvalidUpdateInput() throws Exception {
        // Corrected DTO instantiation (null make for invalid input)
        CarUpdateDTO updateDTO = new CarUpdateDTO(
                null, "Corolla X", 2022, "ADM001-UPD", PriceGroup.STANDARD, "275.00", false
        );
        when(carService.read(car1Uuid)).thenReturn(car1);
        mockMvc.perform(authenticatedPut("/api/v1/admin/cars/" + car1Uuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is("fail")))
                .andExpect(jsonPath("$.errors", not(empty())))
                .andExpect(jsonPath("$.errors[0].field", is("make")));
        verify(carService).read(car1Uuid);
        verify(carService, never()).update(any(Car.class));
    }

    @Test
    void deleteCar_shouldReturn204_whenCarExistsAndDeletedSuccessfully() throws Exception {
        when(carService.read(car1Uuid)).thenReturn(car1);
        when(carService.delete(car1.getId())).thenReturn(true);
        mockMvc.perform(authenticatedDelete("/api/v1/admin/cars/" + car1Uuid))
                .andExpect(status().isNoContent());
        verify(carService).read(car1Uuid);
        verify(carService).delete(car1.getId());
    }

    @Test
    void deleteCar_shouldReturn404_whenCarToDeleteNotFoundDuringRead() throws Exception {
        UUID nonExistentUuid = UUID.randomUUID();
        when(carService.read(nonExistentUuid)).thenThrow(new ResourceNotFoundException("Car not found with UUID: " + nonExistentUuid));
        mockMvc.perform(authenticatedDelete("/api/v1/admin/cars/" + nonExistentUuid))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is("fail")))
                .andExpect(jsonPath("$.errors[0].message", containsString("Car not found with UUID: " + nonExistentUuid)));
        verify(carService).read(nonExistentUuid);
        verify(carService, never()).delete(anyInt());
    }

    @Test
    void deleteCar_shouldReturn404_ifServiceDeleteReturnsFalse() throws Exception {
        when(carService.read(car1Uuid)).thenReturn(car1);
        when(carService.delete(car1.getId())).thenReturn(false);
        mockMvc.perform(authenticatedDelete("/api/v1/admin/cars/" + car1Uuid))
                .andExpect(status().isNotFound());
        verify(carService).read(car1Uuid);
        verify(carService).delete(car1.getId());
    }

    @Test
    void getAllCarsForAdmin_shouldReturn401_whenNotAuthenticated() throws Exception {
        SecurityContextHolder.clearContext(); // Ensure no auth from previous tests
        // Crucially, for this test, ensure JwtUtilities and CustomerUserDetailsService are NOT mocked to succeed
        // or are mocked to reflect an invalid/missing token scenario if the filter relies on them.
        // For instance, if validateToken is called even for missing header (though unlikely):
        // when(jwtUtilities.validateToken(null)).thenReturn(false); // Or any specific handling
        // Or if extractUserEmail is called with a null token:
        // when(jwtUtilities.extractUserEmail(null)).thenThrow(new SomeJwtException("..."));

        mockMvc.perform(get("/api/v1/admin/cars")) // No Authorization header
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createCar_shouldReturn401_whenNotAuthenticated() throws Exception {
        SecurityContextHolder.clearContext();
        // Corrected DTO instantiation
        CarCreateDTO createDTO = new CarCreateDTO(
                "Honda", "Civic", 2024, "NEW001", PriceGroup.STANDARD, "300.00", true
        );
        mockMvc.perform(post("/api/v1/admin/cars") // No Authorization header
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isUnauthorized());
    }
}*/
