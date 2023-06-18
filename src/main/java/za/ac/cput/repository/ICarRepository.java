package za.ac.cput.repository;
/**
 * ICarRepository.java
 * Interface for the ICarRepository
 * Author: Peter Buckingham (220165289)
 * Date: 29 March 2021
 */

import org.springframework.data.jpa.repository.JpaRepository;
import za.ac.cput.domain.impl.Car;




public interface ICarRepository extends JpaRepository<Car, Integer> {



}