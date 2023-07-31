package za.ac.cput.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.ac.cput.domain.Insurance;
import za.ac.cput.repository.IInsuranceRepository;
import za.ac.cput.service.IInsuranceService;

import java.util.List;

@Service
public class IInsuranceServiceImpl implements IInsuranceService {

    private IInsuranceRepository repository;

    @Autowired
    private IInsuranceServiceImpl(IInsuranceRepository repository) {
        this.repository = repository;
    }

    @Override
    public Insurance create(Insurance insurance) {
        return this.repository.save(insurance);
    }

    @Override
    public Insurance read(Integer id) {
        return this.repository.findById(id).orElse(null);
    }

    @Override
    public Insurance update(Insurance insurance) {
        if (this.repository.existsById(insurance.getId())) {
            return this.repository.save(insurance);
        }
        return null;
    }

    @Override
    public boolean delete(Integer integer) {
        if (this.repository.existsById(integer)) {
            this.repository.deleteById(integer);
            return true;
        }
        return false;
    }

    public List<Insurance> getAllInsurancePolicies() {
        return this.repository.findAll();
    }

    public List<Insurance> findAllByInsuranceType(String insuranceType) {
        return this.repository.findAllByInsuranceType(insuranceType);
    }
}
