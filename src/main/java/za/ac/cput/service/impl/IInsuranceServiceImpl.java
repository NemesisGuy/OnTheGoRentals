package za.ac.cput.service.impl;

import org.springframework.stereotype.Service;
import za.ac.cput.domain.impl.Insurance;
import za.ac.cput.repository.impl.IInsuranceRepositoryImpl;
import za.ac.cput.service.IInsuranceService;

import java.util.List;
@Service
public class IInsuranceServiceImpl implements IInsuranceService {
    private static IInsuranceServiceImpl service;
    private static IInsuranceRepositoryImpl repository;

    private IInsuranceServiceImpl() {
        repository = IInsuranceRepositoryImpl.getRepository();
    }

    public static IInsuranceServiceImpl getInsuranceService() {
        if (service == null) {
            service = new IInsuranceServiceImpl();
        }
        return service;
    }

    @Override
    public Insurance create(Insurance account) {
        Insurance created = repository.create(account);
        return created;
    }

    @Override
    public Insurance read(Integer id) {
        Insurance read = repository.read(id);
        return read;
    }

    @Override
    public Insurance update(Insurance account) {
        Insurance updated = repository.update(account);
        return updated;
    }

    @Override
    public boolean delete(Integer id) {
        boolean success = repository.delete(id);
        return success;
    }

    @Override
    public List<Insurance> getAllInsurancePolicies() {
        return repository.getAllInsurancePolicies();
    }

    @Override
    public Insurance getInsuranceById(Integer id) {
        Insurance getById = repository.getInsuranceById(id);
        return getById;
    }
}
