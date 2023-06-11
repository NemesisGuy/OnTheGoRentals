package za.ac.cput.factory.impl;
/**
 * PaymentFactory.java
 * Class for the Payment Factory
 * Author: Aqeel Hanslo (219374422)
 * Date:  06 April 2023
 */

import za.ac.cput.domain.impl.Payment;
import za.ac.cput.factory.IFactory;

import java.util.List;
import java.util.Random;

public class PaymentFactory implements IFactory<Payment> {

    @Override
    public Payment create() {
        return new Payment.Builder()
                .setPaymentId(new Random().nextInt(1000000))
                .build();
    }


    public Payment getById(long id) {
        return null;
    }


    public Payment update(Payment entity) {
        return null;
    }


    public boolean delete(Payment entity) {
        return false;
    }


    public List<Payment> getAll() {
        return null;
    }


    public long count() {
        return 0;
    }


    public Class<Payment> getType() {
        return null;
    }
}
