package az.ac.cput.repository;

import az.ac.cput.domain.Insurance;

import java.util.*;

public class IInsuranceRepositoryImpl implements IInsuranceRepository {
    private List<Insurance> insuranceDB;
    private static IInsuranceRepositoryImpl repository = null;

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
        Insurance insurance = insuranceDB.stream().filter(e -> e.getInsuranceId()==id)
                .findAny().orElse(null);
        return insurance;
    }

    @Override
    public Insurance update(Insurance insurance) {
        Insurance old = read(insurance.getInsuranceId());
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
