package za.ac.cput.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import za.ac.cput.domain.dto.request.BookingRequestDTO;
import za.ac.cput.domain.entity.Booking;
import za.ac.cput.domain.entity.Car;
import za.ac.cput.domain.entity.Driver;
import za.ac.cput.domain.entity.security.Role;
import za.ac.cput.domain.entity.security.RoleName;
import za.ac.cput.domain.entity.security.User;
import za.ac.cput.domain.enums.BookingStatus;
import za.ac.cput.security.CustomerUserDetailsService;
import za.ac.cput.security.JwtUtilities;
import za.ac.cput.security.SpringSecurityConfig;
import za.ac.cput.service.IBookingService;
import za.ac.cput.service.ICarService;
import za.ac.cput.service.IUserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.Random;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// ... (imports remain the same) ...
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(BookingController.class)
@Import(SpringSecurityConfig.class)
@ActiveProfiles("test")
class BookingControllerTest {

    // ... (fields and @BeforeEach as before) ...
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IBookingService bookingService;
    @MockBean
    private ICarService carService;
    @MockBean
    private IUserService userService;
    @MockBean
    private JwtUtilities jwtUtilities;
    @MockBean
    private CustomerUserDetailsService customerUserDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private User sampleUser;
    private Car sampleCar;
    private Booking sampleBooking;
    private UUID carUuid;
    private UUID bookingUuid;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();

        Role userRole = Role.builder().id(1).roleName(RoleName.USER).build();
        sampleUser = User.builder()
                .id(1)
                .uuid(UUID.randomUUID())
                .firstName("Booker")
                .lastName("User")
                .email("booker@example.com")
                .password("encodedPassword")
                .roles(Collections.singletonList(userRole))
                .build();

        carUuid = UUID.randomUUID();
        sampleCar = new Car.Builder() // Using your custom builder
                .setId(1)
                .setUuid(carUuid)
                .setMake("Toyota")
                .setModel("Corolla")
                .setYear(2022)
                .setAvailable(true)
                .build();

        bookingUuid = UUID.randomUUID();
        sampleBooking = new Booking.Builder()
                .setId(1)
                .setUuid(bookingUuid)
                .setUser(sampleUser)
                .setCar(sampleCar)
                .setStartDate(LocalDateTime.now().plusDays(1).withNano(0))
                .setEndDate(LocalDateTime.now().plusDays(3).withNano(0))
                .setStatus(BookingStatus.CONFIRMED)
                .setCreatedAt(LocalDateTime.now().withNano(0))
                .setUpdatedAt(LocalDateTime.now().withNano(0))
                .setDeleted(false)
                .build();

