package za.ac.cput.repository;

import za.ac.cput.domain.Insurance;

import java.util.List;

public interface IInsuranceRepository extends IRepository <Insurance, Integer>{
    List<Insurance> getAllInsurancePolicies();
    Insurance getInsuranceById(Integer id);

}
