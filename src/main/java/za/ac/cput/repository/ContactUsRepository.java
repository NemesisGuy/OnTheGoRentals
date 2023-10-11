package za.ac.cput.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import za.ac.cput.domain.ContactUs;

import java.util.Optional;

public interface ContactUsRepository extends JpaRepository<ContactUs, Integer> {
    //Optional<Object> findById();
}
