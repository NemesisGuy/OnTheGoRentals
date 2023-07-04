package za.ac.cput.factory.impl;

import org.springframework.stereotype.Component;
import za.ac.cput.domain.impl.Car;
import za.ac.cput.domain.impl.Rental;
import za.ac.cput.domain.impl.User;

import java.time.LocalDateTime;
import java.util.Random;

/**
 * Lonwabo Magazi-218331851
 * Date: March 2023
 * RentalFactory Class.java
 */
@Component
public class RentalFactory {

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
   /* @Entity
    public class Rental implements IRent {
        @jakarta.persistence.Id
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private int rentalId;
        @ManyToOne
        @JoinColumn(name = "userId")
        private int user;
        @ManyToOne
        @JoinColumn(name = "carId")
        private int car;
        private int issuer;
        private int receiver;
        private int fine;
        private LocalDateTime issuedDate;
        private LocalDateTime  returnedDate;
*/


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
}


