package za.ac.cput.service;

import za.ac.cput.domain.Driver;

import java.util.List;

public interface IDriverService {
    Driver create (Driver driver);
    public Driver read(Integer id);
    Driver update (Driver driver);
    boolean delete(Integer id);
    List<Driver> getAll();

}
