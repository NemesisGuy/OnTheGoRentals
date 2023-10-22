package za.ac.cput.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import za.ac.cput.controllers.BookingController;
import za.ac.cput.domain.Booking;
import za.ac.cput.service.BookingService;
import za.ac.cput.service.impl.BookingServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

public class BookingControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private BookingController bookingController;

    @Mock
    private BookingServiceImpl bookingService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(bookingController).build();
    }

@Test
    public void readBookingTest() throws Exception {
        Integer bookingId = 1;
        Booking booking = new Booking();
        booking.setId(bookingId);

        when(bookingService.read(bookingId)).thenReturn(booking);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/admin/bookings/read/" + bookingId))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(bookingService, times(1)).read(bookingId);
    }




    @Test
    public void deleteBookingTest() throws Exception {
        Integer bookingId = 1;

        when(bookingService.delete(bookingId)).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/admin/bookings/delete/" + bookingId))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(bookingService, times(1)).delete(bookingId);
    }
}
