package za.ac.cput.factory;

import za.ac.cput.domain.Insurance;
import za.ac.cput.scratch.Rental;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * InsuranceFactory.java
 * Class for the InsuranceFactory
 * Author: Aqeel Hanslo (219374422)
 * Date: 30 March 2023
 */

public class InsuranceFactory implements IFactory<Insurance>{

    public static Insurance createInsurance(String insuranceType, double insuranceAmount, LocalDate insuranceCoverageStartDate, LocalDate insuranceCoverageEndDate, Rental rentalId) {

        String id = generateId();

        return new Insurance.Builder()
                .setInsuranceId(id)
                .setInsuranceType(insuranceType)
                .setInsuranceAmount(insuranceAmount)
                .setInsuranceCoverageStartDate(insuranceCoverageStartDate)
                .setInsuranceCoverageEndDate(insuranceCoverageEndDate)
                .build();
    }

    public static String generateId() {
        return UUID.randomUUID().toString();
    }

    @Override
    public Insurance create() {
        return null;
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
