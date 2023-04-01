package za.ac.cput.factory;

import za.ac.cput.domain.Payment;
import za.ac.cput.scratch.Rental;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * PaymentFactory.java
 * Class for the PaymentFactory
 * Author: Aqeel Hanslo (219374422)
 * Date: 30 March 2023
 */

public class PaymentFactory implements IFactory<Payment>{

    public static Payment createPayment(double paymentAmount, String paymentMethod, LocalDate paymentDate, Rental rentalId) {

        String id = generateId();

        return new Payment.Builder()
                .setPaymentId(id)
                .setPaymentAmount(paymentAmount)
                .setPaymentMethod(paymentMethod)
                .setPaymentDate(paymentDate)
                .setRentalId(rentalId)
                .build();
    }

    public static String generateId() {
        return UUID.randomUUID().toString();
    }

    // Implementing iFactory methods
    @Override
    public Payment create() {
        return null;
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
