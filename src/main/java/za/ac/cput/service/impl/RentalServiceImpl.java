package za.ac.cput.service.impl;

/**
 * Author: Peter Buckingham (220165289)
 */

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.ac.cput.domain.entity.Booking;
import za.ac.cput.domain.entity.Car;
import za.ac.cput.domain.entity.Rental;
import za.ac.cput.domain.enums.RentalStatus;
import za.ac.cput.domain.security.User;
import za.ac.cput.exception.CarNotAvailableException;
import za.ac.cput.exception.UserCantRentMoreThanOneCarException;
import za.ac.cput.factory.impl.RentalFactory;
import za.ac.cput.repository.BookingRepository;
import za.ac.cput.repository.CarRepository;
import za.ac.cput.repository.RentalRepository;
import za.ac.cput.service.IBookingService;
import za.ac.cput.service.IRentalService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service("rentalServiceImpl")
public class RentalServiceImpl implements IRentalService {

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RentalFactory rentalFactory;
    @Autowired
    private UserService userService;
    @Autowired
    private IBookingService IBookingService;

    /*//convert Booking to Rental
    public RentalDTO convertBookingToRental(Booking booking) {
        RentalDTO rental = new RentalDTO();
        rental.setUser(userService.readDTO(booking.getUser().getId()));
        rental.setCar(booking.getCar());
 *//*     rental.setIssuer(booking.getIssuer());
        rental.setReceiver(booking.getReceiver());*//*
        rental.setFine(0.0);
        rental.setIssuedDate(booking.getBookingStartDate());
        rental.setReturnedDate(booking.getBookingEndDate());
        rental.setStatus(RentalStatus.ACTIVE);
        return rental;
    }*/


    // Check if car is available using Rental
    public boolean isCarAvailable(Rental rental) {
        Car carToRent = rental.getCar();
        return carRepository.existsByIdAndAvailableTrueAndDeletedFalse(carToRent.getId());
    }

    // Check if car is available using Car
    public boolean isCarAvailableByCarId(Car car) {

        return carRepository.existsByIdAndAvailableTrueAndDeletedFalse(car.getId());
    }


    @Override
    @Transactional
    public Rental create(Rental rental) {
        if (isCarAvailable(rental)) {
            if (isCurrentlyRenting(rental.getUser())) {
                throw new UserCantRentMoreThanOneCarException(generateUserRentingErrorMessage(rental.getUser()));
            }
            Rental newRental = rentalFactory.create(rental);
            return rentalRepository.save(newRental);
        } else {
            throw new CarNotAvailableException(generateCarNotAvailableErrorMessage(rental.getCar()));
        }
    }

    @Override
    public Rental read(Integer id) {
        return this.rentalRepository.findByIdAndDeletedFalse(id).orElse(null);
    }

    @Override
    public Rental read(UUID uuid) {
        Optional<Rental> rental = this.rentalRepository.findByUuidAndDeletedFalse(uuid);
        return rental.orElse(null);
    }


    @Override
    @Transactional
    public Rental update(Rental rental) {
        if (rentalRepository.existsByIdAndDeletedFalse(rental.getId())) {
            Rental updatedRental = rentalFactory.create(rental);
            Car car = updatedRental.getCar();
            car = Car.builder().copy(car).available(updatedRental.getReturnedDate() != null).build();
            /*car.setAvailable(updatedRental.getReturnedDate() != null);*/
            carRepository.save(car);
            return rentalRepository.save(updatedRental);
        }
        return null;
    }

    @Transactional
    public Rental update(int id, Rental rental) {
        return update(rental);
    }

    @Override
    public boolean delete(Integer id) {
        Rental rental = this.rentalRepository.findById(id).orElse(null);
        if (rental != null && !rental.isDeleted()) {
            Rental updatedRental = new Rental.Builder()
                    .copy(rental)
                    .setDeleted(true)
                    .build();
            this.rentalRepository.save(updatedRental);
            return true;
        }
        return false;
    }

    @Override
    public List<Rental> getAll() {
        return this.rentalRepository.findAllByDeletedFalse();
    }


    public List<Rental> getAllAvailableCars() {
        List<Rental> allRentals = rentalRepository.findAllByDeletedFalse();
        return filterAvailableCars(allRentals);
    }

    private List<Rental> filterAvailableCars(List<Rental> rentals) {
        ArrayList<Rental> availableCars = new ArrayList<>();
        for (Rental rental : rentals) {
            if (isCarAvailable(rental)) {
                availableCars.add(rental);
            }
        }
        return availableCars;
    }

    private String generateCarNotAvailableErrorMessage(Car car) {
        return car.getMake() + " " + car.getModel() + " " + car.getLicensePlate() +
                " is not available for rental at this time";
    }

