package za.ac.cput.repository;
/**
 * Author: Cwenga Dlova (214310671)
 * Date: 23/09/2023
 */

import org.springframework.data.jpa.repository.JpaRepository;
import za.ac.cput.domain.entity.ContactUs;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ContactUsRepository extends JpaRepository<ContactUs, Integer> {
    List<ContactUs> findByDeletedFalse();

    Optional<ContactUs> findByIdAndDeletedFalse(int id);

    Optional<ContactUs> findByUuidAndDeletedFalse(UUID uuid);

    boolean existsByIdAndDeletedFalse(Integer submissionId);
    //Optional<Object> findById();
}
