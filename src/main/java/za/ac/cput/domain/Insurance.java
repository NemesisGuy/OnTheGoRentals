package za.ac.cput.domain;
/**
 * Insurance.java
 * Class for the Insurance
 * Author: Aqeel Hanslo (219374422)
 * Date: 30 March 2023
 */


import za.ac.cput.scratch.Rental;

import java.time.LocalDate;
import java.util.Objects;

public class Insurance implements IInsurance {
    private int insuranceId;
    private String insuranceType;
    private double insuranceAmount;
    private LocalDate insuranceCoverageStartDate;
    private LocalDate insuranceCoverageEndDate;
    private Rental rentalId;

    public Insurance(Builder builder) {
        this.insuranceId = builder.insuranceId;
        this.insuranceType = builder.insuranceType;
        this.insuranceAmount = builder.insuranceAmount;
        this.insuranceCoverageStartDate = builder.insuranceCoverageStartDate;
        this.insuranceCoverageEndDate = builder.insuranceCoverageEndDate;
        this.rentalId = builder.rentalId;
    }

    public int getInsuranceId() {
        return insuranceId;
    }

    public String getInsuranceType() {
        return insuranceType;
    }

    public double getInsuranceAmount() {
        return insuranceAmount;
    }

    public LocalDate getInsuranceCoverageStartDate() {
        return insuranceCoverageStartDate;
    }

    public LocalDate getInsuranceCoverageEndDate() {
        return insuranceCoverageEndDate;
    }

    public Rental getRentalId() {
        return rentalId;
    }

    @Override
    public String toString() {
        return "Insurance{" +
                "insuranceId=" + insuranceId +
                ", insuranceType='" + insuranceType + '\'' +
                ", insuranceAmount=" + insuranceAmount +
                ", insuranceCoverageStartDate=" + insuranceCoverageStartDate +
                ", insuranceCoverageEndDate=" + insuranceCoverageEndDate +
                ", rentalId=" + rentalId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Insurance insurance = (Insurance) o;
        return insuranceId == insurance.insuranceId && Double.compare(insurance.insuranceAmount, insuranceAmount) == 0 && Objects.equals(insuranceType, insurance.insuranceType) && Objects.equals(insuranceCoverageStartDate, insurance.insuranceCoverageStartDate) && Objects.equals(insuranceCoverageEndDate, insurance.insuranceCoverageEndDate) && Objects.equals(rentalId, insurance.rentalId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(insuranceId, insuranceType, insuranceAmount, insuranceCoverageStartDate, insuranceCoverageEndDate, rentalId);
    }

    public static class Builder {
        private int insuranceId;
        private String insuranceType;
        private double insuranceAmount;
        private LocalDate insuranceCoverageStartDate;
        private LocalDate insuranceCoverageEndDate;
        private Rental rentalId;

        public Builder setInsuranceId(int insuranceId) {
            this.insuranceId = insuranceId;
            return this;
        }

        public Builder setInsuranceType(String insuranceType) {
            this.insuranceType = insuranceType;
            return this;
        }

        public Builder setInsuranceAmount(double insuranceAmount) {
            this.insuranceAmount = insuranceAmount;
            return this;
        }

        public Builder setInsuranceCoverageStartDate(LocalDate insuranceCoverageStartDate) {
            this.insuranceCoverageStartDate = insuranceCoverageStartDate;
            return this;
        }

        public Builder setInsuranceCoverageEndDate(LocalDate insuranceCoverageEndDate) {
            this.insuranceCoverageEndDate = insuranceCoverageEndDate;
            return this;
        }

        public Builder setRentalId(Rental rentalId) {
            this.rentalId = rentalId;
            return this;
        }

        public Builder copy(Insurance insurance) {
            this.insuranceId = insurance.insuranceId;
            this.insuranceType = insurance.insuranceType;
            this.insuranceAmount = insurance.insuranceAmount;
            this.insuranceCoverageStartDate = insurance.insuranceCoverageStartDate;
            this.insuranceCoverageEndDate = insurance.insuranceCoverageEndDate;
            this.rentalId = insurance.rentalId;
            return this;
        }

        public Insurance build() {
            return new Insurance(this);
        }
    }
}