    private String generateUserRentingErrorMessage(User user) {
        Rental currentRental = getCurrentRental(user);
        Car rentedCar = currentRental.getCar();
        return user.getFirstName() + " " + user.getLastName() +
                " is currently renting " + rentedCar.getMake() + " " + rentedCar.getModel() +
                " " + rentedCar.getLicensePlate();
    }



    public boolean isCurrentlyRenting(User user) {
        List<Rental> activeRentals = rentalRepository.findByUserIdAndReturnedDateIsNullAndDeletedFalse(user.getId());
        return !activeRentals.isEmpty();
    }

    public Rental getCurrentRental(User user) {
        List<Rental> activeRentals = rentalRepository.findByUserIdAndReturnedDateIsNullAndDeletedFalse(user.getId());
        return !activeRentals.isEmpty() ? activeRentals.get(0) : null;
    }



    @Override
    public boolean existsById(Integer id) {
        return rentalRepository.existsByIdAndDeletedFalse(id);
    }

    //@Override
    //public List<Rental> getRentalHistoryByUser(User user) {
    //    return rentalRepository.findByUserId(user.getId());
    //}

    public boolean isCarBooked(Car car, LocalDateTime startDate, LocalDateTime endDate) {
        List<Booking> activeBookings = bookingRepository.findByCarAndStatusAndEndDateAfterAndStartDateBeforeAndDeletedFalse(
                car, "CONFIRMED", startDate, endDate
        );
        return !activeBookings.isEmpty();
    }
    public List<Rental> getRentalHistoryByUser(User user) {

        List <Rental> rentals= rentalRepository.findByUserIdAndDeletedFalse(user.getId());
        return rentals;
    }



    public boolean isCarBooked(Car car) {
        List<Booking> activeBookings = bookingRepository.findByCarIdAndStatusAndDeletedFalse(car.getId(), "CONFIRMED");
        return !activeBookings.isEmpty();
    }

    public Rental findByUuid(UUID rentalId) {
        Optional<Rental> rental = rentalRepository.findByUuidAndDeletedFalse(rentalId);
        return rental.orElse(null);
    }
    @Override
    public Rental confirmRentalByUuid(UUID rentalUuid) {
        Rental rental = read(rentalUuid);
        if (rental.getStatus() != RentalStatus.PENDING_CONFIRMATION) {
            System.out.println("Only PENDING_CONFIRMATION rentals can be confirmed. Current status: " + rental.getStatus());
        }
        Car car = rental.getCar();
        if (!car.isAvailable() && (rental.getStatus() == RentalStatus.PENDING_CONFIRMATION || rental.getStatus() == RentalStatus.BOOKED) ) {
            // If car became unavailable after initial booking but before confirmation.
            // This check might be more complex if availability is nuanced.
            // throw new BadRequestException("Car is no longer available for this rental period.");
        }
        Rental updatedRental = new Rental.Builder().copy(rental)
                .setStatus(RentalStatus.CONFIRMED)
                .setIssuedDate(LocalDateTime.now())
                .build();
        Car updatedCar = Car.builder().copy(car).available(false).build(); // Mark car as unavailable

        carRepository.save(updatedCar); // Assuming Car entity is mutable for this flag
        return rentalRepository.save(updatedRental);
    }

    @Override
    public Rental cancelRentalByUuid(UUID rentalUuid) {
        Rental rental = read(rentalUuid);
        // Add business rules: e.g., cannot cancel if IN_PROGRESS or COMPLETED
        if (rental.getStatus() == RentalStatus.IN_PROGRESS || rental.getStatus() == RentalStatus.COMPLETED) {
            System.out.println("Cannot cancel a rental that is in progress or already completed.");
        }
        Rental updatedRental = new Rental.Builder().copy(rental).setStatus(RentalStatus.CANCELLED).build();
        Car car = rental.getCar();
        if (car == null) {
            System.out.println("Cannot cancel rental: Car not found.");
        }
        Car updatedCar = Car.builder().copy(car).available(true).build(); // Make car available again

            carRepository.save(updatedCar); // Save updated car status

        return rentalRepository.save(updatedRental);
    }

    @Override
    public Rental completeRentalByUuid(UUID rentalUuid, double fineAmount) {
        Rental rental = read(rentalUuid);
        if (rental.getStatus() != RentalStatus.IN_PROGRESS && rental.getStatus() != RentalStatus.CONFIRMED) { // Or only IN_PROGRESS
            System.out.println("Rental cannot be completed from its current state: " + rental.getStatus());
        }
        Rental updatedRental = new Rental.Builder().copy(rental)
                .setStatus(RentalStatus.COMPLETED)
                .setReturnedDate(LocalDateTime.now())
                .setFine((int) fineAmount)
                .build();

        // Make car available again
        Car car = rental.getCar();
        Car updatedCar = Car.builder().copy(car).available(true).build();


            carRepository.save(updatedCar); // Save updated car status

        return rentalRepository.save(updatedRental);
    }


}
