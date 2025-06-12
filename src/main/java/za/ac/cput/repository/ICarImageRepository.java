package za.ac.cput.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import za.ac.cput.domain.entity.CarImage;

import java.util.UUID;

public interface ICarImageRepository extends JpaRepository<CarImage, UUID> {
}