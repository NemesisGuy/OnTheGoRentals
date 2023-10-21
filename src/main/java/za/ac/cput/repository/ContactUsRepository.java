package za.ac.cput.repository;
/**
 * Author: Cwenga Dlova (214310671)
 * Date: 23/09/2023
 * */

import org.springframework.data.jpa.repository.JpaRepository;
import za.ac.cput.domain.ContactUs;

import java.util.Optional;

public interface ContactUsRepository extends JpaRepository<ContactUs, Integer> {
    //Optional<Object> findById();
}
