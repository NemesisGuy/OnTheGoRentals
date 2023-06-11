package za.ac.cput.repository.impl;
/**
 * IInsuranceRepositoryImpl.java
 * Class implementation for the Insurance Repository
 * Author: Aqeel Hanslo (219374422)
 * Date:  06 April 2023
 */

import za.ac.cput.domain.impl.Insurance;
import za.ac.cput.repository.IInsuranceRepository;

import java.util.ArrayList;
import java.util.List;

public class IInsuranceRepositoryImpl implements IInsuranceRepository {
    private static IInsuranceRepositoryImpl repository = null;
    private List<Insurance> insuranceDB;

    private IInsuranceRepositoryImpl() {
        insuranceDB = new ArrayList<>();
    }

    // Singleton
    public static IInsuranceRepositoryImpl getRepository() {
        if (repository == null) {
            repository = new IInsuranceRepositoryImpl();
        }
        return repository;
    }

    @Override
    public Insurance create(Insurance insurance) {
        insuranceDB.add(insurance);
        return insurance;
    }

    @Override
    public Insurance read(Integer id) {
        Insurance insurance = insuranceDB.stream().filter(e -> e.getId() == id)
                .findAny().orElse(null);
        return insurance;
    }

    @Override
    public Insurance update(Insurance insurance) {
        Insurance old = read(insurance.getId());
        if (old != null) {
            insuranceDB.remove(old);
            insuranceDB.add(insurance);
            return insurance;
        }
        return null;
    }

    @Override
    public boolean delete(Integer id) {
        Insurance insuranceToDelete = read(id);
        if (insuranceToDelete != null) {
            insuranceDB.remove(insuranceToDelete);
            return true;
        }
        return false;
    }

    @Override
    public List<Insurance> getAllInsurancePolicies() {
        return insuranceDB;
    }

    @Override
    public Insurance getInsuranceById(Integer id) {
        return read(id);
    }
}
