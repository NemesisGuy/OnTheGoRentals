/*
package za.ac.cput.controllers.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import za.ac.cput.domain.Booking;
import za.ac.cput.service.impl.BookingServiceImpl;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminBookingController.class)
public class AdminBookingControllerTest {

    @MockBean
    private BookingServiceImpl bookingService;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        // Reset the mocks before each test
        reset(bookingService);
    }

    @Test
    public void getAllBookings() throws Exception {
        Booking booking = new Booking();
        booking.setId(1);
        List<Booking> bookings = Collections.singletonList(booking);

        when(bookingService.getAll()).thenReturn(bookings);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/admin/bookings/list/all"))
                .andExpect(status().isOk());

        verify(bookingService, times(1)).getAll();
    }

    @Test
    public void createBooking() throws Exception {
        Booking booking = new Booking();
        booking.setId(1);

        when(bookingService.create(any(Booking.class))).thenReturn(booking);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/admin/bookings/create")
                        .content(new ObjectMapper().writeValueAsString(booking))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(bookingService, times(1)).create(any(Booking.class));
    }

    @Test
    public void readBooking() throws Exception {
        Integer bookingId = 1;
        Booking booking = new Booking();
        booking.setId(bookingId);

        when(bookingService.read(eq(bookingId))).thenReturn(booking);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/admin/bookings/admin/read/" + bookingId))
                .andExpect(status().isOk());

        verify(bookingService, times(1)).read(eq(bookingId));
    }

    @Test
    public void updateBooking() throws Exception {
        Integer bookingId = 1;
        Booking booking = new Booking();
        booking.setId(bookingId);

        when(bookingService.updateById(eq(bookingId), any(Booking.class))).thenReturn(booking);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/admin/bookings/admin/update/" + bookingId)
                        .content(new ObjectMapper().writeValueAsString(booking))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(bookingService, times(1)).updateById(eq(bookingId), any(Booking.class));
    }

    @Test
    public void deleteBooking() throws Exception {
        Integer bookingId = 1;

        when(bookingService.delete(eq(bookingId))).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/admin/bookings/admin/delete/" + bookingId))
                .andExpect(status().isOk());

        verify(bookingService, times(1)).delete(eq(bookingId));
    }
}
*/
