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
    //this is to enumerate the price groups, for grouping the different vehicles in the fleet
    //this is to be used in the Vehicle class
    ECONOMY, STANDARD, LUXURY, PREMIUM, EXOTIC, SPECIAL, OTHER, NONE;

    public static PriceGroup fromString(String value) {
        return Arrays.stream(PriceGroup.values())
                .filter(group -> group.name().equalsIgnoreCase(value))
                .findFirst()
                .orElse(null);
    }
}
