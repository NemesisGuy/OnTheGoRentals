package za.ac.cput.repository;


import za.ac.cput.domain.Insurance;

import java.util.List;

public interface IInsuranceRepository extends IRepository <Insurance, String>{
    List<Insurance> getAllInsurancePolicies();
    Insurance getInsuranceById(String id);

}
