package za.ac.cput.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import za.ac.cput.domain.impl.Insurance;
import za.ac.cput.domain.impl.Rental;


import java.time.LocalDate;

class InsuranceTest {
    private Insurance insurance;
    private String insuranceType = "Collision Damage Waiver";
    private double insuranceAmount = 42000.0;
    private LocalDate insuranceCoverageStartDate = LocalDate.parse("2022-01-01");
    private LocalDate insuranceCoverageEndDate = LocalDate.parse("2023-12-31");
    private Rental rentalId = null;

    @Test
    public void testInsurance() {

        insurance = new Insurance.Builder()
                .setInsuranceType(insuranceType)
                .setInsuranceAmount(insuranceAmount)
                .setInsuranceCoverageStartDate(insuranceCoverageStartDate)
                .setInsuranceCoverageEndDate(insuranceCoverageEndDate)
                .setRentalId(rentalId)
                .build();

        System.out.println(insurance.toString());

        Assertions.assertEquals(insuranceType, insurance.getInsuranceType());
        Assertions.assertEquals(insuranceAmount, insurance.getInsuranceAmount());
        Assertions.assertEquals(insuranceCoverageStartDate, insurance.getInsuranceCoverageStartDate());
        Assertions.assertEquals(insuranceCoverageEndDate, insurance.getInsuranceCoverageEndDate());
        Assertions.assertEquals(rentalId, insurance.getRentalId());
    }

}