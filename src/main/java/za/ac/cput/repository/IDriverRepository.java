package za.ac.cput.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.ac.cput.domain.Driver;
import za.ac.cput.domain.Rental;

@Repository
public interface IDriverRepository extends JpaRepository<Driver, Integer> {

}
