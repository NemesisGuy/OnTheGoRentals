package za.ac.cput.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import za.ac.cput.domain.Driver;

public interface DriverRepository extends JpaRepository<Driver, Integer> {
    // You can add custom query methods here if needed
}