package az.ac.cput.repository;

import az.ac.cput.domain.Insurance;

import java.util.List;

public interface IInsuranceRepository extends IRepository <Insurance, Integer>{
    List<Insurance> getAllInsurancePolicies();
    Insurance getInsuranceById(Integer id);

}
