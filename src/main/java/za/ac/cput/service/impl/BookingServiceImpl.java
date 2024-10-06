package za.ac.cput.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.ac.cput.domain.Booking;
import za.ac.cput.domain.Car;
import za.ac.cput.domain.security.User;
import za.ac.cput.exception.CarNotAvailableException;
import za.ac.cput.exception.ResourceNotFoundException;
import za.ac.cput.repository.BookingRepository;
import za.ac.cput.repository.CarRepository;
import za.ac.cput.service.BookingService;

import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private CarRepository carRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private CarServiceImpl carService;

    @Override
    public Booking create(Booking booking) {
        System.out.println("BookingServiceImpl: create");
        if (booking.getId() != 0) {
            throw new IllegalArgumentException("ID should not be set for a new entity.");
        }
        /*if(booking.getCar().getId() )*/
        return bookingRepository.save(booking);
    }

    public Booking createBooking(Booking booking) {
        // Check if car is available
        int carId = booking.getCar().getId();
        Car car = carService.read(carId);
        User user = userService.read(booking.getUser().getId());
        List<Booking> activeBookings = bookingRepository.findByCarIdAndStatus(carId, "CONFIRMED");
        if (!activeBookings.isEmpty()) {
            throw new CarNotAvailableException("Car is already booked for the selected period.");
        }
        booking.setStatus("PENDING");
        booking.setCar(car);
        booking.setUser(user);
        return bookingRepository.save(booking);
    }

    public Booking confirmBooking(int bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking != null) {
            booking.setStatus("CONFIRMED");
            return bookingRepository.save(booking);
        }
        return null;
    }

    public Booking cancelBooking(int bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking != null) {
            booking.setStatus("CANCELED");
            return bookingRepository.save(booking);
        }
        return null;
    }

    public List<Booking> getUserBookings(int userId) {
        return bookingRepository.findByUserId(userId);
    }

    @Override
    public Booking read(int id) {
        return bookingRepository.findById(id).orElse(null);
    }

    //@Override
    public Booking updateById(Booking updatedBooking) {
        Booking existingBooking = bookingRepository.findById(updatedBooking.getId()).orElse(null);

        if (existingBooking != null) {
            System.out.println("BookingServiceImpl: updateById, updating booking with ID: " + updatedBooking.getId());
            existingBooking.setBookingStartDate(updatedBooking.getBookingStartDate());
            existingBooking.setBookingEndDate(updatedBooking.getBookingEndDate());
            // Update other properties as needed

            return bookingRepository.save(existingBooking);
        } else {
            return null; // You can also throw an exception to indicate the resource was not found
        }
    }

    @Override
    public boolean delete(int id) {
        bookingRepository.deleteById(id);
        return true;
    }

    @Override
    public List<Booking> getAll() {
        return bookingRepository.findAll();
    }

    @Override
    public Booking getBookingById(int bookingId) {
        return bookingRepository.findById(bookingId).orElse(null);
    }

    @Override
    public Booking update(Booking booking)
    {
        if (booking.getId() != 0) {
            return bookingRepository.save(booking);
        }

        throw new ResourceNotFoundException("Booking not found");
    }



}
