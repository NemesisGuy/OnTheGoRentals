package za.ac.cput.factory;
/**
 * InsuranceFactory.java
 * Class for the Insurance Factory
 * Author: Aqeel Hanslo (219374422)
 * Date:  06 April 2023
 */

import za.ac.cput.domain.Insurance;

import java.util.List;
import java.util.Random;

public class InsuranceFactory implements IFactory<Insurance>{

    @Override
    public Insurance create() {
        return new Insurance.Builder()
                .setInsuranceId(new Random().nextInt(1000000))
                .build();
    }

    @Override
    public Insurance getById(long id) {
        return null;
    }

    @Override
    public Insurance update(Insurance entity) {
        return null;
    }

    @Override
    public boolean delete(Insurance entity) {
        return false;
    }

    @Override
    public List<Insurance> getAll() {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public Class<Insurance> getType() {
        return null;
    }
}