        when(jwtUtilities.validateToken(anyString())).thenReturn(true);
        when(jwtUtilities.extractUserEmail(anyString())).thenReturn(sampleUser.getEmail());
        when(customerUserDetailsService.loadUserByUsername(sampleUser.getEmail()))
                .thenReturn(new org.springframework.security.core.userdetails.User(
                        sampleUser.getEmail(), sampleUser.getPassword(), sampleUser.getAuthorities()
                ));
    }

    private void mockAuthenticatedUser(User user) {
        List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleNameEnum().name()))
                .collect(Collectors.toList());
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                user.getEmail(), null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }


    @Test
    void createBooking_whenAuthenticatedAndCarAvailable_shouldCreateBooking() throws Exception {
        mockAuthenticatedUser(sampleUser);
        BookingRequestDTO bookingRequestDTO = new BookingRequestDTO(
                sampleUser.getUuid(),
                carUuid,
                LocalDateTime.now().plusDays(1).withNano(0),
                LocalDateTime.now().plusDays(3).withNano(0),
                null,
                null
        );

        when(userService.read(sampleUser.getEmail())).thenReturn(sampleUser);
        when(carService.read(carUuid)).thenReturn(sampleCar);

        when(bookingService.create(any(Booking.class))).thenAnswer(invocation -> {
            Booking bookingArg = invocation.getArgument(0);
            // This mock should return a Booking that, when mapped, matches the expected JSON
            Booking created = new Booking.Builder()
                    .setId(new Random().nextInt(10000) + 1)
                    .setUuid(UUID.randomUUID())
                    .setUser(bookingArg.getUser())
                    .setCar(bookingArg.getCar()) // Ensure the car object is set
                    .setDriver(bookingArg.getDriver())
                    .setStartDate(bookingArg.getStartDate())
                    .setEndDate(bookingArg.getEndDate())
                    .setStatus(BookingStatus.CONFIRMED)
                    .setCreatedAt(LocalDateTime.now().withNano(0))
                    .setUpdatedAt(LocalDateTime.now().withNano(0))
                    .setDeleted(false)
                    .build();
            // If BookingMapper.toDto creates a DTO that has car.uuid instead of carUuid directly:
            // BookingResponseDTO responseDto = BookingMapper.toDto(created);
            // System.out.println("Mocked created booking DTO: " + objectMapper.writeValueAsString(responseDto));
            return created;
        });

        mockMvc.perform(post("/api/v1/bookings")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingRequestDTO)))
                .andDo(print())
                .andExpect(status().isCreated())
                // Corrected based on the logged JSON structure:
                .andExpect(jsonPath("$.data.car.uuid", is(carUuid.toString())))
                .andExpect(jsonPath("$.data.status", is(BookingStatus.CONFIRMED.name())));

        verify(userService).read(sampleUser.getEmail());
        verify(carService).read(carUuid);
        verify(bookingService).create(any(Booking.class));
    }

    @Test
    void createBooking_whenCarNotFound_shouldReturnNotFound() throws Exception {
        mockAuthenticatedUser(sampleUser);
        UUID nonExistentCarUuid = UUID.randomUUID();
        BookingRequestDTO bookingRequestDTO = new BookingRequestDTO(
                sampleUser.getUuid(), nonExistentCarUuid,
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(3), null, null);

        when(userService.read(sampleUser.getEmail())).thenReturn(sampleUser);
        when(carService.read(nonExistentCarUuid)).thenReturn(null);

        mockMvc.perform(post("/api/v1/bookings")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingRequestDTO)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0].message", is("Car not found with UUID: " + nonExistentCarUuid)));
    }

    @Test
    void getBookingByUuid_whenBookingExistsAndUserOwns_shouldReturnBooking() throws Exception {
        mockAuthenticatedUser(sampleUser);
        when(bookingService.read(bookingUuid)).thenReturn(sampleBooking);
        when(userService.read(sampleUser.getEmail())).thenReturn(sampleUser);

        mockMvc.perform(get("/api/v1/bookings/{bookingUuid}", bookingUuid)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.uuid", is(bookingUuid.toString())));
    }

    @Test
    void getBookingByUuid_whenUserDoesNotOwn_shouldReturnForbidden() throws Exception {
        User anotherUser = User.builder().id(2).email("another@example.com")
                .roles(Collections.singletonList(Role.builder().roleName(RoleName.USER).build())).build();
        mockAuthenticatedUser(anotherUser);

        when(bookingService.read(bookingUuid)).thenReturn(sampleBooking);
        when(userService.read(anotherUser.getEmail())).thenReturn(anotherUser);

        mockMvc.perform(get("/api/v1/bookings/{bookingUuid}", bookingUuid)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void getCurrentUserBookings_whenAuthenticated_shouldReturnUserBookings() throws Exception {
        mockAuthenticatedUser(sampleUser);
        List<Booking> userBookings = Arrays.asList(sampleBooking);
        when(userService.read(sampleUser.getEmail())).thenReturn(sampleUser);
        when(bookingService.getUserBookings(sampleUser.getId())).thenReturn(userBookings);

        mockMvc.perform(get("/api/v1/bookings/my-bookings")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].uuid", is(bookingUuid.toString())));
    }

    @Test
    void getCurrentUserBookings_whenUnauthenticated_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/bookings/my-bookings")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateBooking_whenAuthenticatedAndOwner_shouldUpdateBooking() throws Exception {
        mockAuthenticatedUser(sampleUser);
        UUID newCarUuid = UUID.randomUUID();

        Car newCar = new Car.Builder().setId(2).setUuid(newCarUuid).setMake("Honda")
                .setModel("Civic").setAvailable(true).setYear(2023).build();

        BookingRequestDTO updateRequest = new BookingRequestDTO(
                sampleUser.getUuid(), newCarUuid,
                sampleBooking.getStartDate().plusDays(1),
                sampleBooking.getEndDate().plusDays(2),
                null, null
        );

        when(bookingService.read(bookingUuid)).thenReturn(sampleBooking);
        when(userService.read(sampleUser.getEmail())).thenReturn(sampleUser);
        when(carService.read(newCarUuid)).thenReturn(newCar);

        when(bookingService.update(any(Booking.class))).thenAnswer(invocation -> {
            Booking bookingToUpdate = invocation.getArgument(0);
            // Ensure the returned Booking object reflects the changes, especially the new car
            return new Booking.Builder().copy(bookingToUpdate)
                    .setCar(newCar) // Make sure newCar is set
                    .setStartDate(updateRequest.getBookingStartDate())
                    .setEndDate(updateRequest.getBookingEndDate())
                    .setUpdatedAt(LocalDateTime.now().withNano(0))
                    .build();
        });

        mockMvc.perform(put("/api/v1/bookings/{bookingUuid}", bookingUuid)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                // Corrected JSONPaths based on logged structure
                .andExpect(jsonPath("$.data.car.uuid", is(newCarUuid.toString())))
                .andExpect(jsonPath("$.data.bookingStartDate").value(is(updateRequest.getBookingStartDate().toString())));

        verify(bookingService).update(any(Booking.class));
    }

    @Test
    void confirmBooking_whenAuthenticatedAndOwner_shouldConfirm() throws Exception {
        mockAuthenticatedUser(sampleUser);
        Booking pendingBooking = new Booking.Builder().copy(sampleBooking).setStatus(BookingStatus.PENDING).build();
        Booking confirmedBooking = new Booking.Builder().copy(pendingBooking).setStatus(BookingStatus.CONFIRMED).build();

        when(bookingService.read(bookingUuid)).thenReturn(pendingBooking);
        when(userService.read(sampleUser.getEmail())).thenReturn(sampleUser);
        when(bookingService.confirmBooking(pendingBooking.getId())).thenReturn(confirmedBooking);

        mockMvc.perform(post("/api/v1/bookings/{bookingUuid}/confirm", bookingUuid)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.uuid", is(bookingUuid.toString())))
                .andExpect(jsonPath("$.data.status", is(BookingStatus.CONFIRMED.name())));
    }

    @Test
    void cancelBooking_whenAuthenticatedAndOwner_shouldCancel() throws Exception {
        mockAuthenticatedUser(sampleUser);
        Booking activeBooking = new Booking.Builder().copy(sampleBooking).setStatus(BookingStatus.CONFIRMED).build();
        Booking cancelledBooking = new Booking.Builder().copy(activeBooking).setStatus(BookingStatus.USER_CANCELLED).build();

        when(bookingService.read(bookingUuid)).thenReturn(activeBooking);
        when(userService.read(sampleUser.getEmail())).thenReturn(sampleUser);
        when(bookingService.cancelBooking(activeBooking.getId())).thenReturn(cancelledBooking);

        mockMvc.perform(post("/api/v1/bookings/{bookingUuid}/cancel", bookingUuid)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.uuid", is(bookingUuid.toString())))
                .andExpect(jsonPath("$.data.status", is(BookingStatus.USER_CANCELLED.name())));
    }

    @Test
    void getAvailableCarsForBooking_shouldReturnAvailableCars() throws Exception {
        // Assuming /api/v1/bookings/available-cars is permitAll in SecurityConfig
        // If not, add mockAuthenticatedUser(sampleUser);
        UUID car1Uuid = UUID.randomUUID();
        Car car1 = new Car.Builder().setId(10).setUuid(car1Uuid).setMake("Honda").setModel("Civic").setAvailable(true).setYear(2023).build();
        UUID car2Uuid = UUID.randomUUID();
        Car car2 = new Car.Builder().setId(11).setUuid(car2Uuid).setMake("Mazda").setModel("3").setAvailable(true).setYear(2022).build();

        List<Car> availableCars = Arrays.asList(car1, car2);
        when(carService.findAllAvailableAndNonDeleted()).thenReturn(availableCars);

        mockMvc.perform(get("/api/v1/bookings/available-cars")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk()) // Should not be 401 if permitAll
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].make", is("Honda")))
                .andExpect(jsonPath("$.data[1].make", is("Mazda")));
    }

    @Test
    void getCurrentUserProfileForBooking_whenAuthenticated_shouldReturnProfile() throws Exception {
        mockAuthenticatedUser(sampleUser);
        when(userService.read(sampleUser.getEmail())).thenReturn(sampleUser);

        mockMvc.perform(get("/api/v1/bookings/user-profile")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email", is(sampleUser.getEmail())));
    }
}