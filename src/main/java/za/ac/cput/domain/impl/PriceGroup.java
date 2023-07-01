package za.ac.cput.domain.impl;
/**
 * FileName: PriceGroup.java
 * Author: Group
 * Purpose: PriceGroup enum for the different price groups of the vehicles
 * Date: 10 June 2023
 * Version: 1.0
 *
 * **/

import java.io.Serializable;
import java.util.Arrays;
public enum PriceGroup implements Serializable {
    ECONOMY(550.00),
    STANDARD(650.00),
    LUXURY(800.00),
    PREMIUM(3000.00),
    EXOTIC(20000.00),
    SPECIAL(450.00),
    OTHER(700.00),
    NONE(0.00);

    private double rentalPrice;

    PriceGroup(double rentalPrice) {
        this.rentalPrice = rentalPrice;
    }

    public double getRentalPrice() {
        return rentalPrice;
    }

    public static PriceGroup fromString(String value) {
        return Arrays.stream(PriceGroup.values())
                .filter(group -> group.name().equalsIgnoreCase(value))
                .findFirst()
                .orElse(null);
    }
}
