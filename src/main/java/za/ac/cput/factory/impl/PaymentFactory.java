package za.ac.cput.factory.impl;
/**
 * PaymentFactory.java
 * Class for the Payment Factory
 * Author: Aqeel Hanslo (219374422)
 * Date:  06 April 2023
 */

import za.ac.cput.domain.impl.Payment;
import za.ac.cput.domain.impl.Rental;
import za.ac.cput.factory.IFactory;

import java.time.LocalDate;
import java.util.Random;

public class PaymentFactory implements IFactory<Payment> {

    public static Payment createPayment(Double paymentAmount, String paymentMethod, LocalDate paymentDate, Rental rentalId) {
        return new Payment.Builder()
                .setPaymentId(new Random().nextInt(1000000))
                .setPaymentAmount(paymentAmount)
                .setPaymentMethod(paymentMethod)
                .setPaymentDate(paymentDate)
                .setRentalId(rentalId)
                .build();
    }

    @Override
    public Payment create() {
        return new Payment.Builder()
                .setPaymentId(new Random().nextInt(1000000))
                .build();
    }
}
