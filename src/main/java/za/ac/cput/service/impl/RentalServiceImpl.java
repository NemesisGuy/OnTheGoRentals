package za.ac.cput.service.impl;
/**
 * Author: Peter Buckingham (220165289)
 */

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.ac.cput.domain.Car;
import za.ac.cput.domain.PriceGroup;
import za.ac.cput.domain.Rental;
/*import za.ac.cput.domain.User;*/
import za.ac.cput.exception.CarNotAvailableException;
import za.ac.cput.exception.UserCantRentMoreThanOneCarException;
import za.ac.cput.factory.impl.RentalFactory;
import za.ac.cput.repository.CarRepository;
import za.ac.cput.repository.RentalRepository;
import za.ac.cput.domain.security.User;
import za.ac.cput.service.IRentalService;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service("rentalServiceImpl")
public class RentalServiceImpl implements IRentalService {
    @Autowired
    private RentalRepository repository;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private RentalFactory rentalFactory;
    private Car car;

    //is car available
    public boolean isCarAvailable(Rental rental) {
        Car carToRent = rental.getCar();
        return carRepository.existsByIdAndIsAvailableIsTrue((int) carToRent.getId());
    }

    public boolean isCarAvailableByCarId(Car car) {
        this.car = car;
        return carRepository.existsByIdAndIsAvailableIsTrue((int) car.getId());
    }
    @Override
    @Transactional
    public Rental create(Rental rental) {
        if (isCarAvailable(rental)) {
            if (isCurrentlyRenting(rental.getUser())) {
                throw new UserCantRentMoreThanOneCarException(
                        generateUserRentingErrorMessage(rental.getUser()));
            }
            Rental newRental = rentalFactory.create(rental);
            if (newRental.getReturnedDate() != null) {
                carRepository.setIsAvailableToTrue((int) newRental.getCar().getId());
                System.out.println("Is car available after update: " + newRental.getCar().isAvailable());
            }else {
                carRepository.setIsAvailableToFalse((int) newRental.getCar().getId());
                System.out.println("Is car available after update: " + newRental.getCar().isAvailable());
            }
           // carRepository.setIsAvailableToFalse((int) newRental.getCar().getId());
            return repository.save(newRental);
        } else {
            throw new CarNotAvailableException(generateCarNotAvailableErrorMessage(rental.getCar()));
        }
    }

