package za.ac.cput.factory;
/**
 * PaymentFactory.java
 * Class for the Payment Factory
 * Author: Aqeel Hanslo (219374422)
 * Date:  06 April 2023
 */

import za.ac.cput.domain.Payment;

import java.util.List;
import java.util.Random;

public class PaymentFactory implements IFactory<Payment>{

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
