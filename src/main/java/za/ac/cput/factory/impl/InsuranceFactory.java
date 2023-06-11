package za.ac.cput.factory.impl;
/**
 * InsuranceFactory.java
 * Class for the Insurance Factory
 * Author: Aqeel Hanslo (219374422)
 * Date:  06 April 2023
 */

import za.ac.cput.domain.impl.Insurance;
import za.ac.cput.domain.impl.Payment;
import za.ac.cput.domain.impl.Rental;
import za.ac.cput.factory.IFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

public class InsuranceFactory implements IFactory<Insurance> {

    public static Insurance createInsurance(String insuranceType, Double insuranceAmount, LocalDate insuranceCoverageStartDate, LocalDate insuranceCoverageEndDate, Rental rentalId) {
        return new Insurance.Builder()
                .setInsuranceId(new Random().nextInt(1000000))
                .setInsuranceType(insuranceType)
                .setInsuranceAmount(insuranceAmount)
                .setInsuranceCoverageStartDate(insuranceCoverageStartDate)
                .setInsuranceCoverageEndDate(insuranceCoverageEndDate)
                .setRentalId(rentalId)
                .build();
    }

    @Override
    public Insurance create() {
        return new Insurance.Builder()
                .setInsuranceId(new Random().nextInt(1000000))
                .build();
    }
}
