package za.ac.cput.repository;
/**
 * CarRepository.java
 * Interface for the CarRepository
 * Author: Peter Buckingham (220165289)
 * Date: 29 March 2021
 */

import org.springframework.data.jpa.repository.JpaRepository;
import za.ac.cput.domain.entity.Car;
import za.ac.cput.domain.enums.PriceGroup;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
//O R M

public interface CarRepository extends JpaRepository<Car, Integer> {
    /**
     * Finds all cars that are available and not deleted.
     * * @return A list of available {@link Car} entities that are not marked as deleted.
     * * This method retrieves all cars that are currently available for booking or rental,
     * * excluding those that have been soft-deleted.
     */
    List<Car> findByPriceGroupAndDeletedFalse(PriceGroup priceGroup);
    //List<Car> findAvailableCars();

    /// is car by id available
    /**
     * Checks if a car with the given ID is available and not deleted.
     * * @param id The ID of the car to check.
     * * @return {@code true} if the car is available and not deleted, {@code false} otherwise.
     * * This method is used to determine if a specific car can be booked or rented based on its availability status.
     */
    boolean existsByIdAndAvailableTrue(int id);

    /**
     * Checks if a car with the given ID is available and not deleted.
     * * @param id The ID of the car to check.
     * * @return {@code true} if the car is available and not deleted, {@code false} otherwise.
     * * This method is used to determine if a specific car can be booked or rented based on its availability status.
     */
    boolean existsByIdAndAvailableTrueAndDeletedFalse(int id);

    /**
     * Finds all cars that are not marked as deleted.
     * * @return A list of all {@link Car} entities that are not marked as deleted.
     * * This method retrieves all cars from the database, excluding those that have been soft-deleted.
     */
    List<Car> findByDeletedFalse();

    /**
     * Finds all cars that are marked as deleted.
     * * @return A list of all {@link Car} entities that are marked as deleted.
     * * This method retrieves all cars from the database that have been soft-deleted.
     */
    List<Car> findByDeletedTrue();

    /**
     * Finds a car by its UUID and checks if it is not deleted.
     * * @param id The UUID of the car to find.
     * * @return An {@link Optional} containing the {@link Car} entity if found and not deleted,
     * * otherwise an empty Optional.
     */
    Optional<Car> findByUuidAndDeletedFalse(UUID id);

    /**
     * Finds a car by its UUID.
     * * @param uuid The UUID of the car to find.
     * * @return An {@link Optional} containing the {@link Car} entity if found,
     * * otherwise an empty Optional.
     */
    Optional<Car> findByUuid(UUID uuid);

    /**
     * Finds all cars that are available and not deleted.
     * * @return A list of available {@link Car} entities that are not marked as deleted.
     * * This method retrieves all cars that are currently available for booking or rental,
     * * excluding those that have been soft-deleted.
     */
    List<Car> findAllByAvailableTrueAndDeletedFalse();

    /**
     * Finds all cars that are available, not deleted, and belong to a specific category.
     * * @param category The category of the cars to find.
     * * @return A list of available {@link Car} entities that are not marked as deleted and belong to the specified category.
     */
    List<Car> findAllByAvailableTrueAndDeletedFalseAndCategory(String category);

    /**
     * Finds all cars that are available, not deleted, and belong to a specific price group.
     * * @param priceGroup The price group of the cars to find.
     * * @return A list of available {@link Car} entities that are not marked as deleted and belong to the specified price group.
     */
    List<Car> findAllByAvailableTrueAndDeletedFalseAndPriceGroup(PriceGroup priceGroup);

    /**
     * Finds a car by its ID and checks if it is not deleted.
     * * @param id The ID of the car to find.
     * * @return An {@link Optional} containing the {@link Car} entity if found and not deleted,
     * * otherwise an empty Optional.
     */
    Optional<Car> findByIdAndDeletedFalse(Integer id);

    /**
     * Finds all cars that are available and NOT in the provided list of IDs.
     * This is used to filter out cars that are already booked for a specific period.
     *
     * @param excludedCarIds A list of car IDs to exclude from the result.
     * @return A list of available cars.
     */

    // Keep this one for the date-only search
    List<Car> findByAvailableTrueAndDeletedFalseAndIdNotIn(List<Integer> excludedCarIds);

    // --- NEW METHODS FOR COMBINED FILTERS ---

    /**
     * Finds available cars of a specific category, excluding those with conflicting bookings.
     */
    List<Car> findByAvailableTrueAndDeletedFalseAndCategoryAndIdNotIn(String category, List<Integer> excludedCarIds);

    /**
     * Finds available cars of a specific price group, excluding those with conflicting bookings.
     */
    List<Car> findByAvailableTrueAndDeletedFalseAndPriceGroupAndIdNotIn(PriceGroup priceGroup, List<Integer> excludedCarIds);
}