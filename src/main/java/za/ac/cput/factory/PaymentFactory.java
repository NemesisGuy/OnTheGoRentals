package za.ac.cput.factory;

import za.ac.cput.domain.Payment;
import za.ac.cput.scratch.Rental;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class PaymentFactory implements IFactory<Payment>{

/*
--> Commented out sirs method

    public static Payment createPayment(double paymentAmount, String paymentMethod, LocalDate paymentDate, Rental rentalId) {

        int id = generateId();

        return new Payment.Builder()
                .setPaymentId(id)
                .setPaymentAmount(paymentAmount)
                .setPaymentMethod(paymentMethod)
                .setPaymentDate(paymentDate)
                .setRentalId(rentalId)
                .build();
    }

    public static int generateId() {
        return new Random().nextInt(1000000);
    }
*/

    // Implementing iFactory methods
    @Override
    public Payment create() {
        return new Payment.Builder()
                .setPaymentId(new Random().nextInt(1000000))
                .build();
    }

    @Override
    public Payment getById(long id) {
        return null;
    }

    @Override
    public Payment update(Payment entity) {
        return null;
    }

    @Override
    public boolean delete(Payment entity) {
        return false;
    }

    @Override
    public List<Payment> getAll() {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public Class<Payment> getType() {
        return null;
    }
}
