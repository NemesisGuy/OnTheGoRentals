package za.ac.cput.repository;

import za.ac.cput.domain.impl.Rental;

import java.util.List;

/**
 * Lonwabo Magazi-218331851
 * Date: March 2023
 * IRentalRepository.java
 */

public interface IRentalRepository extends IRepository<Rental, Integer> {

    Rental getRentalById(Integer id);

    List<Rental> getAllRentals();

}

