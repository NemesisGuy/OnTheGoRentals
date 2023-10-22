package za.ac.cput.factory.impl;

import org.springframework.stereotype.Component;
import za.ac.cput.domain.Car;
import za.ac.cput.domain.Rental;
/*import za.ac.cput.domain.User;*/
import za.ac.cput.factory.IFactory;
import za.ac.cput.domain.security.User;

import java.time.LocalDateTime;
import java.util.Random;

/**
 * Pete Buckingham - 220165289
 * Date: April 2023
 * RentalFactory Class.java
 */
@Component
public class RentalFactory implements IFactory<Rental> {
    public Rental create(int id, User user, Car car, int issuer, int receiver, int fine, LocalDateTime issuedDate, LocalDateTime returnedDate) {

        return new Rental.Builder()
                .setId(id)
                .setUser(user)
                .setCar(car)
                .setIssuer(issuer)
                .setReceiver(receiver)
                .setFine(fine)
                .setIssuedDate(issuedDate)
                .setDateReturned(returnedDate)
                .build();
    }
    public Rental create() {
        return new Rental.Builder()
                .setId(new Random().nextInt(1000000))
                .build();
    }

    public Rental create(Rental rental) {
        return new Rental.Builder()
                .copy(rental)
                .build();
    }
}


