package za.ac.cput.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.ac.cput.domain.Booking;
import za.ac.cput.factory.impl.BookingFactory;
import za.ac.cput.repository.BookingRepository;
import za.ac.cput.service.BookingService;

import java.util.List;
import java.util.Optional;

@Service("bookingServiceImpl")
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final BookingFactory bookingFactory;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository, BookingFactory bookingFactory) {
        this.bookingRepository = bookingRepository;
        this.bookingFactory = bookingFactory;
    }

    @Override
    public Booking createBooking(Booking booking) {
        Booking newBooking = bookingFactory.createBooking(
                booking.getId(),
                booking.getUser(),
                booking.getCar(),
                booking.getStartDate(),
                booking.getEndDate(),
                booking.getTotalPrice(),
                booking.isConfirmed()
        );
        return bookingRepository.save(newBooking);
    }

    @Override
    public Booking updateBooking(Booking booking) {
        if (bookingRepository.existsById(booking.getId())) {
            Booking updatedBooking = bookingFactory.createBooking(
                    booking.getId(),
                    booking.getUser(),
                    booking.getCar(),
                    booking.getStartDate(),
                    booking.getEndDate(),
                    booking.getTotalPrice(),
                    booking.isConfirmed()
            );
            return bookingRepository.save(updatedBooking);
        }
        return null;
    }

    @Override
    public void deleteBooking(int bookingId) {
        bookingRepository.deleteById(bookingId);
    }

    @Override
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    @Override
    public List<Booking> getBookingsByUserId(int userId) {
        return bookingRepository.findByUserId(userId);
    }

    @Override
    public Booking getBookingById(int bookingId) {
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        return optionalBooking.orElse(null);
    }
}
