package za.ac.cput.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.ac.cput.domain.entity.Booking;
import za.ac.cput.domain.entity.Car;
import za.ac.cput.domain.entity.security.User;
import za.ac.cput.exception.CarNotAvailableException;
import za.ac.cput.exception.ResourceNotFoundException;
import za.ac.cput.repository.BookingRepository;
import za.ac.cput.repository.CarRepository;
import za.ac.cput.service.IBookingService;

import java.util.List;
import java.util.UUID;

@Service
public class IBookingServiceImpl implements IBookingService {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private CarRepository carRepository;
    @Autowired
    private UserServiceImpl  userService;
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
/// /////////////////////////////////////////////////////////////////////////////////////////
    public Booking createBooking(Booking booking) {
        // Check if car is available
        //get car from its uuid
        Car car = carRepository.findByUuid(booking.getCar().getUuid()).orElse(null);
        if (car == null) {
            throw new ResourceNotFoundException("Car not found");
        }//get user from its uuid
        User user = userService.read(booking.getUser().getUuid());

        //User user = userService.read(booking.getUser().getId());
        List<Booking> activeBookings = bookingRepository.findByCarIdAndStatusAndDeletedFalse(car.getId(), "CONFIRMED");
        if (!activeBookings.isEmpty()) {
            throw new CarNotAvailableException("Car is already booked for the selected period.");
        }
        Booking.Builder builder = new Booking.Builder().copy(booking);

        builder.setStatus("PENDING");
        builder.setCar(car);
        builder.setUser(user);
        return bookingRepository.save(booking);
    }
    @Override
    public Booking confirmBooking(int bookingId) {
        Booking booking = bookingRepository.findByIdAndDeletedFalse(bookingId).orElse(null);
        if (booking != null) {
            Booking updatedBooking = new Booking.Builder().copy(booking).setStatus("CONFIRMED").build();
            booking = updatedBooking;
            return bookingRepository.save(booking);
        }
        return null;
    }
    @Override
    public Booking cancelBooking(int bookingId) {
        Booking booking = bookingRepository.findByIdAndDeletedFalse(bookingId).orElse(null);
        if (booking != null) {
            Booking updatedBooking = new Booking.Builder().copy(booking).setStatus("CANCELED").build();
            booking = updatedBooking;
            return bookingRepository.save(booking);
        }
        return null;
    }
    @Override
    public List<Booking> getUserBookings(int userId) {
        return bookingRepository.findByUserIdAndDeletedFalse(userId);
    }
/*
*
* public class BookingDTO {
    private int id;
    private UserDTO user;
    private Car car;
    private LocalDateTime bookingStartDate;
    private LocalDateTime bookingEndDate;
    private String status;
}

* */
    @Override
    public Booking read(int id) {
        return bookingRepository.findByIdAndDeletedFalse(id).orElse(null);
    }

    @Override
    public Booking read(UUID uuid) {
        return bookingRepository.findByUuidAndDeletedFalse(uuid).orElse(null);
    }



    //@Override
    public Booking updateById(Booking updatedBooking) {
        Booking existingBooking = bookingRepository.findById(updatedBooking.getId()).orElse(null);

        if (existingBooking != null) {
            Booking.Builder builder = new Booking.Builder().copy(existingBooking);
            System.out.println("BookingServiceImpl: updateById, updating booking with ID: " + updatedBooking.getId());

            builder.setStartDate(updatedBooking.getStartDate());
            builder.setEndDate(updatedBooking.getEndDate());
            // Update other properties as needed

            return bookingRepository.save(existingBooking);
        } else {
            return null; // You can also throw an exception to indicate the resource was not found
        }
    }

    @Override
    public boolean delete(int id) {
        Booking booking = bookingRepository.findById(id).orElse(null);
        if (booking == null) {
            //throw new ResourceNotFoundException("Booking not found")
            return false;
        }
        booking = new Booking.Builder().copy(booking).setDeleted(true).build();
        bookingRepository.save(booking);
        return true;

    }

    @Override
    public List<Booking> getAll() {
        return bookingRepository.findByDeletedFalse();
    }




    @Override
    public Booking update(Booking booking) {
        if (booking.getId() != 0 && bookingRepository.existsById(booking.getId())) {
            return bookingRepository.save(booking);
        }

        throw new ResourceNotFoundException("Booking not found");
    }


}
