package za.ac.cput.service;

import za.ac.cput.domain.impl.Insurance;

import java.util.List;

public interface IInsuranceService extends IService<Insurance, Integer> {
    List<Insurance> getAllInsurancePolicies();

    Insurance getInsuranceById(Integer id);
}