    @Override
    public Rental read(Integer id) {
        return this.repository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Rental update(Rental rental) {
        System.out.println("RentalServiceImpl.update : ");
        System.out.println("rental Id received : " + rental.getRentalId());

        if (repository.existsById(rental.getRentalId())) {

            System.out.println("Rental " + rental.getRentalId() + " found");
            System.out.println(rental);
            Rental updatedRental = rentalFactory.create(rental);
            // Set car to available if the rental was returned
            if (updatedRental.getReturnedDate() != null) {
                Car car = updatedRental.getCar();
                car.setAvailable(true);
                carRepository.save(car); // Save the updated car entity
                System.out.println("Is car available after update: " + car.isAvailable());
            }else {
                Car car = updatedRental.getCar();
                car.setAvailable(false);
                carRepository.save(car); // Save the updated car entity
                System.out.println("Is car available after update: " + car.isAvailable());
            }

            return repository.save(updatedRental);
        }
        System.out.println("Rental " + rental.getRentalId() + " not found");
        return null;
    }

    @Override
    public boolean delete(Integer id) {
        if (this.repository.existsById(id)) {
            this.repository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public ArrayList<Rental> getAll() {
        return (ArrayList<Rental>) this.repository.findAll();
    }

    //get all available cars
    // needs to be implemented
    public ArrayList<Rental> getAllAvailableCars() {
        List<Rental> allRentals = repository.findAll();
        //filter all rentals to only include available cars
        return filterAvailableCars(allRentals);
    }

    private ArrayList<Rental> filterAvailableCars(List<Rental> rentals) {
        ArrayList<Rental> availableCars = new ArrayList<>();
        for (Rental rental : rentals) {
            if (isCarAvailable(rental)) {
                availableCars.add(rental);
            }
        }
        return availableCars;
    }

    private String generateCarNotAvailableErrorMessage(Car car) {
        return car.getMake() + " " + car.getModel() + " " +
                car.getLicensePlate() + " is not available for rental at this time";
    }

    private String generateUserRentingErrorMessage(User user) {
        Rental currentRental = getCurrentRental(user);
        Car rentedCar = currentRental.getCar();
        return user.getFirstName() + " " + user.getLastName() + " is currently renting " +
                rentedCar.getMake() + " " + rentedCar.getModel() + " " +
                rentedCar.getLicensePlate();
    }

    //refactored isCarAvailable
//    private boolean isCarAvailable(Rental rental) {
//        Car carToRent = rental.getCar();
//        Optional<Rental> rentalFromDatabaseOptional = findMostRecentRentalByCarId(carToRent.getId());
//        Rental rentalFromDatabase = rentalFromDatabaseOptional.orElse(null);
//        printRentalInfo(rentalFromDatabase, rental.getReturnedDate());
//        return isCarAvailableBasedOnRental(rental);
//    }

    private Optional<Rental> findMostRecentRentalByCarId(Long carId) {
        return repository.findTopByCarIdOrderByReturnedDateDesc(Math.toIntExact(carId));
    }

    private void printRentalInfo(Rental rentalFromDatabase, LocalDateTime returnedDate) {
        if (returnedDate != null) {
            LocalDateTime timeSinceLastRental = returnedDate.minusDays(rentalFromDatabase.getReturnedDate().getDayOfMonth());
            System.out.println("Rental Date - rentalFromDatabase: " + timeSinceLastRental);
        } else {
            System.out.println("The car has not been returned yet");
            LocalDateTime timeSinceLastRental = LocalDateTime.now().minusDays(rentalFromDatabase.getIssuedDate().getDayOfMonth());
            System.out.println("Rental Date - rentalFromDatabase: " + rentalFromDatabase.getIssuedDate().getDayOfMonth());
        }
    }


    //refactored isCarAvailableByCarId
//    public boolean isCarAvailableByCarId(Car car) {
//        Optional<Rental> rentalByCarIdOrderByReturnedDateDesc = findMostRecentRentalByCarId(car.getId());
//
//        if (rentalByCarIdOrderByReturnedDateDesc.isPresent()) {
//            Rental rentalFromDatabase = rentalByCarIdOrderByReturnedDateDesc.get();
//
//            if (rentalFromDatabase.getReturnedDate() != null) {
//                printRentalInfoForCarAvailability(rentalFromDatabase, "Car is available");
//                // If the most recent rental is returned 24 hours before the new rental, then the car is available
//                return isCarAvailableBasedOnRental(rentalFromDatabase);
//            } else {
//                printRentalInfoForCarAvailability(rentalFromDatabase, "Car is not available");
//            }
//        }
//
//        return false;
//    }

    /// refactored
    private void printRentalInfoForCarAvailability(Rental rentalFromDatabase, String availabilityMessage) {

        if (rentalFromDatabase.getReturnedDate() != null) {
            // Calculate time since returned
            Duration timeSinceReturned = Duration.between(rentalFromDatabase.getReturnedDate(), LocalDateTime.now());

            // Check if over 24 hours
            if (timeSinceReturned.toHours() >= 24) {
                System.out.println("Over 24 hours since car was returned");
            } else {
                System.out.println("Under 24 hours since car was returned");
            }

        } else {
            System.out.println("The car has not been returned yet");
        }
        System.out.println(availabilityMessage);
    }

   /* private boolean isCarAvailableBasedOnRental(Rental rentalFromDatabase) {
        return rentalFromDatabase.getReturnedDate().plusDays(1).isBefore(LocalDateTime.now());
    }*/

    //refactored

    private boolean isCarAvailableBasedOnRental(Rental rentalFromDatabase) {

        // Handle null rental
        if (rentalFromDatabase == null) {
            return true;
        }

        // Handle null return date
        if (rentalFromDatabase.getReturnedDate() == null) {
            System.out.println("Rental has not been returned yet");
            return false;
        }

        // Calculate rental duration
        // Check if over 24 hours
        // Calculate rental duration
        LocalDateTime rentalReturned = rentalFromDatabase.getReturnedDate();
        LocalDateTime now = LocalDateTime.now();

        Duration rentalDuration = Duration.between(rentalReturned, now);

        return rentalDuration.toHours() >= 24;

    }


    public boolean isCurrentlyRenting(User user) {
        // Find active rentals for the user
        List<Rental> activeRentals = repository.findByUserIdAndReturnedDateIsNull(user.getId());

        // If the user has any active rentals, they are currently renting a car
        return !activeRentals.isEmpty();
    }

    public Rental getCurrentRental(User user) {
        // Find active rentals for the user
        List<Rental> activeRentals = repository.findByUserIdAndReturnedDateIsNull(user.getId());

        // If the user has any active rentals, they are currently renting a car
        if (!activeRentals.isEmpty()) {
            return activeRentals.get(0);
        }
        return null;
    }

    //TODO: This is to be used to replace filtering currently done in the controller layer
    public List<Car> getAvailableCarsByPrice(PriceGroup priceGroup) {
        // Join to rentals table and check availability
        // check if car is available
        ArrayList<Car> availableCars = new ArrayList<>(carRepository.findByPriceGroupAndRentalsReturnedDateIsNotNull(priceGroup));
        for (Car car : availableCars)   //for each car in available cars
        {
            if (!isCarAvailableByCarId(car)) //if car is not available
            {
                availableCars.remove(car); //remove car from available cars
            }
        }
        return availableCars;


    }
}







