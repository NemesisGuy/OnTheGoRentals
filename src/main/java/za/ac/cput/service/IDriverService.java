package za.ac.cput.service;

import za.ac.cput.domain.Driver;

import java.util.List;
import java.util.UUID;

public interface IDriverService {
    Driver create(Driver driver);

    Driver read(Integer id);

    Driver update(Driver driver);

    boolean delete(Integer id);
    boolean delete(UUID uuid);

    List<Driver> getAll();


    Driver read(UUID Uuid);
}
