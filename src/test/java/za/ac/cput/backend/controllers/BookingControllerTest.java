package za.ac.cput.backend.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import za.ac.cput.controllers.BookingController;
import za.ac.cput.domain.Booking;
import za.ac.cput.service.BookingService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    private List<Booking> bookingList;

    @BeforeEach
    public void setUp() {
        bookingList = new ArrayList<>();
        bookingList.add(new Booking());

    }

    @Test
    public void testGetAllBookings() throws Exception {

        when(bookingService.getAllBookings()).thenReturn(bookingList);


        mockMvc.perform(get("/api/bookings/list/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.length()").value(bookingList.size()));
    }

    @Test
    public void testGetBookingsByUser() throws Exception {
        int userId = 1; // Re
        when(bookingService.getBookingsByUserId(userId)).thenReturn(bookingList);

        // Perform the GET request
        mockMvc.perform(get("/api/bookings/list/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.length()").value(bookingList.size()));
    }

}
