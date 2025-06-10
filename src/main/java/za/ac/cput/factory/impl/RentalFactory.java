package za.ac.cput.factory.impl;

import org.springframework.stereotype.Component;
import za.ac.cput.domain.entity.Car;
import za.ac.cput.domain.entity.Driver;
import za.ac.cput.domain.entity.Rental;
import za.ac.cput.domain.entity.security.User;
import za.ac.cput.domain.enums.RentalStatus;
import za.ac.cput.factory.IFactory;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Pete Buckingham - 220165289
 * Date: April 2023
 * RentalFactory Class.java
 */
@Component
public class RentalFactory implements IFactory<Rental> {

    public Rental create(int id, User user, Car car, UUID issuer, UUID receiver, int fine,
                         LocalDateTime issuedDate, LocalDateTime returnedDate) {

        RentalStatus status = determineStatus(issuedDate, returnedDate);

        return new Rental.Builder()
                .setId(id)
                .setUser(user)
                .setCar(car)
                .setIssuer(issuer)
                .setReceiver(receiver)
                .setFine(fine)
                .setIssuedDate(issuedDate)
                .setReturnedDate(returnedDate)
                .setStatus(status)
                .build();
    }

    public Rental create() {
        return new Rental.Builder()
                //.setId(new Random().nextInt(1000000))
                .setStatus(RentalStatus.ACTIVE)
                .build();
    }

    /*    public Rental create(Rental rental) {
            RentalStatus status = determineStatus(rental.getIssuedDate(), rental.getReturnedDate());

            return new Rental.Builder()
                    .copy(rental)
                    .setStatus(status)
                    .build();
        }*/
    public Rental create(Rental rental) {
        Rental.Builder newRental = new Rental.Builder()
                .copy(rental);

        if (rental.getReturnedDate() != null) {
            newRental.setStatus(RentalStatus.COMPLETED);
        } else {
            newRental.setStatus(RentalStatus.ACTIVE);
        }

        return newRental.build();
    }


    private RentalStatus determineStatus(LocalDateTime issued, LocalDateTime returned) {
        if (returned == null || returned.isAfter(LocalDateTime.now())) {
            return RentalStatus.ACTIVE;
        } else {
            return RentalStatus.COMPLETED;
        }
    }

    public Rental create(User user, Car carToRent, Driver driver, UUID issuerId, LocalDateTime issuedDate,LocalDateTime expectedReturnDate,  LocalDateTime endDate) {
        RentalStatus status = determineStatus(issuedDate, endDate);
        //Default to the server's current time if the DTO doesn't provide a specific pickup time.
        LocalDateTime pickupTime = (issuedDate != null) ? issuedDate : LocalDateTime.now();
        return new Rental.Builder()
                .setUser(user)
                .setCar(carToRent)
                .setDriver(driver)
                .setIssuer(issuerId)
                .setIssuedDate(pickupTime)
                .setExpectedReturnDate(expectedReturnDate)
                .setReturnedDate(endDate)
                .setStatus(status)
                .build();
    }
}
