package za.ac.cput.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.ac.cput.domain.Driver;
import za.ac.cput.repository.IDriverRepository;
import za.ac.cput.service.IDriverService;

import java.util.List;
import java.util.UUID;

@Service
public class DriverServiceImpl implements IDriverService {
    private final IDriverRepository repository;

    @Autowired
    private DriverServiceImpl(IDriverRepository repository) {
        this.repository = repository;
    }

    @Override
    public Driver create(Driver driver) {
        return this.repository.save(driver);
    }

    @Override
    public Driver read(Integer id) {
        return this.repository.findByIdAndDeletedFalse(id).orElse(null);
    }

    @Override
    public Driver update(Driver driver) {
        if (this.repository.existsById(driver.getId())) {
            return this.repository.save(driver);
        }
        return null;
    }

    @Override
    public boolean delete(Integer id) {
        Driver driver = this.repository.findById(id).orElse(null);
        if (driver != null && !driver.isDeleted()) {
            driver = new Driver.Builder().copy(driver).setDeleted(true).build();
            this.repository.save(driver);
            return true;
        }

        return false;
    }

    @Override
    public boolean delete(UUID uuid) {
        Driver driver = this.repository.findByUuidAndDeletedFalse(uuid).orElse(null);
        if (driver != null) {
            driver = new Driver.Builder().copy(driver).setDeleted(true).build();
            this.repository.save(driver);
            return true;
        }
        return false;
    }

    @Override
    public List<Driver> getAll() {
        return this.repository.findByDeletedFalse();
    }

    @Override
    public Driver read(UUID uuid) {
        return this.repository.findByUuidAndDeletedFalse(uuid).orElse(null);
    }

}
