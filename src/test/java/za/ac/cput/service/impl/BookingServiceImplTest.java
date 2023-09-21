package za.ac.cput.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import za.ac.cput.domain.Booking;
import za.ac.cput.factory.impl.BookingFactory;
import za.ac.cput.repository.BookingRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BookingServiceImplTest {

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingFactory bookingFactory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testUpdateBooking() {
        Booking booking = new Booking();
        Booking updatedBooking = new Booking();
        when(bookingRepository.existsById(anyInt())).thenReturn(true);
        when(bookingRepository.save(booking)).thenReturn(updatedBooking);
        Booking result = bookingService.updateBooking(booking);
        assertNotNull(result);
        assertEquals(updatedBooking, result);
        verify(bookingRepository, times(1)).existsById(anyInt());
        verify(bookingRepository, times(1)).save(booking);
    }

    @Test
    void testUpdateBookingWhenNotExists() {
        Booking booking = new Booking();
        when(bookingRepository.existsById(anyInt())).thenReturn(false);
        Booking result = bookingService.updateBooking(booking);
        assertNull(result);
        verify(bookingRepository, times(1)).existsById(anyInt());
        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void testDeleteBooking() {
        int bookingId = 1;
        bookingService.deleteBooking(bookingId);
        verify(bookingRepository, times(1)).deleteById(bookingId);
    }

    @Test
    void testGetAllBookings() {
        List<Booking> bookings = new ArrayList<>();
        when(bookingRepository.findAll()).thenReturn(bookings);
        List<Booking> result = bookingService.getAllBookings();
        assertNotNull(result);
        assertEquals(bookings, result);
        verify(bookingRepository, times(1)).findAll();
    }

    @Test
    void testGetBookingsByUserId() {
        int userId = 1;
        List<Booking> bookings = new ArrayList<>();
        when(bookingRepository.findByUserId(userId)).thenReturn(bookings);
        List<Booking> result = bookingService.getBookingsByUserId(userId);
        assertNotNull(result);
        assertEquals(bookings, result);
        verify(bookingRepository, times(1)).findByUserId(userId);
    }

    @Test
    void testGetBookingById() {
        int bookingId = 1;
        Booking booking = new Booking();
        Optional<Booking> optionalBooking = Optional.of(booking);
        when(bookingRepository.findById(bookingId)).thenReturn(optionalBooking);
        Booking result = bookingService.getBookingById(bookingId);
        assertNotNull(result);
        assertEquals(booking, result);
        verify(bookingRepository, times(1)).findById(bookingId);
    }

    @Test
    void testGetBookingByIdWhenNotExists() {
        int bookingId = 1;
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());
        Booking result = bookingService.getBookingById(bookingId);
        assertNull(result);
        verify(bookingRepository, times(1)).findById(bookingId);
    }
}

